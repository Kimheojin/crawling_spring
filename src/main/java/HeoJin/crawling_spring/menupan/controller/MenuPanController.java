package HeoJin.crawling_spring.menupan.controller;


import HeoJin.crawling_spring.service.menupan.MenuPanCrawlingService;
import HeoJin.crawling_spring.service.menupan.MenuPanRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/crawling/menupan")
@RequiredArgsConstructor
public class MenuPanController {

    private final MenuPanRecipeService menuPanRecipeService;
    private final MenuPanCrawlingService menuPanCrawlingService;

    @Value("${recipe.indexUrl.menu-pan.url}")
    private String menuPanIndexUrl;

    @PostMapping("/recipes")
    public ResponseEntity<String> menuPanCrawling() throws IOException {
        menuPanRecipeService.crawlingRecipeAboutMenupan();
        return ResponseEntity.ok("크롤링이 완료되었습니다.");
    }

    @PostMapping("/urls")
    public ResponseEntity<String> menuPanUrlCrawling(
            @RequestParam("difficulty") int difficulty,
            @RequestParam("startPage") int startPage,
            @RequestParam("endPage") int endPage) throws IOException {

        if (difficulty != 10 && difficulty != 20 && difficulty != 30) {
            return ResponseEntity.badRequest().body("difficulty는 10(쉬움), 20(보통), 30(어려움)만 가능합니다.");
        }
        String menupanBaseUrl = menuPanIndexUrl;
        String url = menupanBaseUrl.replace("{}", String.valueOf(difficulty));
        menuPanCrawlingService.crawlRecipeUrls(url, startPage, endPage);

        return ResponseEntity.ok("menuPan 크롤링이 종료되었습니다 (난이도: " + difficulty + ")");
    }
}