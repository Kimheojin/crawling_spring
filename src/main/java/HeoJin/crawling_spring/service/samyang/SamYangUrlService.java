package HeoJin.crawling_spring.service.samyang;


import HeoJin.crawling_spring.service.util.CrawlingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SamYangUrlService {

    private final CrawlingUtil crawlingUtil;



    @Value("${recipe.indexUrl.samyang.css-selector}")
    private String cssSelector;

    @Value("${recipe.indexUrl.samyang.collection-name}")
    private String collectionName;


    public void crawlRecipeUrls(String baseUrl, int startPage, int endPage) throws IOException {
        crawlingUtil.crawlWithPagination(baseUrl, startPage, endPage, cssSelector, collectionName);
    }



}
