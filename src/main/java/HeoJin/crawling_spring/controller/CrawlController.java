package HeoJin.crawling_spring.controller;


import HeoJin.crawling_spring.service.okitchen.OkitchenService;
import HeoJin.crawling_spring.service.tehnth.TenthRecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crawling")
@RequiredArgsConstructor
public class CrawlController {

    private final OkitchenService okitchenService;
    private final TenthRecipeService tenthRecipeService;

    private final String OKITCHEN_URL = "https://www.okitchen.co.kr/category/detail?idx=";
    private final String COLLECTION_NAME = "TEST1";

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
    public ResponseEntity<String> tenthRecipesCrawling() {

        // 이거 url 생성 코드 먼저 돌린 다음에
        // 저번에 추천

        return ResponseEntity.ok("크롤링이 종료되었습니다");
    }

    // 삼양 공식


    
}
