package io.github.samkelsey.webcrawler.crawler;

import java.net.URL;

@FunctionalInterface
public interface CrawlerSupplier<T extends Crawler> {

    T get(URL url);

}
