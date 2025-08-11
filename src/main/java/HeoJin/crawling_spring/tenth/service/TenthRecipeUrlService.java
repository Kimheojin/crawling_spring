package HeoJin.crawling_spring.tenth.service;


import HeoJin.crawling_spring.common.util.CrawlingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;



@Service
@RequiredArgsConstructor
@Slf4j
public class TenthRecipeUrlService {

    private final CrawlingUtil crawlingUtil;

    @Value("${recipe.indexUrl.recipe10000.url}")
    private String baseUrl ;


    @Value("${recipe.indexUrl.recipe10000.collection-name}")
    private String collectionName;

    @Value("${recipe.indexUrl.recipe10000.css-selector}")
    private String cssSelector;

    public void crawlRecipeUrls( int startPage, int endPage) throws IOException {
        crawlingUtil.crawlWithPagination(baseUrl, startPage, endPage, cssSelector, collectionName);
    }
}