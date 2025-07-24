package HeoJin.crawling_spring.controller;


import HeoJin.crawling_spring.service.menupan.MenuPanCrawlingService;
import HeoJin.crawling_spring.service.okitchen.OkitchenService;
import HeoJin.crawling_spring.service.tehnth.TenthRecipeUrlService;
import lombok.RequiredArgsConstructor;
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


    private final String OKITCHEN_URL = "https://www.okitchen.co.kr/category/detail?idx=";
    private final String RECIPEABOUT10000 = "https://www.10000recipe.com/issue/view.html?cid=gdubu33&types=magazine&page=";

    // 이거 난이도 별로
    private final String MENUPAN_BASE_URL = "https://www.menupan.com/Cook/recipere.asp?difficulty={}&page=";



    // 오키친
    @PostMapping("/okitchen")
    public ResponseEntity<String> okitchenCrawling(
            @RequestParam("startIndex") Long startIndex,
            @RequestParam("lastIndex") Long lastIndex){
        // 인덱스 범위만 받고 하면 되지 않을가??
        okitchenService.loopOkitchenUrl(OKITCHEN_URL, startIndex, lastIndex);
        // 일반적으로 1 ~ 1500 만 하면 될듯
        return ResponseEntity.ok("크롤링이 종료 되었습니다.");
    }

    // 10000개의 레시피
    @PostMapping("/tenthRecipes")
    public ResponseEntity<String> tenthRecipesCrawling() throws IOException {

        // url 리스트 생성 코드
        tenthRecipeService.crawlRecipeUrls(RECIPEABOUT10000, 1, 10);

        // 리스트 읽고 10000 레시피 생성

        return ResponseEntity.ok("크롤링이 종료되었습니다");
    }

    // menuPan
    @PostMapping("/menuPan")
    public ResponseEntity<String> menuPanCrawling(
            @RequestParam("difficulty") int difficulty,
            @RequestParam("startPage") int startPage,
            @RequestParam("endPage") int endPage) throws IOException {

        // difficulty 값 검증
        if (difficulty != 10 && difficulty != 20 && difficulty != 30) {
            return ResponseEntity.badRequest().body("difficulty는 10(쉬움), 20(보통), 30(어려움)만 가능합니다.");
        }

        // URL 생성
        String url = MENUPAN_BASE_URL.replace("{}", String.valueOf(difficulty));

        // 크롤링 실행
        menuPanCrawlingService.crawlRecipeUrls(url, startPage, endPage);

        return ResponseEntity.ok("menuPan 크롤링이 종료되었습니다 (난이도: " + difficulty + ")");
    }

}
