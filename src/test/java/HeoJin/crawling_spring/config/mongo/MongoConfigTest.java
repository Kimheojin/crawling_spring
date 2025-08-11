package HeoJin.crawling_spring.config.mongo;


import HeoJin.crawling_spring.common.entity.recipe.CookingOrder;
import HeoJin.crawling_spring.common.entity.recipe.Ingredient;
import HeoJin.crawling_spring.common.entity.recipe.Recipe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@TestPropertySource(properties = {
        "spring.data.mongodb.database=test_db"
})
class MongoConfigTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(Recipe.class);
    }

    @Test
    @DisplayName("빈 생성 관련 정상 동작 테스트")
    void test1() {
        assertThat(mongoTemplate).isNotNull();
        assertThat(mongoTemplate.getCollectionNames()).isNotNull();
    }

    @Test
    @DisplayName("저장 관련 정상 동작 테스트")
    void test2() {
        Recipe recipe = Recipe.builder()
                .recipeName("김치찌개")
                .cookingTime("30분")
                .sourceUrl("http://example.com/recipe/1")
                .siteIndex("1")
                .crawledAt(LocalDateTime.now())
                .build();

        Recipe savedRecipe = mongoTemplate.save(recipe);

        assertThat(savedRecipe.getId()).isNotNull();
        assertThat(savedRecipe.getRecipeName()).isEqualTo("김치찌개");
        assertThat(savedRecipe.getCookingTime()).isEqualTo("30분");
        assertThat(savedRecipe.getSourceUrl()).isEqualTo("http://example.com/recipe/1");
        assertThat(savedRecipe.getSiteIndex()).isEqualTo("1");
        assertThat(savedRecipe.getCrawledAt()).isNotNull();

        assertThat(savedRecipe.getIngredientList().isEmpty()).isTrue();
        assertThat(savedRecipe.getCookingOrderList().isEmpty()).isTrue();
    }

    @Test
    @DisplayName("ingredientList + cookingOrderList 추가 entity 저장 관련 테스트")
    void test3() {
        Ingredient ingredient1 = Ingredient.builder()
                .ingredient("김치")
                .quantity("200g")
                .build();

        Ingredient ingredient2 = Ingredient.builder()
                .ingredient("돼지고기")
                .quantity("100g")
                .build();

        CookingOrder order1 = CookingOrder.builder()
                .step(1)
                .instruction("김치를 볶는다")
                .build();

        CookingOrder order2 = CookingOrder.builder()
                .step(2)
                .instruction("돼지고기를 넣고 볶는다")
                .build();

        Recipe recipe = Recipe.builder()
                .recipeName("김치찌개")
                .cookingTime("30분")
                .sourceUrl("http://example.com/recipe/1")
                .siteIndex("site1")
                .crawledAt(LocalDateTime.now())
                .build();

        recipe.getIngredientList().add(ingredient1);
        recipe.getIngredientList().add(ingredient2);
        recipe.getCookingOrderList().add(order1);
        recipe.getCookingOrderList().add(order2);

        Recipe savedRecipe = mongoTemplate.save(recipe);

        assertThat(savedRecipe.getId()).isNotNull();
        assertThat(savedRecipe.getIngredientList()).hasSize(2);
        assertThat(savedRecipe.getCookingOrderList()).hasSize(2);
        assertThat(savedRecipe.getIngredientList().get(0).getIngredient()).isEqualTo("김치");
        assertThat(savedRecipe.getIngredientList().get(0).getQuantity()).isEqualTo("200g");
        assertThat(savedRecipe.getCookingOrderList().get(0).getStep()).isEqualTo(1);
        assertThat(savedRecipe.getCookingOrderList().get(0).getInstruction()).isEqualTo("김치를 볶는다");
    }

    @Test
    @DisplayName("id를 통한 조회 관련 테스트")
    void test4() {
        Recipe recipe = Recipe.builder()
                .recipeName("된장찌개")
                .cookingTime("25분")
                .sourceUrl("http://example.com/recipe/2")
                .siteIndex("site2")
                .crawledAt(LocalDateTime.now())
                .build();

        Recipe savedRecipe = mongoTemplate.save(recipe);

        Recipe foundRecipe = mongoTemplate.findById(savedRecipe.getId(), Recipe.class);

        assertThat(foundRecipe).isNotNull();
        assertThat(foundRecipe.getRecipeName()).isEqualTo("된장찌개");
        assertThat(foundRecipe.getId()).isEqualTo(savedRecipe.getId());
    }

    @Test
    @DisplayName("레시피 이름을 통한 조회 관련 테스트")
    void test5() {
        Recipe recipe1 = Recipe.builder()
                .recipeName("김치찌개")
                .cookingTime("30분")
                .sourceUrl("http://example.com/recipe/1")
                .siteIndex("site1")
                .crawledAt(LocalDateTime.now())
                .build();

        Recipe recipe2 = Recipe.builder()
                .recipeName("된장찌개")
                .cookingTime("25분")
                .sourceUrl("http://example.com/recipe/2")
                .siteIndex("site2")
                .crawledAt(LocalDateTime.now())
                .build();

        mongoTemplate.save(recipe1);
        mongoTemplate.save(recipe2);

        Query query = new Query(Criteria.where("recipeName").is("김치찌개"));
        List<Recipe> foundRecipes = mongoTemplate.find(query, Recipe.class);

        assertThat(foundRecipes).hasSize(1);
        assertThat(foundRecipes.get(0).getRecipeName()).isEqualTo("김치찌개");
    }

    @Test
    @DisplayName("레시피 삭제 관련 테스트")
    void test6() {
        Recipe recipe = Recipe.builder()
                .recipeName("김치찌개")
                .cookingTime("30분")
                .sourceUrl("http://example.com/recipe/1")
                .siteIndex("site1")
                .crawledAt(LocalDateTime.now())
                .build();

        Recipe savedRecipe = mongoTemplate.save(recipe);

        mongoTemplate.remove(savedRecipe);

        Recipe foundRecipe = mongoTemplate.findById(savedRecipe.getId(), Recipe.class);
        assertThat(foundRecipe).isNull();
    }
}