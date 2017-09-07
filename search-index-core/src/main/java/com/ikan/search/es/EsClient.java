package com.ikan.search.es;

import java.io.IOException;
import java.lang.reflect.Constructor;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class EsClient {
	private static EsClient instance = new EsClient();
	private static TransportClient client;
    private static Settings settings = ImmutableSettings.settingsBuilder()
	        .put("cluster.name", "ikan-products-es")
	        .put("client.transport.sniff", true)
	         //心跳包发送时间
	        .put("client.transport.nodes_sampler_interval",10).build();
	static {
        try {
            Class<?> clazz = Class.forName(TransportClient.class.getName());
            Constructor<?> constructor = clazz.getDeclaredConstructor(Settings.class);
            constructor.setAccessible(true);
            client = (TransportClient) constructor.newInstance(settings);
            client.addTransportAddress(new InetSocketTransportAddress(
                    "127.0.0.1", 9300));
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
	
	private EsClient () {
	}
	
	
	public static EsClient getInstance() {
		return instance;
	}
	
	public TransportClient getElasticSearchClient() {
		return client;
	}
	
	public static void main(String[] orgs) {
//		String json = "{" +
//		        "\"name\":\"孩之宝的变形金刚101\"," +
//		        "\"description\":\"80后集体的回忆,孩之宝变形金刚\"" +
//		    "}";
//		IndexResponse ir = EsClient.getInstance().getElasticSearchClient()
//				.prepareIndex("products", "toys")
//				.setSource(json).execute().actionGet();
//		System.out.println(ir.toString());
//		
//		SearchResponse sr = EsClient.getInstance().getElasticSearchClient()
//		.prepareSearch("products")
//		.setTypes("toys")
//		.setQuery(QueryBuilders.queryString("h"))
//		.setFrom(0).setSize(60).setExplain(true)
//		.execute()
//        .actionGet();
//		SearchHits hits = sr.getHits();
//		for (SearchHit hit : hits) {
//			System.out.println(hit.id());
//			System.out.println(hit.getSourceAsString());
//			System.out.println("#########################");
//		}
		
		
	}
	
	
	public static boolean createIndex(String indexName) {
		CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
		getInstance().getElasticSearchClient().admin().indices().create(createIndexRequest);
		return true;
	}
	
	
	
	public static void createToyMapper(String type,String indices) throws IOException {
		XContentBuilder builder=XContentFactory.jsonBuilder()
								.startObject()
									.startObject(type)
										.startObject("properties")
											.startObject("id").field("type", "integer").field("store", "yes").endObject()
											.startObject("productname").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
											.startObject("productdescription").field("type", "string").field("store", "yes").field("indexAnalyzer", "ik").field("searchAnalyzer", "ik").endObject()
										.endObject()
									.endObject()
								.endObject();
		PutMappingRequest mapping = Requests.putMappingRequest(indices).type(type).source(builder);
		getInstance().getElasticSearchClient().admin().indices().putMapping(mapping).actionGet();
		client.close();
	}
	
	
}
