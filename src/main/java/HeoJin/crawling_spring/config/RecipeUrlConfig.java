package HeoJin.crawling_spring.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;


@ConfigurationProperties(prefix = "recipe")
@Getter
@Setter
public class RecipeUrlConfig {

    private Map<String, String> urls = new HashMap<>();
}
