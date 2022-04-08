package io.github.samkelsey.webcrawler.crawler;

import io.github.samkelsey.webcrawler.LinkScraper;
import io.github.samkelsey.webcrawler.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LinkCrawlerTest {

    @Test
    void whenCall_shouldReturnAllLinks() throws Exception {
        LinkScraper mockScraper = mock(LinkScraper.class);
        when(mockScraper.getValidLinks(any())).thenReturn(TestUtils.getLinks());
        when(mockScraper.getUrl()).thenReturn(new URL("https://www.monzo.com"));

        Callable<Set<String>> thread = new LinkCrawler(mockScraper);
        Set<String> results = thread.call();

        assertEquals(results, TestUtils.getLinks());
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
}
