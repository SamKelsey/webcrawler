package io.github.samkelsey.webcrawler;

import org.junit.jupiter.api.Test;

public class ThreadControllerTest {

    // When fail to submit task to executor, the null return isn't added to running tasks.
    @Test
    void whenBeginCrawlingSubmitFails_nullTaskNotQueued() {

    }

    @Test
    void whenBeginCrawlingTasksAndQueueEmpty_completes() {

    }

    @Test
    void whenBeginCrawlingCompletes_executorShutsDown() {

    }
}
