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

    @Value("${recipe.indexUrl.hansik.url}")
    private String baseUrl;

    @Value("${recipe.indexUrl.hansik.collection-name}")
    private String collectionName;

    @Value("${recipe.indexUrl.hansik.css-selector}")
    private String cssSelector;

    public void crawlRecipeUrls( int startPage, int endPage) throws IOException {
        crawlingUtil.crawlWithPaginationHanSik(baseUrl, startPage, endPage, cssSelector, collectionName);
    }


}
