package io.github.samkelsey.webcrawler.crawler;

import io.github.samkelsey.webcrawler.scraper.LinkScraper;
import io.github.samkelsey.webcrawler.scraper.Scraper;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Responsible for utilising the {@link LinkScraper} methods to find and return
 * link data.
 */
public class LinkCrawler extends Crawler {

    public LinkCrawler(Scraper scraper) {
        super(scraper);
    }

    @Override
    public Set<String> call() {
        log.info("Crawling: {}", startingLink);
        Set<String> foundLinks = new HashSet<>();

        try {
            Document page = scraper.fetchPage();
            foundLinks.addAll(scraper.getData(page));
        } catch (IOException e) {
            log.warn(
                    "Skipping link {}, due to error fetching: {}",
                    startingLink,
                    e.getMessage()
            );
        }

        return foundLinks;
    }
}
