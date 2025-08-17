package HeoJin.crawling_spring.hansik.controller;

import HeoJin.crawling_spring.hansik.service.HansikRecipeService;
import HeoJin.crawling_spring.hansik.service.HansikUrlCrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/crawling/hansik")
@RequiredArgsConstructor
public class HansikController {

    private final HansikRecipeService hansikRecipeService;
    private final HansikUrlCrawlingService hansikUrlCrawlingService;


    // 인덱스 크롤링
    @PostMapping("/urls")
    public ResponseEntity<String> hanSikUrlCrawling(
            @RequestParam("startPage") int startPage,
            @RequestParam("endPage") int endPage
    ) throws IOException {
        hansikUrlCrawlingService.crawlRecipeUrls( startPage, endPage);
        return ResponseEntity.ok("한식 크롤링이 종료되었습니다.");
    }

    // 데이터 크롤링
    @PostMapping("/data")
    public ResponseEntity<String> hansikDataCrawling() throws IOException {
        hansikRecipeService.loopHansikUrl();
        return ResponseEntity.ok("크롤링이 완료되었습니다.");
    }
}