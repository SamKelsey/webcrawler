package io.github.samkelsey.webcrawler.crawler;

import io.github.samkelsey.webcrawler.LinkScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Base class to enforce a specific constructor.
 * Crawlers are responsible for using scraping methods to fetch necessary data from a given url.
 */
public abstract class Crawler implements Callable<Set<String>> {

    protected final String startingLink;
    protected final LinkScraper linkScraper;
    protected final Logger log = LoggerFactory.getLogger(LinkCrawler.class);

    public Crawler(LinkScraper linkScraper) {
        this.linkScraper = linkScraper;
        this.startingLink = linkScraper.getUrl().toString();
    }
}
