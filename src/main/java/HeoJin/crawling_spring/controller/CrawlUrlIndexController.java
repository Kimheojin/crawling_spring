package HeoJin.crawling_spring.controller;

import HeoJin.crawling_spring.service.hansik.HansikUrlCrawlingService;
import HeoJin.crawling_spring.service.menupan.MenuPanCrawlingService;
import HeoJin.crawling_spring.service.okitchen.OkitchenService;
import HeoJin.crawling_spring.service.samyang.SamYangUrlService;
import HeoJin.crawling_spring.service.tehnth.TenthRecipeUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/crawling")
@RequiredArgsConstructor
public class CrawlUrlIndexController {

    private final TenthRecipeUrlService tenthRecipeService;
    private final MenuPanCrawlingService menuPanCrawlingService;
    private final SamYangUrlService samYangUrlService;
    private final HansikUrlCrawlingService hansikUrlCrawlingService;

    @Value("${recipe.indexUrl.menu-pan.url}")
    private String menuPanIndexUrl;

    @Value("${recipe.indexUrl.samyang.url}")
    private String samYangUrl;




    @PostMapping("/tenthRecipes")
    public ResponseEntity<String> tenthRecipesCrawling(
            @RequestParam("startPage") int startPage,
            @RequestParam("lastPage") int lastPage
    ) throws IOException {

        tenthRecipeService.crawlRecipeUrls(startPage, lastPage);
        return ResponseEntity.ok("크롤링이 종료되었습니다");
    }

    @PostMapping("/menuPan")
    public ResponseEntity<String> menuPanCrawling(
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

    @PostMapping("/samYang")
    public ResponseEntity<String> samYangCrawling(
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

    @PostMapping("/hansik")
    public ResponseEntity<String> hanSikCrawling(
            @RequestParam("startPage") int startPage,
            @RequestParam("endPage") int endPage
    ) throws IOException {

        hansikUrlCrawlingService.crawlRecipeUrls( startPage, endPage);
        return ResponseEntity.ok("한식 크롤링이 종료되었습니다.");
    }
}