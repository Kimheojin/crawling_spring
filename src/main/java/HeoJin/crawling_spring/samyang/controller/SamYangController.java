package HeoJin.crawling_spring.samyang.controller;



import HeoJin.crawling_spring.samyang.service.SamYangRecipeService;
import HeoJin.crawling_spring.samyang.service.SamYangUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/crawling/samyang")
@RequiredArgsConstructor
public class SamYangController {

    private final SamYangRecipeService samYangRecipeService;
    private final SamYangUrlService samYangUrlService;

    @Value("${recipe.indexUrl.samyang.url}")
    private String samYangUrl;

    @PostMapping("/data")
    public ResponseEntity<String> samYangCrawling() throws IOException {

        samYangRecipeService.crawlAllRecipes();
        return ResponseEntity.ok("크롤링이 완료되었습니다.");
    }

    @PostMapping("/urls")
    public ResponseEntity<String> samYangUrlCrawling(
            @RequestParam("categoryNo") int no,
            @RequestParam("startPage") int startPage,
            @RequestParam("endPage") int endPage) throws IOException {

        if (no != 20 && no != 21 && no != 22 && no != 23) {
            return ResponseEntity
                    .badRequest()
                    .body("no 는 20, 21, 22, 23만 가능합니다.");
        }

        String samYangNoUrl = samYangUrl;
        String url = samYangNoUrl.replace("{}", String.valueOf(no));

        samYangUrlService.crawlRecipeUrls(url ,startPage, endPage);
        return ResponseEntity.ok("삼양 크롤링이 종료되었습니다");
    }
}