package HeoJin.crawling_spring.tenth.controller;


import HeoJin.crawling_spring.tenth.service.TenthRecipeService;
import HeoJin.crawling_spring.tenth.service.TenthRecipeUrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/crawling/tenth")
@RequiredArgsConstructor
public class TenthController {

    private final TenthRecipeService tenthRecipeService;
    private final TenthRecipeUrlService tenthRecipeUrlService;

    @PostMapping("/recipes")
    public ResponseEntity<String> tenthRecipeCrawling() throws Exception {
        tenthRecipeService.crawlingRecipeAboutTenth();
        return ResponseEntity.ok("크롤링이 완료되었습니다.");
    }

    @PostMapping("/urls")
    public ResponseEntity<String> tenthRecipesCrawling(
            @RequestParam("startPage") int startPage,
            @RequestParam("lastPage") int lastPage
    ) throws IOException {
        tenthRecipeUrlService.crawlRecipeUrls(startPage, lastPage);
        return ResponseEntity.ok("크롤링이 종료되었습니다");
    }
}