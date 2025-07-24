package HeoJin.crawling_spring.service.tehnth;


import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenthRecipeService {

    private final MongoTemplate mongoTemplate;


    // 인덱스 기반으로 크롤링 하는 함수

    // mongo 에서 url 가져오는 함수
}
