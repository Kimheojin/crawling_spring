package HeoJin.crawling_spring.common.entity.recipe;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Ingredient {
    private String ingredient;
    private String quantity;

}
