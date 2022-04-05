package io.github.samkelsey.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WebCrawler {

    private final static ExecutorService executorService = Executors.newFixedThreadPool(3);
    private final static Set<String> visitedLinks = new HashSet<>();
    private static final Logger log = LoggerFactory.getLogger(WebCrawler.class);

    public void crawl(String startingLink) throws InterruptedException {
        log.info("Starting crawling");
        addLink(startingLink);

        // TODO: Timeout time needs thought through.
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

    // Add a new task to the executor
    public static void addLink(String link) {
        // TODO:
        //  Check link hasn't already been visited before adding.
        //  Add link to a set of already visited links.
        try {
            executorService.execute(new CrawlerThread(link));
        } catch (MalformedURLException e) {
            log.warn("Skipping adding link due to malformed url: {}", e.getMessage());
        }
    }
}
