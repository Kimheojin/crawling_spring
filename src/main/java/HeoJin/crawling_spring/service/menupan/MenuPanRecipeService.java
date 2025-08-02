package HeoJin.crawling_spring.service.menupan;


import lombok.RequiredArgsConstructor;

import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuPanRecipeService {

    private MongoTemplate mongoTemplate;

    @Value("${recipe.sites.menupan.url}")
    private String sourceUrl;

    @Value("${recipe.sites.menupan.collection-name}")
    private String collectionName;

    public void crawlingRecipeAboutMenupan(){
        // url mongo 에서 가져온 다음에 합치는 메소드

        List<Map> indexUrls = getAllRecipeUrlAsMap(collectionName);

        for(Map map : indexUrls){
            String siteIndex = (String) map.get("siteIndex");
            String url  = sourceUrl + siteIndex;

            crawledRecipe(url, siteIndex);
        }
    }

    public List<Map>  getAllRecipeUrlAsMap(String collectionName) {
        Query query = new Query();

        query.addCriteria(Criteria.where("isCrawled").is(false));

        return mongoTemplate.find(query, Map.class, collectionName);
    }

    private void crawledRecipe(String acceptUrl, String siteIndex){
        String sourceUrl = acceptUrl;
        Document document = new Document(sourceUrl);

        // 음식 명
        // 사이트 인덱스

        // 재료 목록

        // 조리 순서
        // 조리 시간

    }
}
