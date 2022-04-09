package io.github.samkelsey.webcrawler.scraper;

import org.jsoup.nodes.Document;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Responsible for providing methods to scrape links
 * from a given url.
 */
public class LinkScraper extends Scraper {


    public LinkScraper(URL url) {
        super(url);
    }

    /**
     * Selects the href values of all links in the provided document.
     * @param page The document to be searched for links.
     * @return A set of the found href values.
     */
    public Set<String> getData(Document page) {
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

    /**
     * Checks if a link's href begins with a '/'.
     * If so, it prepends the root domain to the link, making it a valid url.
     * @param link Raw url to be checked/amended
     * @return The amended version of the provided url.
     */
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
}
