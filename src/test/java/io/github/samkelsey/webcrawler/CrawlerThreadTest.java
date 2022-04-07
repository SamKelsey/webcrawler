package io.github.samkelsey.webcrawler;

import io.github.samkelsey.webcrawler.crawler.LinkCrawler;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CrawlerThreadTest {

    @Test
    void whenCall_shouldReturnAllLinks() throws Exception {
        LinkScraper mockScraper = mock(LinkScraper.class);
        when(mockScraper.getValidLinks(any())).thenReturn(getLinks());
        when(mockScraper.getUrl()).thenReturn(new URL("https://www.monzo.com"));

        Callable<Set<String>> thread = new LinkCrawler(mockScraper);
        Set<String> results = thread.call();

        assertEquals(results, getLinks());
    }

    @Test
    void whenCallException_shouldReturnEmptySet() throws Exception {
        LinkScraper mockScraper = mock(LinkScraper.class);
        when(mockScraper.getUrl()).thenReturn(new URL("https://www.monzo.com"));
        when(mockScraper.fetchPage()).thenThrow(new IOException("failure"));

        Callable<Set<String>> thread = new LinkCrawler(mockScraper);
        Set<String> results = thread.call();

        assertEquals(results, Collections.emptySet());
    }

    private Set<String> getLinks() {
        return new HashSet<>(Arrays.asList(
                "https://www.monzo.com",
                "https://www.monzo.com/banking",
                "https://monzo.com/banking",
                "https://monzo.com/banking",
                "https://fail.monzo.com/",
                "www.monzo.com/banking"
        ));
    }
}
