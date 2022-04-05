package io.github.samkelsey.webcrawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

        Set<String> actual =  linkScraper.getValidLinks(createSampleDocument());

        Set<String> expected = new HashSet<>(Arrays.asList(
                "https://www.monzo.com",
                "https://www.monzo.com/banking",
                "https://monzo.com/banking")
        );
        assertEquals(expected, actual);
    }

    private Document createSampleDocument() {
        Document doc = Document.createShell("test");
        Element body = doc.body();
        appendLink(body, "https://www.monzo.com");
        appendLink(body, "https://www.monzo.com/banking");
        appendLink(body, "https://monzo.com/banking");
        appendLink(body, "https://monzo.com/banking");
        appendLink(body, "www.monzo.com/banking");
        appendLink(body, "https://fail.monzo.com/");

        return doc;
    }

    private void appendLink(Element parent, String url) {
        Element link = new Element("a").attr("href", url);
        parent.appendChild(link);
    }

}
