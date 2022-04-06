package io.github.samkelsey.webcrawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkScraperTest {

    // Check for duplicates and same domain only.
    @ParameterizedTest
    @ValueSource(strings = {"https://www.monzo.com", "https://monzo.com"})
    void whenGetValidLinks_shouldReturn(String startingUrl) throws IOException {
        LinkScraper linkScraper = new LinkScraper(new URL(startingUrl));

        Set<String> actual =  linkScraper.getValidLinks(createDocument());

        Set<String> expected = new HashSet<>(Arrays.asList(
                "https://www.monzo.com",
                "https://www.monzo.com/banking",
                "https://monzo.com/banking")
        );
        assertEquals(expected, actual);
    }

    // Checks if there is a link that begins with "/" then the root domain is correctly prepended.
    @ParameterizedTest
    @ValueSource(strings = {"https://www.monzo.com", "https://www.monzo.com/"})
    void whenGetValidLinks_shouldPrependDomain(String rootUrl) throws IOException {
        LinkScraper linkScraper = new LinkScraper(new URL(rootUrl));

        Set<String> actual =  linkScraper.getValidLinks(createDocument("/should-work"));

        Set<String> expected = new HashSet<>(Arrays.asList(
                "https://www.monzo.com/should-work")
        );
        assertEquals(expected, actual);
    }

    private Document createDocument(String... links) {
        Document doc = Document.createShell("test");
        Element body = doc.body();

        for (String s : links) {
            Element link = new Element("a").attr("href", s);
            body.appendChild(link);
        }

        return doc;
    }

    private Document createDocument() {
        return createDocument(
                "https://www.monzo.com",
                "https://www.monzo.com/banking",
                "https://monzo.com/banking",
                "https://monzo.com/banking",
                "https://fail.monzo.com/",
                "www.monzo.com/banking"
        );
    }
}
