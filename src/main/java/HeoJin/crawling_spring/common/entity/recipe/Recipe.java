package HeoJin.crawling_spring.common.entity.recipe;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Recipe {
    @Id // mongo 아이디가 string
    private String id;

    private String recipeName;

    private String cookingTime;

    private String sourceUrl;
    private String siteIndex;

    @Builder.Default
    private List<Ingredient> ingredientList = new ArrayList<>();
    @Builder.Default
    private List<CookingOrder> cookingOrderList = new ArrayList<>();

    private LocalDateTime crawledAt;

}
