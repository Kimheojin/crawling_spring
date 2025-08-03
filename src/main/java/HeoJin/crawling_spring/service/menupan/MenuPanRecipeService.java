package HeoJin.crawling_spring.service.menupan;


import HeoJin.crawling_spring.entity.recipe.CookingOrder;
import HeoJin.crawling_spring.entity.recipe.Ingredient;
import HeoJin.crawling_spring.entity.recipe.Recipe;
import HeoJin.crawling_spring.service.util.CustomWebCrawlerUtil;
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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuPanRecipeService {

    private final MongoTemplate mongoTemplate;

    @Value("${recipe.sites.menupan.url}")
    private String sourceUrl;

    @Value("${recipe.sites.menupan.collection-name}")
    private String collectionName;

    @Value("${recipe.indexUrl.menu-pan.collection-name}")
    private String indexCollectionName;

    public void crawlingRecipeAboutMenupan() throws IOException {
        // url mongo 에서 가져온 다음에 합치는 메소드

        List<Map> indexUrls = getAllRecipeUrlAsMap(indexCollectionName);

        for(Map map : indexUrls){
            String siteIndex = (String) map.get("siteIndex");
            String url  = sourceUrl + siteIndex;

            crawledRecipe(url, siteIndex);
        }
    }

    public List<Map>  getAllRecipeUrlAsMap(String collectionName) {
        Query query = new Query();

        query.addCriteria(Criteria.where("isCrawled").is(false));

        return mongoTemplate.find(query, Map.class, collectionName);
    }

    private void crawledRecipe(String acceptUrl, String siteIndex) throws IOException {
        String sourceUrl = acceptUrl;
        Document document = CustomWebCrawlerUtil.connect(sourceUrl);

        // 음식 명
        Element titleElement = document.selectFirst("div.wrap_top h2");

        if(titleElement != null){
            String title = titleElement.text();
            log.info("title:{}",title);
        }else{
            log.info("{} : 제목 파싱에 실패했습니다.", sourceUrl);
        }
        // 사이트 인덱스
        String Index = siteIndex;

        // 재료 목록
        List<Ingredient> ingredients = new ArrayList<>();

        Elements ddElements = document.select("div.infoTable dd");

        for (Element dd : ddElements) {
            // a 태그 안에서 재료면 추출
            Element linkElement = dd.selectFirst("a");
            if (linkElement != null) {
                String ingredientName = linkElement.text().trim();

                String fullText = dd.text();
                String quantity = fullText.replace(ingredientName, "").trim();

                quantity = quantity.replaceAll("^[,\\s\"]+|[,\\s\"]+$", "");

                Ingredient ingredient = Ingredient.builder()
                        .ingredient(ingredientName)
                        .quantity(quantity)
                        .build();
                ingredients.add(ingredient);
            }

        }

        // 조리 순서
        List<CookingOrder> orders = new ArrayList<>();
        Elements dtElements = document.select("div.wrap_recipe dt");
        Long step = 1L;
        for (Element dt : dtElements) {
            String orderText = dt.text().trim();
            String cleanOrderText = orderText.replaceFirst("^\\d+\\.\\s*", "");

            CookingOrder order = CookingOrder.builder()
                    .step(step)
                    .instruction(cleanOrderText).build();

            orders.add(order);
        }


        // 조리 시간
        Recipe recipe = Recipe.builder()
                .build();

    }
}
