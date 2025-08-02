package HeoJin.crawling_spring.service.hansik;


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

    public void loopHansikUrl() throws IOException {
        List<RecipeUrlDto> urlDtos = generateRecipeUrls();
        for (RecipeUrlDto urlDto : urlDtos) {
            crawlRecipes(urlDto.getUrl(), urlDto.getSiteIndex());
        }
    }

    public List<Integer> extractHrefIndexNumbers(){
        List<Map> documents = mongoTemplate.findAll(Map.class, "testHansik");

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
        List<Integer> numbers = extractHrefIndexNumbers();
        return numbers.stream()
                .map(number -> new RecipeUrlDto(
                        hansikUrl.replace("{}", String.valueOf(number)),
                        number
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

        // 레시피 재료

        // 첫 번째 리스트 - 재료 (list-ingredients 클래스)
        List<String> ingredientsRaw = doc.select("ul.list-unstyled.list-ingredients.bg-recipe.p-4 li")
                .stream()
                .map(Element::text)
                .collect(Collectors.toList());

        List<Map<String, String>> ingredients = new ArrayList<>();
        for (String ingredientText : ingredientsRaw) {
            String[] items = ingredientText.split(",");
            for (String item : items) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty()) {
                    Map<String, String> ingredient = parseIngredientItem(trimmed);
                    ingredients.add(ingredient);
                }
            }
        }

        List<String> seasoningsRaw = doc.select("ul.list-unstyled.mt-1 li.text-smaller")
                .stream()
                .map(Element::text)
                .collect(Collectors.toList());

        List<Map<String, String>> seasonings = new ArrayList<>();
        for (String seasoningText : seasoningsRaw) {
            String[] items = seasoningText.split(",");
            for (String item : items) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty()) {
                    Map<String, String> seasoning = parseIngredientItem(trimmed);
                    seasonings.add(seasoning);
                }
            }
        }


        // 조리 순서

        List<String> cookingSteps = new ArrayList<>();

        List<String> preparationSteps = doc.select("ol.list-unstyled.list-preparation li")
                .stream()
                .map(Element::text)
                .collect(Collectors.toList());
        cookingSteps.addAll(preparationSteps);

        List<String> cookingProcess = doc.select("ol.list-unstyled.list-preparation li")
                .stream()
                .map(Element::text)
                .collect(Collectors.toList());
        cookingSteps.addAll(cookingProcess);

        // 조리 시간

        // 조리 시간은 없는듯 사이트 ㅏ체에





    }

    private Map<String, String> parseIngredientItem(String item) {
        Map<String, String> result = new HashMap<>();

        // 정규식으로 재료명과 양 분리 (예: "오징어 20마리(손질 후 500g)")
        Pattern pattern = Pattern.compile("^([가-힣\\s]+?)\\s+(.*?)$");
        Matcher matcher = pattern.matcher(item);

        if (matcher.find()) {
            result.put("name", matcher.group(1).trim());
            result.put("quantity", matcher.group(2).trim());
        } else {
            result.put("name", item);
            result.put("quantity", "");
        }

        return result;
    }



}
