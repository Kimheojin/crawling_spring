package HeoJin.crawling_spring.service.tehnth;


import HeoJin.crawling_spring.entity.url.Recipe10000UrlIndex;
import HeoJin.crawling_spring.service.util.CustomWebCrawlerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TenthRecipeService {
    
//     https://www.10000recipe.com/issue/view.html?cid=gdubu33&types=magazine&page=264 -> 1부터  264까지
    private final MongoTemplate mongoTemplate;
    private final static String COLLECTION_NAME = "indexMagazine10000";
    private final static String cssSelector= "div.container div#contents_area_full div.chef_cont div div#id_bestRecipes.theme_list.st2 a";

    public void crawlRecipeUrls(String baseUrl, int startPage, int endPage) throws IOException {
        List<String> urls = generateUrlsToProcess(baseUrl, startPage, endPage);

        for (String url : urls) {
            List<String> extractedHrefs = CustomWebCrawlerUtil.extractHrefs(url, cssSelector);

            saveExtractedUrls(extractedHrefs);
        }
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
