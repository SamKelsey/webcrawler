package io.github.samkelsey.webcrawler.crawler;

import java.net.URL;

/**
 * Similar to the {@link java.util.function.Supplier}, an interface for returning a result of a Crawler.
 */
@FunctionalInterface
public interface CrawlerSupplier {

    Crawler get(URL url);

}
