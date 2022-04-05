package io.github.samkelsey.webcrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;


public class LinkScraper {

    private final URL url;
    private final Logger log = LoggerFactory.getLogger(LinkScraper.class);

    public LinkScraper(URL url) {
        this.url = url;
    }

    public Document fetchPage() throws IOException {
        log.info("Starting to scrape: {}", url);
        return Jsoup.connect(url.toString()).get();
    }

    public Set<String> getValidLinks(Document page) {
        return page.select("a[href]").stream()
                .map(link -> link.attr("href"))
                .filter(this::isValidDomain)
                .collect(Collectors.toSet());
    }

    // TODO: Test this?
    public void logFoundLinks(Set<String> links) {
        log.info("Successfully scraped {} and found the following valid links: {}",
                url,
                links.toString()
        );
    }

    private boolean isValidDomain(String link) {
        boolean result = false;
        URL newUrl;

        try {
            newUrl = new URL(link);

            if (isSameDomain(newUrl)) {
                log.debug("Incorrect domain: {}", link);
                result = true;
            }
        } catch (MalformedURLException e) {
            log.debug("Invalid url: {}", link);
        }

        return result;
    }

    private boolean isSameDomain(URL newUrl) {
        return newUrl.getHost().equals(url.getHost())
                || newUrl.getHost().substring(4).equals(url.getHost())
                || newUrl.getHost().equals(url.getHost().substring(4));
    }
}
