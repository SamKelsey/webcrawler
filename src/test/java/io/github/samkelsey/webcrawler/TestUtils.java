package io.github.samkelsey.webcrawler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class TestUtils {

    private TestUtils() {

    }

    public static Set<String> getLinks() {
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
