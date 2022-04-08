package io.github.samkelsey.webcrawler;

import io.github.samkelsey.webcrawler.crawler.LinkCrawler;
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
            ThreadController<LinkCrawler> controller = ThreadController.buildDefaultThreadController(startingLink);

            controller.beginCrawling();
        } catch (MalformedURLException e) {
            log.error("Invalid starting url provided {}", args[0]);
        }
    }
}
