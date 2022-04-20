package io.github.samkelsey.webcrawler;

import io.github.samkelsey.webcrawler.crawler.Crawler;
import io.github.samkelsey.webcrawler.crawler.CrawlerSupplier;
import io.github.samkelsey.webcrawler.crawler.LinkCrawler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ThreadControllerTest {

    private static URL rootUrl;

    private static Crawler mockCrawler;
    private static CrawlerSupplier supplier;

    @BeforeAll
    public static void init() throws MalformedURLException {
        rootUrl = new URL("https://www.monzo.com");

        mockCrawler = mock(LinkCrawler.class);
        supplier = (URL url) -> mockCrawler;
    }

    @Test
    void whenBeginCrawling_everyTaskSubmittedToExecutor() throws Exception {
        when(mockCrawler.call())
                .thenReturn(TestUtils.getLinks())
                .thenReturn(Collections.emptySet());

        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        ExecutorService spyExecutor = spy(executorService);

        ThreadController controller = new ThreadController(rootUrl, spyExecutor, supplier);
        controller.beginCrawling();

        verify(spyExecutor, times(4)).submit(mockCrawler);
    }

    @Test
    void whenTaskException_emptySetReturned() throws Exception {
        when(mockCrawler.call()).thenThrow(ExecutionException.class);

        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        ExecutorService spyExecutor = spy(executorService);

        ThreadController controller = new ThreadController(rootUrl, spyExecutor, supplier);
        controller.beginCrawling();

        verify(spyExecutor, times(1)).submit(mockCrawler);
    }
}
