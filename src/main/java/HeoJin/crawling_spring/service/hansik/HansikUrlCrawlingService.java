package HeoJin.crawling_spring.service.hansik;


import HeoJin.crawling_spring.service.util.CrawlingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class HansikUrlCrawlingService {

    private final CrawlingUtil crawlingUtil;

    @Value("${recipe.hansik.collection-name}")
    private String collectionName;

    @Value("${recipe.hansik.css-selector}")
    private String cssSelector;

    public void crawlRecipeUrls(String baseUrl, int startPage, int endPage) throws IOException {
        crawlingUtil.crawlWithPagination(baseUrl, startPage, endPage, cssSelector, collectionName);
    }


}
