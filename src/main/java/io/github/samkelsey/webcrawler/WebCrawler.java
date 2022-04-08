package io.github.samkelsey.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class WebCrawler {

    private static final Logger log = LoggerFactory.getLogger(WebCrawler.class);

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Invalid starting url argument provided.");
        }

        try {
            URL startingLink = new URL(args[0]);
            log.info("Starting crawl for {}", startingLink);
            ThreadController controller = ThreadController.buildDefaultThreadController(startingLink);

            boolean result = controller.beginCrawling();
            if (result) {
                log.info("Crawling successfully completed");
            } else {
                log.info("Executor did not successfully shutdown, some links may not have been scraped.");
            }
        } catch (MalformedURLException e) {
            log.error("Invalid starting url provided {}", args[0]);
        }
    }
}
