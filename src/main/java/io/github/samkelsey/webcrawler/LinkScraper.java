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

/**
 * Responsible for providing methods to scrape links
 * from a given url.
 */
public class LinkScraper {

    private final URL url;
    private final Logger log = LoggerFactory.getLogger(LinkScraper.class);

    public LinkScraper(URL url) {
        this.url = url;
    }

    public Document fetchPage() throws IOException {
        log.debug("Fetching page: {}", url);
        return Jsoup.connect(url.toString()).get();
    }

    public Set<String> getValidLinks(Document page) {
        Set<String> validLinks = page.select("a[href]").stream()
                .map(link -> link.attr("href"))
                .distinct()
                .map(this::prependDomain)
                .filter(this::isValidDomain)
                .collect(Collectors.toSet());

        log.info("Successfully scraped {} and found the following valid links: {}",
                url,
                validLinks
        );

        return validLinks;
    }

    private boolean isValidDomain(String link) {
        boolean result = true;
        URL newUrl;

        try {
            newUrl = new URL(link);

            if (!isSameDomain(newUrl.getHost())) {
                log.debug("Incorrect domain: {}, Root domain: {}", newUrl.getHost(), url.getHost());
                result = false;
            }
        } catch (MalformedURLException e) {
            log.debug("Invalid url: {}", link);
            result = false;
        }

        return result;
    }

    private String prependDomain(String link) {
        if (link.charAt(0) != '/') {

            return link;
        }
        String rootUrl = url.toString();
        return rootUrl.endsWith("/") ? rootUrl + link.substring(1) : rootUrl + link;
    }

    private boolean isSameDomain(String domain) {
        try {
            return domain.equals(url.getHost())
                    || domain.substring(4).equals(url.getHost())
                    || domain.equals(url.getHost().substring(4));
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public URL getUrl() {
        return this.url;
    }
}
