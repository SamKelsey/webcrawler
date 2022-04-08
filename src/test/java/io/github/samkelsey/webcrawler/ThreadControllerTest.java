package io.github.samkelsey.webcrawler;

import io.github.samkelsey.webcrawler.crawler.Crawler;
import io.github.samkelsey.webcrawler.crawler.CrawlerSupplier;
import io.github.samkelsey.webcrawler.crawler.LinkCrawler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ThreadControllerTest {


    @Test
    void whenBeginCrawling_everyTaskSubmittedToExecutor() throws Exception {
        ExecutorService mockExecutorService = mock(ExecutorService.class);
        Crawler mockCrawler = mock(LinkCrawler.class);
        when(mockCrawler.call())
                .thenReturn(TestUtils.getLinks())
                .thenReturn(Collections.emptySet());

        CrawlerSupplier<Crawler> supplier = (URL url) -> mockCrawler;
        URL rootUrl = new URL("https://www.monzo.com");

        ThreadController<Crawler> controller = new ThreadController<>(
                rootUrl,
                mockExecutorService,
                supplier
        );

        controller.beginCrawling();

        // Verify executor.submit is called for root and all of the getLinks links once each.
        ArgumentCaptor<Callable<Set<String>>> arguments = ArgumentCaptor.forClass(Callable.class);

        verify(mockExecutorService).submit(arguments.capture());


    }

    // When fail to submit task to executor, the null return isn't added to running tasks.
    @Test
    void whenExecutorSubmitFails_nullTaskNotQueued() {

    }

    @Test
    void whenTasksAndQueueEmpty_completes() {

    }

    @Test
    void whenBeginCrawlingCompletes_executorShutsDown() {

    }
}
