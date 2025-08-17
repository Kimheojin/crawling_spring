package HeoJin.crawling_spring.samyang.service;




import HeoJin.crawling_spring.common.entity.recipe.CookingOrder;
import HeoJin.crawling_spring.common.entity.recipe.Ingredient;
import HeoJin.crawling_spring.common.entity.recipe.Recipe;
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

    public void crawlAllRecipes() throws IOException {
        log.info("삼양 레시피 크롤링 시작");
        List<Map> indexUrls = getAllRecipeUrlAsMap(indexCollectionName);
        log.info("크롤링 대상 URL {} 개 조회됨", indexUrls.size());

        // 테스트용으로 10개만 처리하도록 제한
//        int maxTestCount = Math.min(indexUrls.size(), 10);
//        log.info("테스트 모드: {} 개의 URL만 처리", maxTestCount);

        for (int i = 0; i < indexUrls.size(); i++) {
            Map map = indexUrls.get(i);
            String siteIndex = (String) map.get("hrefIndex");
            String url = indexUrl + siteIndex;
            log.info("크롤링 중: URL = {}, 사이트 인덱스 = {}", url, siteIndex);

            crawlSingleRecipe(url, siteIndex);
        }

        log.info("삼양 레시피 크롤링 완료");
    }

    // site index 는 그냥 바로 넣으면 되는 거 아닌가?

    public List<Map> getAllRecipeUrlAsMap(String collectionName) {
        Query query = new Query();

        query.addCriteria(Criteria.where("isCrawled").is(false));

        return mongoTemplate.find(query, Map.class, collectionName);
    }

    public void crawlSingleRecipe(String recipeUrl, String index) throws IOException {
        log.info("개별 레시피 크롤링 시작 - 사이트 인덱스: {}", index);
        String sourceUrl = recipeUrl;

        String siteIndex = index;

        Document doc = CustomWebCrawlerUtil.connect(sourceUrl);
        log.info("웹 페이지 연결 성공 - URL: {}", sourceUrl);

        // 레시피 이름
        String recipeName = doc.selectFirst("h3.recipe_detail_title").text();
        log.info("레시피명: {}", recipeName);

        // 레시피 재료
        log.info("재료 목록 파싱 시작");
        List<Ingredient> ingredients = new ArrayList<>();
        Elements elements = doc.select("table.col3 tbody tr");
        log.info("재료 요소 {} 개 발견", elements.size());

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
        log.info("총 재료 개수: {}", ingredients.size());
        
        // 조리 순서
        log.info("조리 순서 파싱 시작");
        List<CookingOrder> orders = new ArrayList<>();

        // li 목록
        Elements stepElements = doc.select("div.fr-view.fr-view-article ol li");
        log.info("조리 순서 요소 {} 개 발견", stepElements.size());
        int count = 1;
        for(Element step :  stepElements){


            String description = step.text().trim();
            CookingOrder cookingOrder = CookingOrder.builder()
                    .instruction(description)
                    .step(count).build();
            count++;
            orders.add(cookingOrder);
        }
        log.info("총 조리 순서 개수: {}", orders.size());

        log.info("레시피 객체 생성 및 저장 시작");
        Recipe recipe = Recipe.builder()
                .cookingOrderList(orders)
                .ingredientList(ingredients)
                .recipeName(recipeName)
                .sourceUrl(sourceUrl)
                .siteIndex(index).build();

        mongoTemplate.save(recipe, collectionName);
        log.info("삼양 레시피 저장 완료 - 레시피명: {}", recipeName);

        // 조리 시가




    }



}
