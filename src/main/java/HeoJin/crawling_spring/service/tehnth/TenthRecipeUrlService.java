package HeoJin.crawling_spring.service.tehnth;


import HeoJin.crawling_spring.entity.url.Recipe10000UrlIndex;
import HeoJin.crawling_spring.exception.CustomException;
import HeoJin.crawling_spring.service.util.CustomWebCrawlerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenthRecipeUrlService {

    //     https://www.10000recipe.com/issue/view.html?cid=gdubu33&types=magazine&page=264 -> 1부터  264까지
    private final MongoTemplate mongoTemplate;
    private final static String COLLECTION_NAME = "indexMagazine10000";
    private final static String cssSelector = "div.theme_list.st2 a.thumbnail";
    public void crawlRecipeUrls(String baseUrl, int startPage, int endPage) throws IOException {
        log.info("크롤링 시작 - 페이지 범위: {} ~ {}", startPage, endPage);

        List<String> urls = generateUrlsToProcess(baseUrl, startPage, endPage);

        for (String url : urls) {
            try {
                log.info("크롤링 중: {}", url);
                List<String> extractedHrefs = CustomWebCrawlerUtil.extractHrefs(url, cssSelector);
                log.info("추출된 링크 수: {}", extractedHrefs.size());

                saveExtractedUrls(extractedHrefs);
                log.info("데이터 저장 완료");
                Thread.sleep(500);
            } catch (IOException e) {
                log.error("크롤링 실패: {} - {}", url, e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 인터럽트 상태 복원

                throw new CustomException("크롤링 중단됨: " + e.getMessage());
            }
        }

        log.info("전체 크롤링 완료");
    }

    private List<String> generateUrlsToProcess(String baseUrl, int startPage, int endPage) {
        List<String> urls = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            urls.add(baseUrl + i);
        }
        return urls;
    }

    private void saveExtractedUrls(List<String> hrefValues) {
        List<Recipe10000UrlIndex> urlIndices = hrefValues.stream()
                .map(href -> Recipe10000UrlIndex.builder()
                        .hrefIndex(href)
                        .isCrawled(false)
                        .build())
                .toList();

        mongoTemplate.insert(urlIndices, COLLECTION_NAME);
    }
}