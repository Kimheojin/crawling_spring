package HeoJin.crawling_spring.menupan.service;


import HeoJin.crawling_spring.common.entity.recipe.CookingOrder;
import HeoJin.crawling_spring.common.entity.recipe.Ingredient;
import HeoJin.crawling_spring.common.entity.recipe.Recipe;
import HeoJin.crawling_spring.common.util.CustomWebCrawlerUtil;
import HeoJin.crawling_spring.hansik.dto.RecipeUrlDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public void crawlAllRecipes() throws IOException {
        log.info("메뉴판 레시피 크롤링 시작");
        // url mongo 에서 가져온 다음에 합치는 메소드

        List<RecipeUrlDto> indexUrls = generateRecipeUrls();
        log.info("크롤링 대상 URL {} 개 조회됨", indexUrls.size());

        // 테스트용으로 10개만 처리하도록 제한
//        int maxTestCount = Math.min(indexUrls.size(), 10);
//        log.info("테스트 모드: {} 개의 URL만 처리", maxTestCount);

        for (int i = 0; i < indexUrls.size(); i++) {
            RecipeUrlDto urlDto = indexUrls.get(i);
            log.info("크롤링 중: URL = {}, 사이트 인덱스 = {}", urlDto.getUrl(), urlDto.getSiteIndex());
            crawlSingleRecipe(urlDto.getUrl(), String.valueOf(urlDto.getSiteIndex()));
        }

        log.info("메뉴판 레시피 크롤링 완료");
    }

    public List<RecipeUrlDto> generateRecipeUrls() {
        List<Integer> numbers = extractHrefIndexNumbers(indexCollectionName);
        return numbers.stream()
                .map(siteIndex -> new RecipeUrlDto(
                        sourceUrl.replace("{}", String.valueOf(siteIndex)),
                        siteIndex
                ))
                .collect(Collectors.toList());
    }



    private void crawlSingleRecipe(String recipeUrl, String siteIndex) throws IOException {
        log.info("개별 레시피 크롤링 시작 - 사이트 인덱스: {}", siteIndex);
        String sourceUrl = recipeUrl;
        
        try {
            Document document = CustomWebCrawlerUtil.connect(sourceUrl);
            log.info("웹 페이지 연결 성공 - URL: {}", sourceUrl);

        // 음식 명
        Element titleElement = document.selectFirst("div.wrap_top h2");
        String title = "";
        if(titleElement != null){
            title = titleElement.text();
            log.info("title:{}",title);
        }else{
            log.info("{} : 제목 파싱에 실패했습니다.", sourceUrl);
        }
        // 사이트 인덱스
        String Index = siteIndex;

        // 재료 목록
        log.info("재료 목록 파싱 시작");
   

        Elements aElements = document.select("div.infoTable dd a");
        log.info("재료 a 태그 {} 개 발견", aElements.size());

        List<Ingredient> ingredients = new ArrayList<>();
        for (Element a : aElements) {
            String ingredientName = a.text().trim();
            Ingredient ingredient = Ingredient.builder()
                    .ingredient(ingredientName).build();
            if (!ingredientName.isEmpty()) {
                ingredients.add(ingredient);
            }
        }

        log.info("총 재료 개수: {}", ingredients.size());

        // 조리 순서
        log.info("조리 순서 파싱 시작");
        List<CookingOrder> orders = new ArrayList<>();
        Elements dtElements = document.select("div.wrap_recipe dt");
        log.info("조리 순서 요소 {} 개 발견", dtElements.size());
        int step = 1;
        for (Element dt : dtElements) {
            String orderText = dt.text().trim();
            String cleanOrderText = orderText.replaceFirst("^\\d+\\.\\s*", "");

            CookingOrder order = CookingOrder.builder()
                    .step(step)
                    .instruction(cleanOrderText).build();

            orders.add(order);
            step++;
        }


        // 조리 시간

        log.info("총 조리 순서 개수: {}", orders.size());

        log.info("레시피 객체 생성 및 저장 시작");
        Recipe recipe = Recipe.builder()
                .siteIndex(Index)
                .sourceUrl(sourceUrl)
                .recipeName(title)
                .ingredientList(ingredients)
                .cookingOrderList(orders)
                .build();

        mongoTemplate.save(recipe, collectionName);
        log.info("메뉴판 레시피 저장 완료 - 레시피명: {}", title);
        
        } catch (Exception e) {
            log.error("레시피 크롤링 중 오류 발생 - URL: {}, 사이트 인덱스: {}, 에러: {}", sourceUrl, siteIndex, e.getMessage(), e);
            throw e;
        }


    }

    public List<Integer> extractHrefIndexNumbers(String indexCollectionName){
        log.info("인덱스 컬렉션에서 href 번호 추출 시작: {}", indexCollectionName);
        List<Map> documents = mongoTemplate.findAll(Map.class, indexCollectionName);
        log.info("총 {} 개의 문서 조회됨", documents.size());

        // 숫자만 가져오기
        Pattern pattern = Pattern.compile("\\d+");

        // https://girawhale.tistory.com/77

        List<Integer> result = documents.stream()
                .map(doc -> (String) doc.get("hrefIndex"))
                .filter(Objects::nonNull)
                .map(hrefIndex -> {
                    Matcher matcher = pattern.matcher(hrefIndex);
                    return matcher.find() ? Integer.parseInt(matcher.group()) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("추출된 인덱스 번호 개수: {}", result.size());
        return result;
    }
}
