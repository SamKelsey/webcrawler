package io.github.samkelsey.webcrawler;

import io.github.samkelsey.webcrawler.crawler.Crawler;
import io.github.samkelsey.webcrawler.crawler.CrawlerSupplier;
import io.github.samkelsey.webcrawler.crawler.LinkCrawler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Responsible for deploying Crawler threads to find new links.
 * @param <T> Type of crawler to use.
 */
public class ThreadController<T extends Crawler> {

    private final Logger log = LoggerFactory.getLogger(WebCrawler.class);

    private final Set<Future<Set<String>>> runningTasks = new HashSet<>();
    private final ExecutorService executorService;
    private final CrawlerSupplier<T> crawlerSupplier;
    private final Set<String> visitedLinks = new HashSet<>();
    private final URL rootUrl;

    private static final int DEFAULT_NUMBER_THREADS = 3;

    public ThreadController(URL rootUrl, ExecutorService executorService, CrawlerSupplier<T> crawlerSupplier) {
        this.rootUrl = rootUrl;
        this.executorService = executorService;
        this.crawlerSupplier = crawlerSupplier;
    }

    public static ThreadController<LinkCrawler> buildDefaultThreadController(URL rootUrl) {
        return new ThreadController<>(
                rootUrl,
                Executors.newFixedThreadPool(DEFAULT_NUMBER_THREADS),
                (URL link) -> new LinkCrawler(new LinkScraper(link))
        );
    }

    public void beginCrawling() throws InterruptedException {
        Future<Set<String>> task = addLinkToExecutor(rootUrl);
        runningTasks.add(task);
        Set<Set<String>> finishedTasks = pollFinishedTasks();

        while (finishedTasks.size() != 0 || runningTasks.size() != 0) {
            for (Set<String> finishedTask : finishedTasks) {
                finishedTask.forEach(rawLink -> {
                    try {
                        URL newUrl = new URL(rawLink);
                        Future<Set<String>> newTask = addLinkToExecutor(newUrl);
                        if (newTask == null) {
                            return;
                        }
                        runningTasks.add(newTask);
                    } catch (MalformedURLException e) {
                        log.warn("Skipping link due to malformed url: {}", rawLink);
                    }
                });
            }

            finishedTasks = pollFinishedTasks();
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        log.info("Crawling complete");
    }

    /**
     * Adds a thread to the executor for crawling the given link. Updates the crawled links set.
     * @param link Url to be crawled.
     * @return Task that is being processed by executor.
     */
    private Future<Set<String>> addLinkToExecutor(URL link) {
        if (visitedLinks.contains(link.toString())) {
            return null;
        }

        visitedLinks.add(link.toString());

        Callable<Set<String>> crawlerThread = crawlerSupplier.get(link);
        return executorService.submit(crawlerThread);
    }

    private Set<Set<String>> pollFinishedTasks() {
        Set<Set<String>> finishedTasks = runningTasks.stream()
                .filter(Future::isDone)
                .map(result -> {
                    try {
                        return result.get();
                    } catch (InterruptedException e) {
                        log.warn("Thread interrupted, returning partial results.");
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException e) {
                        log.warn("Failed to get results, skipping. {}", e.getMessage());
                    }

                    return new HashSet<String>();
                }).collect(Collectors.toSet());

        runningTasks.removeIf(Future::isDone);
        return finishedTasks;
    }

    // TODO: Is there a better way of doing this?
    //      - Can use .getConstructor(String.class).getInstance() but don't think I like this.
//    private Crawler createCrawler(LinkScraper linkScraper) {
//        if (crawlerSupplier == LinkCrawler.class) {
//            return new LinkCrawler(linkScraper);
//        } else {
//            throw new IllegalArgumentException(String.format("%s is not a recognised Crawler.", crawlerSupplier));
//        }
//    }
}
