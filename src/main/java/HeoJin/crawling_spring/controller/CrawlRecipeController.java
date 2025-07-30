package HeoJin.crawling_spring.controller;


import HeoJin.crawling_spring.service.okitchen.OkitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crawling/recipe")
@RequiredArgsConstructor
public class CrawlRecipeController {

    private final OkitchenService okitchenService;

    @PostMapping("/okitchen")
    public ResponseEntity<String> okitchenCrawling(
            @RequestParam("startIndex") Long startIndex,
            @RequestParam("lastIndex") Long lastIndex) {


        okitchenService.loopOkitchenUrl(startIndex, lastIndex);
        return ResponseEntity.ok("크롤링이 종료 되었습니다.");
    }
    
    // 뭐 먼저하지


}
