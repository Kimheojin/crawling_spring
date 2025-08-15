package HeoJin.crawling_spring.hansik.service;


import HeoJin.crawling_spring.common.entity.recipe.CookingOrder;
import HeoJin.crawling_spring.common.entity.recipe.Ingredient;
import HeoJin.crawling_spring.common.entity.recipe.Recipe;
import HeoJin.crawling_spring.hansik.dto.RecipeUrlDto;
import HeoJin.crawling_spring.common.util.CustomWebCrawlerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.jsoup.nodes.Document;


import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HansikRecipeService {

    private final MongoTemplate mongoTemplate;

    // 한식 url  + {} 처리 생각하기
    @Value("${recipe.sites.hansik.url}")
    private String hansikUrl;

    @Value("${recipe.sites.hansik.collection-name}")
    private String collectionName;

    @Value("${recipe.indexUrl.hansik.collection-name}")
    private String indexCollectionName;



    public void loopHansikUrl() throws IOException {
        log.info("한식 레시피 크롤링 시작");
        List<RecipeUrlDto> urlDtos = generateRecipeUrls();
        log.info("총 {} 개의 URL 생성됨", urlDtos.size());
        
        for (RecipeUrlDto urlDto : urlDtos) {
            log.info("크롤링 중: URL = {}, 사이트 인덱스 = {}", urlDto.getUrl(), urlDto.getSiteIndex());
            crawlRecipes(urlDto.getUrl(), urlDto.getSiteIndex());
        }
        log.info("한식 레시피 크롤링 완료");
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

    public List<RecipeUrlDto> generateRecipeUrls() {
        List<Integer> numbers = extractHrefIndexNumbers(indexCollectionName);
        return numbers.stream()
                .map(siteIndex -> new RecipeUrlDto(
                        hansikUrl.replace("{}", String.valueOf(siteIndex)),
                        siteIndex
                ))
                .collect(Collectors.toList());
    }

    // 파싱 이루어지는 부분

    public void crawlRecipes(String baseUrl, Integer siteIndex) throws IOException {
        log.info("레시피 크롤링 시작 - 사이트 인덱스: {}", siteIndex);

        // site url
        String sourceUrl = baseUrl;
        log.info("sourceUrl: {}", sourceUrl);

        try {
            Document doc = CustomWebCrawlerUtil.connect(sourceUrl);
            log.info("웹 페이지 연결 성공");

            // 사이트 인덱스
            String Index = siteIndex.toString();
            log.info("Index: {}", Index);

            // 레시피 이름
            String recipeName = doc.select("h3.mb-0").first().text();
            log.info("recipeName: {}", recipeName);

            // 레시피 재료
            log.info("레시피 재료 파싱 시작");

            // 첫 번째 리스트 - 재료 (list-ingredients 클래스)
            List<String> ingredientsRaw = doc.select("ul.list-unstyled.list-ingredients.bg-recipe.p-4 li")
                    .stream()
                    .map(Element::text)
                    .collect(Collectors.toList());
            log.info("기본 재료 {} 개 추출", ingredientsRaw.size());

            List<Ingredient> ingredients = new ArrayList<>();
            for (String ingredientText : ingredientsRaw) {
                String[] items = ingredientText.split(",");
                for (String item : items) {
                    String trimmed = item.trim(); // 앞 뒤 공백 제거
                    if (!trimmed.isEmpty()) {
                        Ingredient ingredient = Ingredient.builder()
                                .ingredient(trimmed).build();
                        ingredients.add(ingredient);
                    }
                }
            }

            // 하위 재료
            List<String> seasoningsRaw = doc.select("ul.list-unstyled.mt-1 li.text-smaller")
                    .stream()
                    .map(Element::text)
                    .collect(Collectors.toList());
            log.info("양념/부재료 {} 개 추출", seasoningsRaw.size());

            for (String seasoningText : seasoningsRaw) {
                String[] items = seasoningText.split(",");
                for (String item : items) {
                    String trimmed = item.trim();
                    if (!trimmed.isEmpty()) {
                        Ingredient ingredient = Ingredient.builder()
                                .ingredient(trimmed).build();
                        ingredients.add(ingredient);
                    }
                }
            }
            log.info("총 재료 개수: {}", ingredients.size());


            // 조리 순서
            log.info("조리 순서 파싱 시작");

            List<CookingOrder> cookingSteps = new ArrayList<>();

            List<String> preparationSteps = doc.select("ol.list-unstyled.list-preparation li")
                    .stream()
                    .map(Element::text)
                    .toList();
            log.info("조리 순서 {} 단계 추출", preparationSteps.size());
            
            int count = 1;
            for (String cmp : preparationSteps) {
                CookingOrder cookingOrder = CookingOrder.builder()
                        .step(count)
                        .instruction(cmp).build();

                cookingSteps.add(cookingOrder);
                count ++;
            }

            // 조리 시간 부분 없는 듯
            log.info("레시피 객체 생성 및 저장 시작");
            Recipe recipe = Recipe.builder()
                    .recipeName(recipeName)
                    .cookingOrderList(cookingSteps)
                    .ingredientList(ingredients)
                    .siteIndex(Index)
                    .sourceUrl(sourceUrl)
                    .build();

            mongoTemplate.save(recipe, collectionName);
            log.info("한식데이터 저장 완료 - 레시피명: {}", recipeName);
            
        } catch (Exception e) {
            log.error("레시피 크롤링 중 오류 발생 - URL: {}, 에러: {}", sourceUrl, e.getMessage(), e);
            throw e;
        }
    }





}
