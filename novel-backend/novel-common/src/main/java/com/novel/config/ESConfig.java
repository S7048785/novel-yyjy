package com.novel.config;

import jakarta.annotation.PreDestroy;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.elasticsearch.client.RestClient;


import java.io.IOException;

@Configuration
public class ESConfig {
	@Value("${es.host:192.168.229.128}")
	private String host;
	
	@Value("${es.port:9200}")
	private int port;
	
	@Value("${es.connect-timeout:5000}")
	private int connectTimeout;
	
	@Value("${es.socket-timeout:30000}")
	private int socketTimeout;
	
	private RestHighLevelClient client;
	
	@Bean
	public RestHighLevelClient restHighLevelClient() {
		RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, "http"))
				                            .setRequestConfigCallback(req -> req
						                                                             .setConnectTimeout(connectTimeout)
						                                                             .setSocketTimeout(socketTimeout));
		client = new RestHighLevelClient(builder);
		warmUp();          // 启动即预热
		return client;
	}
	
	private void warmUp() {
		try {
			client.search(new org.elasticsearch.action.search.SearchRequest("book")
					              .source(new org.elasticsearch.search.builder.SearchSourceBuilder()
							                      .size(0)
							                      .query(org.elasticsearch.index.query.QueryBuilders.matchAllQuery())),
					org.elasticsearch.client.RequestOptions.DEFAULT);
		} catch (Exception ignore) {}
	}
	
	@PreDestroy
	public void close() throws IOException {
		if (client != null) client.close();
	}
}
