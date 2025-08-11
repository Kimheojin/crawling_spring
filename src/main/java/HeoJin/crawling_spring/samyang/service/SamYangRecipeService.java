package HeoJin.crawling_spring.samyang.service;




import HeoJin.crawling_spring.common.entity.recipe.CookingOrder;
import HeoJin.crawling_spring.common.entity.recipe.Ingredient;
import HeoJin.crawling_spring.common.util.CustomWebCrawlerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import org.springframework.data.mongodb.core.query.Query;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SamYangRecipeService {

    private final MongoTemplate mongoTemplate;
    @Value("${recipe.sites.samyang.url}")
    private String indexUrl;

    @Value("${recipe.sites.samyang.collection-name}")
    private String collectionName;

    @Value("${recipe.indexUrl.samyang.collection-name}")
    private String indexCollectionName;

    // url 조합 먼저 하면 될듯

    public void crawlingRecipeAboutSamYang() throws IOException {
        List<Map> indexUrls = getAllRecipeUrlAsMap(indexCollectionName);

        for (Map map : indexUrls) {
            String siteIndex = (String) map.get("siteIndex");
            String url = indexUrl + siteIndex;

            crawledRecipeAboutSamYang(url, siteIndex);
        }


    }

    // site index 는 그냥 바로 넣으면 되는 거 아닌가?

    public List<Map> getAllRecipeUrlAsMap(String collectionName) {
        Query query = new Query();

        query.addCriteria(Criteria.where("isCrawled").is(false));

        return mongoTemplate.find(query, Map.class, collectionName);
    }

    public void crawledRecipeAboutSamYang(String baseUrl, String index) throws IOException {

        String sourceUrl = baseUrl;
        log.info("sourceUrl : {}", sourceUrl);

        String siteIndex = index;
        log.info("사이트 인덱스 : {}", siteIndex);

        Document doc = CustomWebCrawlerUtil.connect(sourceUrl);

        // 레시피 이름
        String recipeName = doc.selectFirst("h3.recipe_detail_title").text();

        // 레시피 재료
        List<Ingredient> ingredients = new ArrayList<>();
        Elements elements = doc.select("table.col3 tbody tr");

        for (Element row : elements) {
            Elements tds = row.select("td");

            if (tds.size() == 1) {
                // td가 1개인 경우 - 재료명만
                String ingredientName = tds.get(0).text().trim();
                Ingredient ingredient = Ingredient.builder()
                        .ingredient(ingredientName)
                        .build();
                ingredients.add(ingredient);


            } else if (tds.size() == 2) {
                // td가 2개인 경우 - 재료명 + 값
                String ingredientName = tds.get(0).text().trim();
                String value = tds.get(1).text().trim();
                Ingredient ingredient = Ingredient.builder()
                        .ingredient(ingredientName)
                        .quantity(value)
                        .build();
                ingredients.add(ingredient);

            } else if (tds.size() >= 3) {
                // td가 3개 이상인 경우 - 뒤에 2개만 사용
                String ingredientName = tds.get(1).text().trim();
                String value = tds.get(2).text().trim();
                Ingredient ingredient = Ingredient.builder()
                        .ingredient(ingredientName)
                        .quantity(value)
                        .build();
                ingredients.add(ingredient);
            }

        }
        // 조리 순서
        List<CookingOrder> orders = new ArrayList<>();

        // li 목록
        Elements stepElements = doc.select("div.fr-view.fr-view-article ol li");
        Integer count = 1;
        for(Element step :  stepElements){


            String description = step.text().trim();
            CookingOrder cookingOrder = CookingOrder.builder()
                    .instruction(description)
                    .step(count).build();
            count++;
            orders.add(cookingOrder);
        }

        // 조리 시가




    }



}
