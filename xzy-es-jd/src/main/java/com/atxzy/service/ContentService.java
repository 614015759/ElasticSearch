package com.atxzy.service;

import com.alibaba.fastjson.JSON;
import com.atxzy.pojo.Content;
import com.atxzy.utils.HtmlParseUtil;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ContentService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    /**
     * 将爬虫爬到的数据存入数据库中
     * @param keyWords
     * @return
     * @throws Exception
     */
    public boolean parseContent(String keyWords) throws Exception {
        List<Content> contents = new HtmlParseUtil().parseJD(keyWords);

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for(Content content: contents){
            if(!content.getTitle().equals("")) {
                System.out.println(content);
                bulkRequest.add(new IndexRequest("jd_goods").source(JSON.toJSONString(content), XContentType.JSON));
            }
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);


        return !bulk.hasFailures();
    }

    /**
     * 获取es中的数据  实现搜索功能
     */

    public    List<Map<String,Object>> searchPage(String keyWords,int from,int page) throws IOException {
        if(from <= 1){
            from = 1;
        }
        //条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("title", keyWords);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(page);
        searchSourceBuilder.query(termQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String,Object>> response = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            response.add(sourceAsMap);
        }
        return response;

    }



    /**
     * 获取es中的数据  实现搜索功能
     */

    public    List<Map<String,Object>> searchPageHighLight(String keyWords,int from,int page) throws IOException {
        if(from <= 1){
            from = 1;
        }
        //条件搜索
        SearchRequest searchRequest = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("title", keyWords);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(page);
        searchSourceBuilder.query(termQueryBuilder);

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        List<Map<String,Object>> response = new ArrayList<>();
        for (SearchHit hit : search.getHits().getHits()) {


            //获取高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//原来的结果

            //解析高亮字段   将原来的字段换为高亮字段即可
        if(title != null){
            Text[] fragments = title.fragments();
            String newTitle = "";

            for(Text text:fragments){
                newTitle += text;
            }
            sourceAsMap.put("title",newTitle); //高亮字段替换掉原来的内容
        }
            response.add(sourceAsMap);

        }
        return response;

    }
}
