package io.github.samkelsey.webcrawler;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class WebCrawlerTest {

    @ParameterizedTest
    @MethodSource("invalidArgumentsArrayProvider")
    void whenIncorrectArgumentsProvided_exceptionThrown(String[] args) {
        assertThrows(IllegalArgumentException.class, () -> WebCrawler.main(args));
    }

    private static Stream<Arguments> invalidArgumentsArrayProvider() {
        return Stream.of(
                Arguments.of((Object) new String[]{}),
                Arguments.of((Object) new String[]{"1", "2"}),
                Arguments.of((Object) new String[]{"monzo.com"}),
                Arguments.of((Object) new String[]{"fail"})
        );
    }

}
