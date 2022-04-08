package io.github.samkelsey.webcrawler.crawler;

import java.net.URL;

@FunctionalInterface
public interface CrawlerSupplier {

    Crawler get(URL url);

}
