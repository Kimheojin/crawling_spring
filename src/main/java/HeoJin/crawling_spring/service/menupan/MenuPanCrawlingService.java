package HeoJin.crawling_spring.service.menupan;


import HeoJin.crawling_spring.service.util.CrawlingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MenuPanCrawlingService {

    private final CrawlingUtil crawlingUtil;
    private final static String COLLECTION_NAME = "testMenuPan";
    private final static String cssSelector = "span.link a";

    public void crawlRecipeUrls(String baseUrl, int startPage, int endPage) throws IOException {
        crawlingUtil.crawlWithPagination(baseUrl, startPage, endPage, cssSelector, COLLECTION_NAME);
    }


}
