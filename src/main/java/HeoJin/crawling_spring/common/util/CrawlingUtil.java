package HeoJin.crawling_spring.common.util;


import HeoJin.crawling_spring.common.entity.url.RecipeUrlIndex;
import HeoJin.crawling_spring.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class CrawlingUtil {

    private final MongoTemplate mongoTemplate;

    public void crawlWithPagination(String baseUrl, int startPage, int endPage,
                                    String cssSelector, String collectionName) throws IOException {
        crawlWithPagination(baseUrl, startPage, endPage, cssSelector, collectionName, 500);
    }

    public void crawlWithPagination(String baseUrl, int startPage, int endPage,
                                    String cssSelector, String collectionName, int delayMs) throws IOException, CustomException {
        log.info("크롤링 시작 - 페이지 범위: {} ~ {}", startPage, endPage);

        List<String> urls = generateUrlsToProcess(baseUrl, startPage, endPage);

        for (String url : urls) {
            try {
                log.info("크롤링 중: {}", url);
                List<String> extractedHrefs = CustomWebCrawlerUtil.extractHrefs(url, cssSelector);
                log.info("추출된 링크 수: {}", extractedHrefs.size());

                saveExtractedUrls(extractedHrefs, collectionName);
                log.info("데이터 저장 완료");
                Thread.sleep(delayMs);
            } catch (IOException e) {
                log.error("크롤링 실패: {} - {}", url, e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
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

    private void saveExtractedUrls(List<String> hrefValues, String collectionName) {
        List<RecipeUrlIndex> urlIndices = hrefValues.stream()
                .map(href -> RecipeUrlIndex.builder()
                        .hrefIndex(href)
                        .isCrawled(false)
                        .build())
                .toList();

        mongoTemplate.insert(urlIndices, collectionName);
    }

    public void crawlWithPaginationHanSik(String baseUrlTemplate, int startPage, int endPage,
                                    String cssSelector, String collectionName) throws IOException, CustomException {
        log.info("크롤링 시작 - 페이지 범위: {} ~ {}", startPage, endPage);

        List<String> urls = generateUrlsFromTemplate(baseUrlTemplate, startPage, endPage);

        for (String url : urls) {
            try {
                log.info("크롤링 중: {}", url);
                List<String> extractedHrefs = CustomWebCrawlerUtil.extractHrefs(url, cssSelector);
                log.info("추출된 링크 수: {}", extractedHrefs.size());

                saveExtractedUrls(extractedHrefs, collectionName);
                log.info("데이터 저장 완료");
                Thread.sleep(10000);
            } catch (IOException e) {
                log.error("크롤링 실패: {} - {}", url, e.getMessage());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new CustomException("크롤링 중단됨: " + e.getMessage());
            }
        }

        log.info("전체 크롤링 완료");
    }

    private List<String> generateUrlsFromTemplate(String baseUrlTemplate, int startPage, int endPage) {
        List<String> urls = new ArrayList<>();

        for (int page = startPage; page <= endPage; page++) {
            String url = baseUrlTemplate.replace("{}", String.valueOf(page));
            urls.add(url);
        }

        return urls;
    }



}