package com.guimu.alpha.serviceimpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.guimu.alpha.model.LogEsBase;
import com.guimu.alpha.service.EsService;
import com.guimu.alpha.utils.JacksonUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @Description: EsService 的实现类
 * @Author: Guimu
 * @Create: 2019/02/16 09:56:56
 **/
@Service("EsServiceImpl")
public class EsServiceImpl implements EsService {

    @Autowired
    private RestHighLevelClient client;
    private static final String CURRENT_MODEL_PACKAGE_NAME = "com.guimu.alpha.model.";
    private static final String DEFAULT_INDEX = "elasticsearch";

    @Override
    public boolean addOne(LogEsBase source) {
        IndexRequest singleRequest;
        boolean flag = false;
        try {
            singleRequest = indexRequestGenerater(source);
            IndexResponse indexResponse = client.index(singleRequest);
            flag = "created".equalsIgnoreCase(indexResponse.getResult().name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public List<LogEsBase> getAllByBase(LogEsBase condition) {
        return this.queryBaseByUserId(condition.getType(), condition.getUserId());
    }

    /**
     * @Description: 查找指定corpId和type的所有数据
     * @Param: [name, corpId]
     * @Return: java.util.List<com.yy.elasticsearch.model.Base>
     * @Author: Guimu
     * @Date: 2018/7/31  下午2:21
     */
    public List<LogEsBase> queryBaseByUserId(String type, String userId) {
        return baseQuery(this.getSearchRequestByUserId(type, userId));
    }

    /**
     * @Author: Guimu
     * @Description: 得到条件查询结果的id集合
     * @Param: [type, userId]
     * @Return: java.util.List<com.guimu.alpha.model.LogEsBase>
     * @Date: 2019-02-16 14:01
     */
    public List<String> queryIdsByUserId(String type, String userId) {
        return baseQueryIds(this.getSearchRequestByUserId(type, userId));
    }


    /**
     * @Author: Guimu
     * @Description: 根据type和userId得到es执行请求SearchRequest
     * @Param: [type, userId]
     * @Return: org.elasticsearch.action.search.SearchRequest
     * @Date: 2019-02-16 13:58
     */
    private SearchRequest getSearchRequestByUserId(String type, String userId) {
        MatchPhraseQueryBuilder mb = QueryBuilders.matchPhraseQuery("userId", userId);
        QueryBuilder queryBuilder = QueryBuilders.boolQuery().must(mb);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        SearchRequest searchRequest = new SearchRequest(DEFAULT_INDEX);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(this.getCount(searchRequest).intValue());
        searchRequest.types(type);
        searchRequest.source(searchSourceBuilder);
        return searchRequest;
    }

    /**
     * @Description: 根据Base 子类数据产生一个IndexRequest数据
     * @Param: [source]
     * @Return: org.elasticsearch.action.index.IndexRequest
     * @Author: Guimu
     * @Date: 2018/7/31  下午5:30
     */
    private IndexRequest indexRequestGenerater(LogEsBase source) {
        IndexRequest indexRequest = null;
        if (StringUtils.isEmpty(source.getIndex())) {
            source.setIndex(DEFAULT_INDEX);
        }
        try {
            String[] tempArr = source.getClass().getName().split("\\.");
            source.setType(tempArr[tempArr.length - 1]);
            indexRequest = new IndexRequest(source.getIndex(), source.getType());
            indexRequest.source(JacksonUtil.getString(source), XContentType.JSON);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return indexRequest;
    }

    /**
     * @Description: 私有的基础查询, 提高代码复用性
     * @Param: [searchRequest]
     * @Return: java.util.List<com.yy.elasticsearch.model.Base>
     * @Author: Guimu
     * @Date: 2018/7/31  下午6:15
     */
    private List<LogEsBase> baseQuery(SearchRequest searchRequest) {
        try {
            SearchResponse response = client.search(searchRequest);
            return Arrays.stream(response.getHits().getHits()).map(el -> {
                Map<String, Object> map = el.getSource();
                LogEsBase base = null;
                try {
                    base = JacksonUtil.getObject(JacksonUtil.getString(map), LogEsBase.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return base;
            }).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description: 私有的基础查询, 提高代码复用性,得到id集合
     * @Param: [searchRequest]
     * @Return: java.util.List<com.yy.elasticsearch.model.Base>
     * @Author: Guimu
     * @Date: 2018/7/31  下午6:15
     */
    private List<String> baseQueryIds(SearchRequest searchRequest) {
        try {
            SearchResponse response = client.search(searchRequest);
            return Arrays.stream(response.getHits().getHits()).map(SearchHit::getId)
                .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description: 获取该查询请求的总条数total
     * @Param: [searchRequest]
     * @Return: java.lang.Long
     * @Author: Guimu
     * @Date: 2018/7/31  下午4:58
     */
    private Long getCount(SearchRequest searchRequest) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(1);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse response = client.search(searchRequest);
            return response.getHits().getTotalHits();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1L;
    }

    @Override
    public boolean clearUserType(LogEsBase condition) {
        if (StringUtils.isEmpty(condition.getIndex())) {
            condition.setIndex(DEFAULT_INDEX);
        }
        List<String> idList = queryIdsByUserId(condition.getType(), condition.getUserId());
        if (CollectionUtils.isEmpty(idList)) {
            return false;
        }
        BulkRequest bulkRequest = new BulkRequest();
        idList.forEach(
            el -> bulkRequest
                .add(new DeleteRequest(condition.getIndex(), condition.getType(), el)));
        boolean flag = false;
        try {
            BulkResponse deleteResponse = client.bulk(bulkRequest);
            System.out.println(deleteResponse);
            flag = "deleted".equalsIgnoreCase("");
            System.out.println(deleteResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }
}
