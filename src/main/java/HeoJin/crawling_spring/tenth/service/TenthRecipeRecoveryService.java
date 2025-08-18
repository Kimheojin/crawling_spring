package HeoJin.crawling_spring.tenth.service;


import HeoJin.crawling_spring.common.entity.recipe.Recipe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenthRecipeRecoveryService {

    private final MongoTemplate mongoTemplate;
    private final TenthRecipeService tenthRecipeService;

    @Value("${recipe.sites.recipe10000.collection-name}")
    private String RecipeCollectionName;

    @Value("${recipe.indexUrl.recipe10000.collection-name}")
    private String indexCollectionName;


    @Value("${recipe.sites.recipe10000.url}")
    private String baseUrl;





    public void recoveryData() throws Exception {

        long siteCount = mongoTemplate.count(new Query(Criteria.where("siteIndex").exists(true)), RecipeCollectionName);
        long hrefCount = mongoTemplate.count(new Query(Criteria.where("hrefIndex").exists(true)), RecipeCollectionName);

        log.info("siteIndex count: {}, hrefIndex count: {}", siteCount, hrefCount);

        // siteIndex만 가져오기
        Query query1 = new Query(Criteria.where("siteIndex").exists(true));
        query1.fields().include("siteIndex"); // projection
        // object Id 는 들어올듯
        List<Object> siteIndexList = mongoTemplate.find(query1, Object.class, RecipeCollectionName);// Bson으로 들어옴



        Query query2 = new  Query(Criteria.where("hrefIndex").exists(true));
        query2.fields().include("hrefIndex"); // 결과에 hrefIndex 필드만 포함
        List<Object> hrefIndexList = mongoTemplate.find(query2, Object.class, indexCollectionName);

        HashSet<String> dataIndex = siteIndexList.stream()
                .map(doc -> ((Map<String, Object>) doc).get("siteIndex").toString())
                .collect(Collectors.toCollection(HashSet::new));

        for (Object object : hrefIndexList) {
            Map<String, Object> hrefDoc = (Map<String, Object>) object;
            String hrefIndex = hrefDoc.get("hrefIndex").toString();
            if(dataIndex.contains(hrefIndex)){
                continue;
            }else {
                String sourceUrl = baseUrl + hrefIndex;
                tenthRecipeService.crawlSingleRecipe(sourceUrl, hrefIndex);

            }
        }




    }

}
