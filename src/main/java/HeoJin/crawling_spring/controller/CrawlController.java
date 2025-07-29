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
public class CrawlController {

    private final OkitchenService okitchenService;
    private final TenthRecipeUrlService tenthRecipeService;
    private final MenuPanCrawlingService menuPanCrawlingService;
    private final SamYangUrlService samYangUrlService;
    private final HansikUrlCrawlingService hansikUrlCrawlingService;

    @Value("${recipe.indexUrl.menu-pan.url}")
    private String menuPanIndexUrl;


    @PostMapping("/okitchen")
    public ResponseEntity<String> okitchenCrawling(
            @RequestParam("startIndex") Long startIndex,
            @RequestParam("lastIndex") Long lastIndex) {


        okitchenService.loopOkitchenUrl(startIndex, lastIndex);
        return ResponseEntity.ok("크롤링이 종료 되었습니다.");
    }

    @PostMapping("/tenthRecipes")
    public ResponseEntity<String> tenthRecipesCrawling() throws IOException {

        tenthRecipeService.crawlRecipeUrls(1, 10);
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
            @RequestParam("startPage") int startPage,
            @RequestParam("endPage") int endPage) throws IOException {

        samYangUrlService.crawlRecipeUrls( startPage, endPage);
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