package com.atxzy.esapi;

import com.alibaba.fastjson.JSON;
import com.atxzy.esapi.pojo.User;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.naming.directory.SearchResult;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * es API 7.6.x  高级客户端API测试
 */
@SpringBootTest
class EsApiApplicationTests {

    //面向对象操作

    @Autowired
    private RestHighLevelClient restHighLevelClient ;

    //测试索引的创建 Request
    @Test
    void testCreateIndex() throws IOException {
        //创建索引  请求 PUT kuang_index
        CreateIndexRequest request = new CreateIndexRequest("kuang_index");
        // 客户端执行创建请求IndicateClient 请求后获得响应
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    //判断索引是否存在
    @Test
    boolean testExistIndex(String index) throws IOException {
        //GET kuang_index
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
        if(exists){
           return true;
        }else{
            return  false;
        }

    }

    @Test
    void testDelete() throws IOException{
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("test1");
        //删除
        AcknowledgedResponse delete = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }


    //测试添加文档
    @Test
    void testAddDocument() throws IOException {
        //创建对象
        User user = new User("狂神说", 3);
        //创建请求
        IndexRequest request = new IndexRequest("kuang_index");

        //规则 put /kuang_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        request.timeout("1s");

        //将我们的数据放入请求json
        //因为这里要将对象转化为json  所以要引入fastjson依赖
        IndexRequest source = request.source(JSON.toJSONString(user), XContentType.JSON);

        //客户端发送请求，获取响应结果
        IndexResponse index = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        System.out.println(index.toString());
        System.out.println(index.status()); //对应命令返回的状态

    }


    //测试获取文档
    @Test
    void testGetDocument() throws IOException {
        //判断是否存在
        if (testExistIndex("kuang_index")) {
            GetRequest getRequest = new GetRequest("kuang_index", "1");
            //不获取返回上下文   _source的上下文
            getRequest.fetchSourceContext(new FetchSourceContext(false));
            getRequest.storedFields("_none_");
            boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
            System.out.println(exists);

        }
    }

        //获取文档信息
        @Test
        void testDocumentContent() throws IOException {
            GetRequest getRequest = new GetRequest("kuang_index", "2");
            GetResponse response = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
            System.out.println(response.getSourceAsString());//打印文档内容
    }


    @Test
    void testUpdate() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("kuang_index", "1");
       updateRequest.timeout("1s");
        User user = new User("狂神说java", 15);
        updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);
        restHighLevelClient.update(updateRequest,RequestOptions.DEFAULT);
    }

    @Test
    void testDelete1() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("kuang_index","1");
        deleteRequest.timeout("1s");
        DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(delete.status());
    }

    //真实项目一般会大量插入
    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        List<User> list = new ArrayList<>();
        list.add(new User("kuang1",3));
        list.add(new User("shabi",3));
        list.add(new User("wangyiu",3));
        list.add(new User("傻逼",3));
        list.add(new User("二傻子",3));
        list.add(new User("你妈死了",3));
        list.add(new User("你就就死了",3));
        list.add(new User("你身份证就是全家福",3));
        for(int i = 0;i<list.size();i++){
            bulkRequest.add(new IndexRequest("kuang_index").source(JSON.toJSONString(list.get(i)),XContentType.JSON));
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulk.hasFailures());
    }

    //查询测试
    //searchRequest查询请求
    //searchBuilder查询条件构造list.add(new User("kuang1",3));
    //HighLightBuilder构建高亮

    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("kuang_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询条件，可以使用builders快速匹配
        //精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "你");
        //匹配所有文件
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(5);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSON(search.getHits()));
        System.out.println("==================================================");
for(SearchHit documentFields:search.getHits().getHits()){
    System.out.println(documentFields.getSourceAsMap());
}
    }

}
