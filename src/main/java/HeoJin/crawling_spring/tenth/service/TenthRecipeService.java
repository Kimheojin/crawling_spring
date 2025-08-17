package HeoJin.crawling_spring.tenth.service;


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
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenthRecipeService {

    @Value("${recipe.indexUrl.recipe10000.collection-name}")
    private String indexCollectionName;

    @Value("${recipe.sites.recipe10000.collection-name}")
    private String RecipeCollectionName;

    @Value("${recipe.sites.recipe10000.url}")
    private String baseUrl;

    private final MongoTemplate mongoTemplate;

    // 인덱스 기반으로 크롤링 하는 함수
    public void crawlAllRecipes() throws Exception {
        // mongo template 활용해서 url 빼오고
        List<Map> indexUrls = getAllRecipeUrlAsMap(indexCollectionName);
        log.info("총 {} 개의 URL 조회됨", indexUrls.size());
        
        // 테스트용으로 10개만 처리하도록 제한
//        int maxTestCount = Math.min(indexUrls.size(), 10);
//        log.info("테스트 모드: {} 개의 URL만 처리", maxTestCount);

        // for문?
        for(int i = 0; i < indexUrls.size(); i++){
            Map url = indexUrls.get(i);
            String siteIndex = (String) url.get("hrefIndex");
            String sourceUrl = baseUrl + siteIndex;
            log.info("sourceUrl 생성 : {}",  sourceUrl);

            crawlSingleRecipe(sourceUrl, siteIndex);

        }

    }

    // mongo 에서 url 가져오는 함수
    // map?? entity 기반 Arraylist? 둘중 하나로
    private List<Map> getAllRecipeUrlAsMap(String collectionName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isCrawled").is(false));

        // 인덱스 안타면 느림
//        query.with(Sort.by(Sort.Direction.ASC, "hrefIndex"));

        return mongoTemplate.find(query, Map.class, collectionName);
    }


    private void crawlSingleRecipe(String recipeUrl, String siteIndex) throws Exception {

        Document doc = CustomWebCrawlerUtil.connect(recipeUrl);

        // 컨텐츠 영역 로딩
        Element content = doc.select("div#contents_area_full").first();

        // 사이트 인덱스
        String site_index = siteIndex;
        log.info("Site_index : {}", site_index);

        // 이름
        String foodname = content.select("div.view2_summary.st3 h3").text();
        log.info("foodname : {}", foodname);

        // 재료 추출 - ready_ingre3 영역에서 가져오기
        Elements ingredientItems = doc.select("div.ready_ingre3 ul li");

        List<Ingredient> ingredientList = new ArrayList<>();
        for (Element ing : ingredientItems) {
            Element nameElement = ing.select("div.ingre_list_name a").first();
            Element quantityElement = ing.select("span.ingre_list_ea").first();

            String ingredientName = nameElement != null ? nameElement.text().trim() : "";
            String quantity = quantityElement != null ? quantityElement.text().trim() : "";

            log.info("파싱된 재료 - 이름: '{}', 수량: '{}'", ingredientName, quantity);

            if (!ingredientName.isEmpty()) {
                ingredientList.add(new Ingredient(ingredientName, quantity));
                log.info("추가된 재료: {} - {}", ingredientName, quantity);
            }
        }

        log.info("최종 재료 리스트 크기: {}", ingredientList.size());

        // 조리 순서

        Elements steps = content.select("div[id^=stepdescr]");  // id가 'stepdescr'로 시작하는 모든 div 선택
        ArrayList<CookingOrder> cookingOrders = new ArrayList<>();
        int stepCount = 1;
        for (Element step : steps) {
            String stepDescription = step.text().trim();
            log.info("Step {}: {}", stepCount, stepDescription);
            cookingOrders.add(new CookingOrder(stepCount, stepDescription));
            stepCount++;
        }

        // 조리 시간
        String timeText = content.select("span.view2_summary_info2").text().trim();
        int time = 0;
        try {
            // 숫자만 추출하여 파싱
            time = Integer.parseInt(timeText.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            log.warn("조리시간 파싱 실패: {}", timeText);
        }

        Recipe recipe = Recipe.builder()
                .sourceUrl(recipeUrl)
                .siteIndex(site_index)
                .recipeName(foodname)
                .cookingTime(String.valueOf(time))
                .cookingOrderList(cookingOrders)
                .ingredientList(ingredientList)
                .build();

        mongoTemplate.insert(recipe, RecipeCollectionName);



    }

}
