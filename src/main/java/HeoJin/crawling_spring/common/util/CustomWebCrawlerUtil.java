package HeoJin.crawling_spring.common.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class CustomWebCrawlerUtil {

    public static List<String> extractHrefs(String url, String cssSelector) throws IOException {
        Document doc = connect(url);
        Elements elements = doc.select(cssSelector);

        return elements.stream()
                .map(element -> element.attr("href"))
                .collect(Collectors.toList());
    }

    public static Document connect(String url) throws IOException {
        return Jsoup.connect(url)
                .ignoreContentType(true)
                .userAgent("Mozilla/5.0")
                .get();
    }
}
