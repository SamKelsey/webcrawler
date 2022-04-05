package io.github.samkelsey.webcrawler;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

public class CrawlerThread extends Thread {

    private final String startingLink;
    private final LinkScraper linkScraper;
    private final Logger log = LoggerFactory.getLogger(CrawlerThread.class);

    public CrawlerThread(String startingLink) throws MalformedURLException {
        super();
        this.startingLink = startingLink;
        this.linkScraper = new LinkScraper(new URL(startingLink));
    }

    @Override
    public void run() {
        log.info("Crawling: {}", startingLink);

        try {
            Document page = linkScraper.fetchPage();
            Set<String> links = linkScraper.getValidLinks(page);
            linkScraper.logFoundLinks(links);

            // pass links to queue
            links.forEach(WebCrawler::addLink);

        } catch (IOException e) {
            // TODO: Be specific about what errors to catch and how to handle them.
            log.warn(e.getMessage());
        }
    }
}
