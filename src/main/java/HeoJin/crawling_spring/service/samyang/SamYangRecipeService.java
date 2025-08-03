package HeoJin.crawling_spring.service.samyang;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import org.springframework.data.mongodb.core.query.Query;


import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SamYangRecipeService {

    private final MongoTemplate mongoTemplate;
    @Value("${recipe.sites.samyang.url}")
    private String indexUrl;

    @Value("${recipe.sites.samyang.collection-name}")
    private String collectionName;

    @Value("${recipe.indexUrl.samyang.collection-name}")
    private String indexCollectionName;

    // url 조합 먼저 하면 될듯

    public void crawlingRecipeAboutSamYang(){
        List<Map> indexUrls = getAllRecipeUrlAsMap(indexCollectionName);

        for (Map map : indexUrls) {
            String siteIndex = (String) map.get("siteIndex");
            String url = indexUrl + siteIndex;

            crawledRecipeAboutSamYang(url, siteIndex);
        }


    }

    // site index 는 그냥 바로 넣으면 되는 거 아닌가?

    public List<Map> getAllRecipeUrlAsMap(String collectionName) {
        Query query = new Query();

        query.addCriteria(Criteria.where("isCrawled").is(false));

        return mongoTemplate.find(query, Map.class, collectionName);
    }

    public void crawledRecipeAboutSamYang(String baseUrl, String index){

        String sourceUrl = baseUrl;

        String siteIndex = index;

        Document document = new Document(sourceUrl);




    }



}
