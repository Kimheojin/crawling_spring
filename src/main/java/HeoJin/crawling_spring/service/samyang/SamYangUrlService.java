package HeoJin.crawling_spring.service.samyang;


import HeoJin.crawling_spring.service.util.CrawlingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class SamYangUrlService {

    private final CrawlingUtil crawlingUtil;
    private final static String COLLECTION_NAME = "testSamYang";
    private final static String cssSelector = "a.subject";

    public void crawlRecipeUrls(String baseUrl, int startPage, int endPage) throws IOException {
        crawlingUtil.crawlWithPagination(baseUrl, startPage, endPage, cssSelector, COLLECTION_NAME);
    }



}
