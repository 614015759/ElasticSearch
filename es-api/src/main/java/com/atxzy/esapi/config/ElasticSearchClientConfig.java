package com.atxzy.esapi.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//spring两步  1、找对象  2、放到spring中待用
@Configuration  //xml  -  bean
public class ElasticSearchClientConfig {

    //elk
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient client = new RestHighLevelClient(
        RestClient.builder(
                new HttpHost("39.99.136.0",9200,"http")));

        return client;
    }


}
