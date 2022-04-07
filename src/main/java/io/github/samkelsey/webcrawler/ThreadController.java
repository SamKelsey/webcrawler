package io.github.samkelsey.webcrawler;

import io.github.samkelsey.webcrawler.crawler.Crawler;
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
    private final Class<T> crawlerType;
    private final Set<String> visitedLinks = new HashSet<>();
    private final String rootUrl;

    public ThreadController(String rootUrl, ExecutorService executorService, Class<T> crawlerType) {
        this.rootUrl = rootUrl;
        this.executorService = executorService;
        this.crawlerType = crawlerType;
    }

    public static ThreadController<LinkCrawler> buildDefaultThreadController(String rootUrl) {
        return new ThreadController<>(
                rootUrl,
                Executors.newFixedThreadPool(3),
                LinkCrawler.class
        );
    }

    public void beginCrawling() throws InterruptedException {
        Future<Set<String>> task = addLinkToExecutor(rootUrl);
        runningTasks.add(task);
        Set<Set<String>> finishedTasks = pollFinishedTasks();

        while (finishedTasks.size() != 0 || runningTasks.size() != 0) {
            for (Set<String> finishedTask : finishedTasks) {
                finishedTask.forEach(newLink -> {
                    Future<Set<String>> newTask = addLinkToExecutor(newLink);
                    if (newTask == null) {
                        return;
                    }
                    runningTasks.add(newTask);
                });
            }

            finishedTasks = pollFinishedTasks();
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        log.info("Crawling complete");
    }

    /**
     * Adds a thread to the executor crawling the given link. Updates the crawled links set.
     * @param link Url to be crawled.
     * @return Task that is being processed by executor.
     * Null if it fails to submit the task to the executor.
     */
    private Future<Set<String>> addLinkToExecutor(String link) {
        if (visitedLinks.contains(link)) {
            return null;
        }

        visitedLinks.add(link);

        try {
            URL url = new URL(link);
            Callable<Set<String>> crawlerThread = createCrawler(new LinkScraper(url));
            return executorService.submit(crawlerThread);
        } catch (MalformedURLException e) {
            log.warn("Skipping link due to malformed url: {}", link);
            visitedLinks.remove(link);
        }

        return null;
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
    private Crawler createCrawler(LinkScraper linkScraper) {
        if (crawlerType == LinkCrawler.class) {
            return new LinkCrawler(linkScraper);
        } else {
            throw new IllegalArgumentException(String.format("%s is not a recognised Crawler.", crawlerType));
        }
    }
}
