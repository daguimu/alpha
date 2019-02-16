package com.guimu.alpha.config;

/**
 * @description:
 * @author: Guimu
 * @create: 2018/07/31 01:14:58
 **/

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class ElasticsearchConfiguration implements FactoryBean<RestHighLevelClient>,
    InitializingBean, DisposableBean {

    //@Value("${spring.data.elasticsearch.cluster-nodes}")
    private String clusterNodes = "127.0.0.1:9200";
//    private String clusterNodes = "172.20.10.2:9200";

    private RestHighLevelClient restHighLevelClient;
    private static RestClient restClient;

    /**
     * 控制Bean的实例化过程
     *
     * @return
     * @throws Exception
     */
    @Override
    public RestHighLevelClient getObject() {
        return restHighLevelClient;
    }

    /**
     * 获取接口返回的实例的class
     *
     * @return
     */
    @Override
    public Class<?> getObjectType() {
        return RestHighLevelClient.class;
    }

    @Override
    public void destroy() throws Exception {
        try {
            if (restClient != null) {
                restClient.close();
            }
        } catch (final Exception e) {
            System.out.println("Error closing ElasticSearch client: ");
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() {
        restHighLevelClient = buildClient();
    }

    private RestHighLevelClient buildClient() {
        try {
            RestClientBuilder restClientBuilder = RestClient.builder(
                    new HttpHost(
                            clusterNodes.split(":")[0],
                            Integer.parseInt(clusterNodes.split(":")[1]),
                            "http")).setRequestConfigCallback((builder) -> {
                builder.setConnectTimeout(5000);
                builder.setSocketTimeout(40000);
                builder.setConnectionRequestTimeout(1000);
                return builder;
            }).setMaxRetryTimeoutMillis(5 * 60 * 1000);
            restClient = restClientBuilder.build();
            restHighLevelClient = new RestHighLevelClient(restClient);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return restHighLevelClient;
    }

    public static RestClient getRestClient() {
        return restClient;
    }
}





