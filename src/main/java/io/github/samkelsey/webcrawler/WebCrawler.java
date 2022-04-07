package io.github.samkelsey.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebCrawler {

    private static final Logger log = LoggerFactory.getLogger(WebCrawler.class);

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Invalid starting url argument provided.");
        }

        String startingLink = args[0];
        log.info("Starting crawl for {}", startingLink);
        ThreadController controller = ThreadController.buildDefaultThreadController(startingLink);

        controller.beginCrawling();
    }
}
