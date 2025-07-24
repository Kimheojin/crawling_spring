package HeoJin.crawling_spring.service.tehnth;


import HeoJin.crawling_spring.service.util.CrawlingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;



@Service
@RequiredArgsConstructor
@Slf4j
public class TenthRecipeUrlService {

    private final CrawlingUtil crawlingUtil;
    private final static String COLLECTION_NAME = "test10000";
    private final static String cssSelector = "div.theme_list.st2 a.thumbnail";

    public void crawlRecipeUrls(String baseUrl, int startPage, int endPage) throws IOException {
        crawlingUtil.crawlWithPagination(baseUrl, startPage, endPage, cssSelector, COLLECTION_NAME);
    }
}