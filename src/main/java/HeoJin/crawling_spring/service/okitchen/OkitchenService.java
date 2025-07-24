package HeoJin.crawling_spring.service.okitchen;


import HeoJin.crawling_spring.entity.recipe.CookingOrder;
import HeoJin.crawling_spring.entity.recipe.Ingredient;
import HeoJin.crawling_spring.entity.recipe.Recipe;
import HeoJin.crawling_spring.exception.CustomException;
import HeoJin.crawling_spring.service.util.CustomWebCrawlerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OkitchenService {


    private final MongoTemplate mongoTemplate;
    public void loopOkitchenUrl(String baseUrl, Long startIndex, Long lastIndex){
        log.info("크롤링 시장: startIndex -> {}, lastIndex -> {}", startIndex, lastIndex);

        for(Long i = startIndex; i <= lastIndex; i++){
            try {
                String fullUrl = baseUrl + i;
                crawRecipes(fullUrl, i.intValue());

                Thread.sleep(500);
            } catch (Exception e ){
                log.error("크롤링 실패 : 인덱스 -> {}, message : {} ", i, e.getMessage());
            }
        }

    }

    private void crawRecipes(String acceptUrl, int index) throws Exception {

        String sourceUrl = acceptUrl;
        Document doc = CustomWebCrawlerUtil.connect(sourceUrl);// doc 객체로


        Element content = Optional.ofNullable(doc.select("div.content.detailBody").first())
                .orElseThrow(() -> new CustomException("오키친 사이트 : 존재하지 않음 index 번호 : " + index)); // 발생하면 종료

        // 사이트 인덱스
        String Site_index = "오 키친 + " + index;
        // 이름
        String recipeName = content.select("div.detailInfo h2").text();

        log.info("=======  element 접근시작 : {}=======", index);

        log.info("레시피 명 : " + recipeName);


        // 재료
        List<Ingredient> ingredients = content.select("div.ingredients p")
                .stream()
                .map(Element::text)
                .filter(text -> !text.trim().isEmpty())
                .flatMap(text -> Arrays.stream(text.split(",")))
                .map(String::trim)
                .map(text -> Ingredient.builder()
                        .ingredient(text)
                        .build())
                .collect(Collectors.toList());


        // 그거
        log.info("재료 총 갯수 : {}", ingredients.size());
        log.info("재료 들 : {}", ingredients);


        // 조리순서
        List<CookingOrder> cookingOrders = new ArrayList<>();
        Elements orderElements = content.select("div.ContentArea p");
        log.info("Steps count: " + orderElements.size());

        int cookingOrderStep = 1;
        for (Element order : orderElements) {
            if(order.text().contains("Step") || order.text().isBlank()){
                continue;
            }
            cookingOrders.add(CookingOrder.builder()
                    .step((long) cookingOrderStep)
                    .instruction(order.text())
                    .build());
            log.info("Step: " + order.text());
            cookingOrderStep++;
            
        }

        // 조리 시간

        Element timeElement = content.select("div.recipe-stats span:contains(조리시간) + h4").first();
        int minutes = 0;
        if(timeElement != null) {
            String timeStr = timeElement.text()
                    .replaceAll("[^0-9]", ""); // 숫자가 아닌 모든 문자 제거

            // 숫자가 없으면 예외 발생
            if (timeStr.isEmpty()) {
                throw new CustomException("조리시간이 숫자형태로 존재하지 않아요" + index);
            }
            // 숫자가 있으면 파싱
            minutes = Integer.parseInt(timeStr);
        } else {
            // timeElement 자체가 없는 경우
            throw new CustomException("조리시간이 존재하지 않아요. index : "+ index);
        }

        Recipe recipe = Recipe.builder()
                .sourceUrl(sourceUrl)
                .siteIndex(Site_index)
                .recipeName(recipeName)
                .ingredientList(ingredients)  // List<Ingredient>
                .cookingOrderList(cookingOrders)  // List<CookingOrder>
                .cookingTime((long) minutes)
                .crawledAt(LocalDateTime.now())
                .build();


        mongoTemplate.save(recipe, "test1");

        log.info("===== 크롤링 완료  + 해당 인덱스 = {} ====", index);

    }


}
