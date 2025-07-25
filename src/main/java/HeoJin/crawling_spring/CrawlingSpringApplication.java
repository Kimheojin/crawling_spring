package HeoJin.crawling_spring;

import HeoJin.crawling_spring.config.RecipeUrlConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RecipeUrlConfig.class)
public class CrawlingSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrawlingSpringApplication.class, args);
	}

}
