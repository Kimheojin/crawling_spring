package HeoJin.crawling_spring.controller;


import HeoJin.crawling_spring.service.hansik.HansikRecipeService;
import HeoJin.crawling_spring.service.menupan.MenuPanRecipeService;
import HeoJin.crawling_spring.service.okitchen.OkitchenService;
import HeoJin.crawling_spring.service.samyang.SamYangRecipeService;
import HeoJin.crawling_spring.service.tehnth.TenthRecipeService;
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
    private final TenthRecipeService tenthRecipeService;
    private final HansikRecipeService hansikRecipeService;
    private final MenuPanRecipeService menuPanRecipeService;
    private final SamYangRecipeService samYangRecipeService;


    @PostMapping("/okitchen")
    public ResponseEntity<String> okitchenCrawling(
            @RequestParam("startIndex") Long startIndex,
            @RequestParam("lastIndex") Long lastIndex) {
        okitchenService.loopOkitchenUrl(startIndex, lastIndex);
        return ResponseEntity.ok("크롤링이 종료 되었습니다.");
    }
    
    // collection 명에서 읽어오는 거 service 로직에서 해야하나

    @PostMapping("/tenthRecipes")
    public ResponseEntity<String> tenthRecipeCrawling() throws Exception {
        tenthRecipeService.crawlingRecipeAboutTenth();
        return ResponseEntity.ok("크롤링이 완료되었습니다.");
    }

    @PostMapping("/menuPan")
    public ResponseEntity<String> menuPanCrawling(){
        return ResponseEntity.ok("크롤링이 완료되었습니다.");
    }

    @PostMapping("/samYang")
    public ResponseEntity<String> samYangCrawling(){
        return ResponseEntity.ok("크롤링이 완료되었습니다.");
    }

    @PostMapping("/hansik")
    public ResponseEntity<String> hansikCrawling(){
        return ResponseEntity.ok("크롤링이 완료되었습니다.");
    }


}
