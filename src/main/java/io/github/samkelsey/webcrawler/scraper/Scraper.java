package io.github.samkelsey.webcrawler.scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public abstract class Scraper {

    protected final URL url;
    protected final Logger log = LoggerFactory.getLogger(LinkScraper.class);

    public Scraper(URL url) {
        this.url = url;
    }

    public Document fetchPage() throws IOException {
        log.debug("Fetching page: {}", url);
        return Jsoup.connect(url.toString()).get();
    }

    public URL getUrl() {
        return this.url;
    }

    public abstract Set<String> getData(Document page);
}
