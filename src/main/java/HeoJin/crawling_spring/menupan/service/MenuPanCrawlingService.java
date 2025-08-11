package HeoJin.crawling_spring.menupan.service;



import HeoJin.crawling_spring.common.util.CrawlingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MenuPanCrawlingService {

    private final CrawlingUtil crawlingUtil;

    @Value("${recipe.indexUrl.menu-pan.collection-name}")
    private String collectionName;

    @Value("${recipe.indexUrl.menu-pan.css-selector}")
    private String cssSelector;

    public void crawlRecipeUrls(String baseUrl, int startPage, int endPage) throws IOException {
        crawlingUtil.crawlWithPagination(baseUrl, startPage, endPage, cssSelector, collectionName);
    }


}
