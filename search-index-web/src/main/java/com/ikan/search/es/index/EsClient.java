package com.ikan.search.es.index;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.format.ISODateTimeFormat;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptService.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

@Component("esutils")
public class EsClient {

	private static Logger LOGGER = LoggerFactory.getLogger( EsClient.class );
	public static final String TYPE = "es_type";
	//TODO 测试环境
	private static final String INDEX = "ikan-test";
	//TODO 正式环境
//	private static String INDEX = "ikan-new-v1";
	public static final String TYPE_GOODS = "ikan-products";
	public static final String TYPE_ALBUMS = "ikan-albums";
	
	static Settings settings = ImmutableSettings.settingsBuilder()
			.put( "cluster.name" , "ikan-es" )
			.put( "client.transport.sniff" , true)
			.put( "client.transport.ignore_cluster_name", true).build();

	
	private static TransportClient CLIENT;
	@Value("${es.retry_on_confict}")
	private int  RETRY_ON_CONFLICT;
	@Value("${es.bulk.batch.size}")
	private int BATCH_SIZE;
	@Value("${es.bulk.actions}")
	private int BULK_ACTION;
	@Value("${es.hosts}")
	private String esHost;
	
	@PostConstruct
	protected void init() {
		try {
			Class<?> clazz = Class.forName(TransportClient.class.getName());
			Constructor<?> constructor = clazz.getDeclaredConstructor(new Class[] { Settings.class });
			constructor.setAccessible(true);
			String[] esHosts = esHost.split(";");
			CLIENT = (TransportClient) constructor.newInstance(new Object[] { settings });
			for(String h : esHosts){
				String host = h.split(":")[0];
				int port = Integer.parseInt(h.split(":")[1]);
				CLIENT.addTransportAddress(new InetSocketTransportAddress(host,port));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @return
	 */
	public BulkProcessor getBulkProcessor(){
		return BulkProcessor.builder(CLIENT, new BulkProcessor.Listener() {
			
			@Override
			public void beforeBulk(long executionId, BulkRequest request) {
				LOGGER.info( String.format( "exeid: [%s], beforeBulk. action size : %s ", executionId, request.numberOfActions() ) );
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
				LOGGER.error( "[afterBulk] exeid : " + executionId + " error.", failure );
			}
			
			@Override
			public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
				if( response.hasFailures() ) LOGGER.error( String.format( "exeid: [%s], afterBulk. response : %s ", executionId, response.buildFailureMessage() ) );
			}
		}).setConcurrentRequests(Runtime.getRuntime().availableProcessors() )
		.setBulkActions(BULK_ACTION)
		.setFlushInterval( TimeValue.timeValueSeconds( 30 ) )
		.build();
	}
	
	/**
	 * es client
	 * @return
	 */
	public Client getIndexClient(){
		return CLIENT;
	}
	
	/**
	 * 索引添加
	 * @throws ElasticsearchException
	 * @throws IOException
	 */
	public  void createIndex() throws ElasticsearchException, IOException {
		Client client = getIndexClient();
		IndicesExistsResponse response = client.admin().indices().prepareExists( INDEX ).execute().actionGet();
		if(response.isExists()) return;
		client.admin().indices().prepareCreate( INDEX ).execute().actionGet();
	}
	
	/**
	 * 删除 index
	 */
	public void deleteIndex(){
		Client client = getIndexClient();
		client.admin().indices().prepareDelete( INDEX ).execute();
	}
	
	/**
	 * 索引更新
	 * @param id
	 * @param json 
	 */
	public < T extends  Object> void updateIndex(String typeName, String id, Map<String, T> json ){
		Client client = getIndexClient();
		UpdateResponse response = client.prepareUpdate( INDEX, typeName, id )
				.setDoc( json )
				.setRetryOnConflict( RETRY_ON_CONFLICT )
				.execute()
				.actionGet();
		LOGGER.info( "id : "  + response.getId() + " updated" );
	}
	
	/**
	 * 更新添加
	 * @param id
	 * @param json
	 */
	public < T extends  Object> void upsertIndex( String typeName, String id, Map<String, T> json ){
		Client client = getIndexClient();
		UpdateResponse response = client.prepareUpdate( INDEX, typeName, id )
				.setDoc( json )
				.setUpsert( json )
				.setRetryOnConflict( RETRY_ON_CONFLICT )
				.execute()
				.actionGet();
		LOGGER.info( "id : "  + response.getId() + " upserted" );
	}
	
	
	/**
	 * update Builder 
	 * @param id
	 * @param script
	 * @return
	 */
	public UpdateRequestBuilder updateBuilder( String typeName, String id , String script ){
		Client client = getIndexClient();
		return client.prepareUpdate( INDEX, typeName, id )
				.setScript( script,ScriptType.FILE )
				//.setScriptLang( DEFAULT_SCRIPT_LANGUAGE )
				. setRetryOnConflict( RETRY_ON_CONFLICT );
	}
	
	/**
	 * update builder 
	 * @param id
	 * @param json
	 * @return
	 */
	public < T extends  Object > UpdateRequestBuilder updateBuilder( String typeName, String id ,  Map<String, T> json ){
		Client client = getIndexClient();
		return client.prepareUpdate( INDEX, typeName, id ) .setDoc( json ).setRetryOnConflict( RETRY_ON_CONFLICT );
	}
	
	/**
	 * bulk update
	 * { id : script}
	 * @param scripts
	 * @return
	 */
	public String updateIndexBulk( String typeName, Map<String, List<String>> scripts ){
		Client client = getIndexClient();
		BulkRequestBuilder bulk = client.prepareBulk();
		for( Map.Entry< String, List<String>> entry : scripts.entrySet() ){
			for( String script : entry.getValue() ){
				bulk.add( client.prepareUpdate( INDEX, typeName, entry.getKey() ).setScript( script,ScriptType.FILE ) ) ;
			}
		}
		BulkResponse bulkResponse = ( BulkResponse ) bulk.execute().actionGet();
		if ( bulkResponse.hasFailures() ) {
			LOGGER.error( bulkResponse.buildFailureMessage() );
			BulkItemResponse[] items = bulkResponse.getItems() ;
			for( BulkItemResponse item : items){
				return item.getId() ;
			}
		}
		return null;
	}
	
	/**
	 * build update
	 * @param updateBuilders
	 * @return
	 */
	public ListenableActionFuture<BulkResponse>  updateIndexBulk( List<UpdateRequestBuilder> updateBuilders ){
		Client client = getIndexClient();
		BulkRequestBuilder bulk = client.prepareBulk();
		for( UpdateRequestBuilder u : updateBuilders ){
			bulk.add( u );
		}
		return bulk.execute();
	}
	
	/**
	 * build update
	 * @param updateBuilders
	 * @return
	 */
	public String bulkUpdateIndex( List<UpdateRequestBuilder> updateBuilders ){
		Client client = getIndexClient();
		BulkRequestBuilder bulk = client.prepareBulk();
		for( UpdateRequestBuilder u : updateBuilders ){
			bulk.add( u );
		}
		
		BulkResponse bulkResponse = ( BulkResponse ) bulk.execute().actionGet();
		if ( bulkResponse.hasFailures() ) {
			LOGGER.error( bulkResponse.buildFailureMessage() );
			BulkItemResponse[] items = bulkResponse.getItems() ;
			for( BulkItemResponse item : items){
				return item.getId() ;
			}
		}
		return null;
	}
	
	/**
	 * 添加到 buld Processor
	 * @param action
	 */
	public void bulkAdd( BulkProcessor bp, IndexRequest index ){
		bp.add( index );
	}
	
	/**
	 * updateRequest 
	 * @param id
	 * @param json
	 * @return
	 */
	public UpdateRequest updateRequest( String typeName, String id, Map<String, Object> json ){
		return getIndexClient()
		.prepareUpdate( INDEX, typeName, id )
		.setDoc( json )
		.setUpsert( json )
		.setRetryOnConflict( RETRY_ON_CONFLICT )
		.request();
	}
	
	/**
	 * index Request
	 * @param id
	 * @param json
	 * @return
	 */
	public IndexRequest indexRequest( String typeName, String id, Map<String, Object> json ){
		return getIndexClient()
		.prepareIndex( INDEX, typeName, id )
		.setSource( json )
		.request();
	}
	
	public String updateIndex(String typeName,  Map<String, Map<String, Object>> json  ){
		Client client = getIndexClient();
		for( Map.Entry<String, Map<String, Object>> j : json.entrySet() ){
			client.prepareUpdate( INDEX, typeName, j.getKey() ).setDoc( j.getValue() ).execute().actionGet();
		}
		return null;
	}
	
	public void addIndex( String typeName,String id, Map<String, Object> json ){
		Client client = getIndexClient();
		IndexRequestBuilder builder = client.prepareIndex().setIndex( INDEX ).setType( typeName );
		builder.setId( id ).setSource( json );
		builder.execute();
	}
	
	public String updateIndex( String id, String script, String type ){
		Client client = getIndexClient();
		client.prepareUpdate(  INDEX, type, id ).setScript( script,ScriptType.FILE ).execute().actionGet();
		return null;
	}
	
	
	/**
	 * 设置副本数更新之前设置为0, -1
	 * 之后应该设置 为1  1s
	 * @param replicas
	 */
	public void setReplicas( int replicas ){
		Client client = getIndexClient();
		Settings settings = ImmutableSettings.settingsBuilder()
				.put( "number_of_replicas" , replicas )
				//.put( "refresh_interval", "-1" )
				.build();
		client.admin().indices().prepareUpdateSettings( INDEX ).setSettings( settings ).execute();
	}
	
	
	/**
	 * 批量添加索引
	 * @param updateBuilders
	 * @return
	 */
	public String IndexBulk( List<IndexRequestBuilder> IndexBuilders ){
		Client client = getIndexClient();
		BulkRequestBuilder bulk = client.prepareBulk();
		int i = 0;
		for( IndexRequestBuilder u : IndexBuilders ){
			bulk.add( u );
			if( bulk.numberOfActions() == BATCH_SIZE ){
				bulk.execute().actionGet();
				bulk = client.prepareBulk();
				LOGGER.info( "add  : " + i++ );
			}
		}
		BulkResponse bulkResponse = (BulkResponse) bulk.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			System.out.println( bulkResponse.buildFailureMessage() );
			return bulkResponse.buildFailureMessage();
		}
		return null;
	}
	
	/**
	 * index builder
	 * @param id
	 * @param json
	 * @return
	 */
	public IndexRequestBuilder indexBuilder( String typeName, String id , Map<String, Object> json ){
		return getIndexClient()
				.prepareIndex( INDEX, typeName, id )
				.setSource( json );
	}
	
	/**
	 * 删除
	 * @param id
	 */
	public void delete(String typeName, String id ){
		DeleteResponse response = getIndexClient().prepareDelete(  INDEX, typeName, id ).execute().actionGet();
		if( response.isFound() ){
			LOGGER.info( String.format( "id %s delete done. ", id ) );
		}else{
			LOGGER.error(  String.format( "id %s CAT'T FOUND,delete error. ", id ) );
		}
	}
	
	/**
	 * 
	 */
	public void search(){
		Client client = this.getIndexClient();
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		queryBuilder.must( QueryBuilders.termsQuery( "brand_name.raw", "百思图", "百丽" ) );
		//queryBuilder.must( QueryBuilders.queryString( "拖鞋" ) );
//		queryBuilder.should(  QueryBuilders.termQuery( "brands._id", "hVpM" ) );
//		builder.setQuery( queryBuilder );
//		SearchResponse response = builder.execute().actionGet();
		//QueryBuilders.filteredQuery(queryBuilder, )
		AndFilterBuilder fliterBuilder =  FilterBuilders.andFilter( FilterBuilders.termsFilter( "brand_name.raw", "百思图", "百丽" ));
		fliterBuilder.add( FilterBuilders.termFilter( "commodity_status", 2 ) );
		//fliterBuilder.add( FilterBuilders.termFilter( "", "" ) );
		fliterBuilder.add( FilterBuilders.existsFilter( "default_pic" ) );
		fliterBuilder.add( FilterBuilders.existsFilter( "pic_small" ) );
		fliterBuilder.add( FilterBuilders.rangeFilter( "sale_price" ).from( 10 ).to( 300 ) );
		fliterBuilder.add( FilterBuilders.queryFilter( QueryBuilders.queryString( "牛皮" ) ));
		
		//AggregationBuilders.count( "aaa" ).field( "seo_en_brand_name" ).
		
		SearchResponse response = client.prepareSearch( INDEX )
		.setTypes( TYPE_GOODS )
		.setQuery( QueryBuilders.filteredQuery( queryBuilder, fliterBuilder ) )
		//.setPostFilter( fliterBuilder.buildAsBytes() )
		.addAggregation( AggregationBuilders.terms( "mysqll1l" ).field( "seo_en_brand_name" ).size( Integer.MAX_VALUE ) .order( Terms.Order.count( false ) ))
		.setFrom( 0 ).setSize( 10 )
		.addSort( "score_a", SortOrder.DESC )
		.execute().actionGet();
		SearchHits hits = response.getHits();
		SearchHit[] searchHits = hits.getHits();
		System.out.println( hits.getTotalHits() + " ------------");
		for( SearchHit hit  : searchHits ){
			System.out.println( hit.getSourceAsString() );
		}
		Aggregations aggreations = response.getAggregations();
		Map<String, Aggregation> map = aggreations.asMap();
		for( Map.Entry<String, Aggregation> e : map.entrySet() ){
			InternalTerms t =  (InternalTerms) e.getValue();
			for( Bucket b : t.getBuckets()){
				 System.out.println( b.getKey() );
				 System.out.println( b.getDocCount());
			}
		}
		System.out.println( 1 );
		//.setQuery( "keyword","百丽" )
		
	}
	
	public void get(){
		Client client = getIndexClient();
		GetRequestBuilder builder = client.prepareGet( INDEX, TYPE, "95kPs5YBRPao3-Lei51g2w" );
		GetResponse response =  builder.execute().actionGet();
		Map<String, Object> source = response.getSource();
		String s =  (String) source.get( "my_time" );
		DateTime d =  ISODateTimeFormat.dateTime().parseDateTime( s ) ;
		System.out.println( d.toDate());
	}
	
	public void datetimeRange( ){
		Client client = this.getIndexClient();
		AndFilterBuilder fliterBuilder =  FilterBuilders.andFilter();
		fliterBuilder.add( FilterBuilders.rangeFilter( "my_time" ).to( new Date() ) );
		SearchResponse response = client.prepareSearch( INDEX )
		.setTypes( TYPE )
		.setQuery( QueryBuilders.filteredQuery( QueryBuilders.matchAllQuery(), fliterBuilder ) )
		.execute().actionGet();
		SearchHits hits = response.getHits();
		SearchHit[] searchHits = hits.getHits();
		System.out.println( hits.getTotalHits() + " ------------");
		for( SearchHit hit  : searchHits ){
			System.out.println( hit.getSourceAsString() );
		}
		
	}

	
	public void addIndex(){
		Client client = getIndexClient();
		IndexRequestBuilder builder = client.prepareIndex().setIndex( INDEX ).setType( TYPE );
		Map<String,Object> source =  Maps.newHashMap();
		source.put( "my_name",  "日本人是傻逼" );
		builder.setSource( source );
		builder.setId( "111" );
		builder.execute();
	}
	
	public void search11(){
		Client c = getIndexClient();
		QueryBuilder q =  null;//QueryBuilders.matchAllQuery();
		//q = QueryBuilders.prefixQuery("my_name",  "这是" );
		q = QueryBuilders.queryString( "傻逼" );
		SearchResponse response = c.prepareSearch( INDEX )
				.setQuery( q  ).execute().actionGet();
		SearchHits hits = response.getHits();
		SearchHit[] searchHits = hits.getHits();
		System.out.println( hits.getTotalHits() + " ------------");
		for( SearchHit hit  : searchHits ){
			System.out.println( hit.getSourceAsString() );
		}
	}
	
//	public void deleteIndex() {
//		Client client = getIndexClient();
//		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("ikan");
//		client.admin().indices().delete(deleteIndexRequest);
//		
//	}
	
	public static void main( String[] args ) throws ElasticsearchException, IOException{
//		EsClient t = new EsClient();
//		t.deleteIndex();
		String[] value = new String[2];
		System.out.println(value instanceof Object[]);
//		SearchRequestBuilder  searchBuilder = t.getIndexClient().prepareSearch( INDEX ).setTypes( TYPE_GOODS );
//		QueryBuilder queryBuilder = QueryBuilders.queryString( "泳池" ) ;
//		AndFilterBuilder filterBuilder =  FilterBuilders.andFilter( );
//		searchBuilder.setQuery( QueryBuilders.filteredQuery(queryBuilder, filterBuilder));
//		SearchResponse response = searchBuilder.execute().actionGet();
//		SearchHits hits = response.getHits();
//		Long totalHits = hits.getTotalHits();
//		System.out.println(totalHits);
		//t.setReplicas( 1 );
		//t.addIndex();
		//t.addIndexBulk();
		//t.createIndex();
		//t.deleteIndex();
		//Map<String,Object> source =  Maps.newHashMap();
		
		//source.put(  "active_id" , null ) ;
		//Map<String, Map<String,Object>> json = Maps.newHashMap();
		//json.put("100000004", source);
		//String s = "ctx._source.rebate=ctx._source.market_price/12";
		//t.updateIndex( "100000004", s, TYPE_GOODS);
		//t.addIndexBulk( t.dao.getBrands(), TYPE_BRANDS );
		//t.query();
		//String s =  String.format( "exeid : %s, beforeBulk. action size : %s ", 11, 11L ) ;
		//System.out.println( s );
		//System.out.println(  Runtime.getRuntime().availableProcessors()  );
		//t.addIndex();
//		t.search11();
		//t.datetimeRange();
	}
}
