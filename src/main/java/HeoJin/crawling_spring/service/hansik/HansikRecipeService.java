package HeoJin.crawling_spring.service.hansik;


import HeoJin.crawling_spring.entity.recipe.CookingOrder;
import HeoJin.crawling_spring.entity.recipe.Ingredient;
import HeoJin.crawling_spring.service.hansik.dto.RecipeUrlDto;
import HeoJin.crawling_spring.service.util.CustomWebCrawlerUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
        List<RecipeUrlDto> urlDtos = generateRecipeUrls();
        for (RecipeUrlDto urlDto : urlDtos) {
            crawlRecipes(urlDto.getUrl(), urlDto.getSiteIndex());
        }
    }

    public List<Integer> extractHrefIndexNumbers(String indexCollectionName){
        List<Map> documents = mongoTemplate.findAll(Map.class, indexCollectionName);

        // 숫자만 가져오기
        Pattern pattern = Pattern.compile("\\d+");

        // https://girawhale.tistory.com/77

        return documents.stream()
                .map(doc -> (String) doc.get("hrefIndex"))
                .filter(Objects::nonNull)
                .map(hrefIndex -> {
                    Matcher matcher = pattern.matcher(hrefIndex);
                    return matcher.find() ? Integer.parseInt(matcher.group()) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

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

        // site url
        String sourceUrl = baseUrl;
        log.info("sourceUrl: {}", sourceUrl);

        Document doc = CustomWebCrawlerUtil.connect(sourceUrl);

        // 사이트 인덱스
        String Index = siteIndex.toString();
        log.info("Index: {}", Index);

        // 레시피 이름
        String recipeName = doc.select("h3.mb-0").first().text();
        log.info("recipeName: {}", recipeName);

        // 레시피 재료

        // 첫 번째 리스트 - 재료 (list-ingredients 클래스)
        List<String> ingredientsRaw = doc.select("ul.list-unstyled.list-ingredients.bg-recipe.p-4 li")
                .stream()
                .map(Element::text)
                .collect(Collectors.toList());

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


        // 조리 순서

        List<CookingOrder> cookingSteps = new ArrayList<>();

        List<String> preparationSteps = doc.select("ol.list-unstyled.list-preparation li")
                .stream()
                .map(Element::text)
                .toList();
        Integer count = 1;
        for (String cmp : preparationSteps) {
            CookingOrder cookingOrder = CookingOrder.builder()
                    .step(count)
                    .instruction(cmp).build();

            count ++;
        }




    }





}
