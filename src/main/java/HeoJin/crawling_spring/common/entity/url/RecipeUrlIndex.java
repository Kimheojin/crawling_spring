package HeoJin.crawling_spring.common.entity.url;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Document
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipeUrlIndex {

    @Id
    private String id;

    private String hrefIndex;
    @Builder.Default
    private boolean isCrawled = false;
}
