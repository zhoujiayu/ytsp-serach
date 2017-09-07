package com.ikan.search.es.index.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.springframework.stereotype.Component;

import com.ikan.core.entity.EbProduct;
import com.ikan.core.entity.app.AppAlbum;
import com.ikan.search.es.index.EsClient;
import com.ikan.search.es.index.model.IndexAlbumModel;
import com.ikan.search.es.index.model.IndexProductModel;
import com.ikan.search.es.index.service.impl.IndexAlbumService;
import com.ikan.search.es.index.service.impl.IndexProductService;

@Component("indexHandler")
public class IndexHandler {
	
  @Resource(name = "esutils")
  private EsClient esUtils;
  
  @Resource(name = "indexProductService")
  private IndexProductService indexProductService;
  
  @Resource(name = "indexAlbumService")
  private IndexAlbumService indexAlbumService;
  
  //不能被搜索到的商品
  private static Integer[] excludeProducts = {6011013,6011015,6011016,9900011};
  
  public void index() {
	  List<EbProduct> ebProducts = indexProductService.loadProducts();
	  
	  List<EbProduct> needToIndexProducts = new ArrayList<EbProduct>();
	  int i = 1;
	  BulkProcessor bp = esUtils.getBulkProcessor();
	  List<Integer> excludeProductList = Arrays.asList(excludeProducts);
	  for (EbProduct ebProduct : ebProducts) {
		  if(excludeProductList.contains(ebProduct.getProductCode()))
			  continue;
		  if (i++ % 100 == 0) {
			  List<IndexProductModel> indexProductModels = indexProductService.getIndexProductModels(needToIndexProducts);
			  Map<Integer,Map<String,Object>> jsonMaps= indexProductService.getJsonHashMaps(indexProductModels, needToIndexProducts);
			  for (Integer id : jsonMaps.keySet()) {
				  bp.add(esUtils.updateRequest(EsClient.TYPE_GOODS, id.toString(),jsonMaps.get(id)));
			  }
			  needToIndexProducts = new ArrayList<EbProduct>();
		  }
		  needToIndexProducts.add(ebProduct);
	  }
	  
	  if (needToIndexProducts.size() > 0) {
		  List<IndexProductModel> indexProductModels = indexProductService.getIndexProductModels(needToIndexProducts);
		  Map<Integer,Map<String,Object>> jsonMaps= indexProductService.getJsonHashMaps(indexProductModels, needToIndexProducts);
		  for (Integer id : jsonMaps.keySet()) {
			  bp.add(esUtils.updateRequest(EsClient.TYPE_GOODS,id.toString(),jsonMaps.get(id)));
		  }
	  }
	  
	  
	  
	  List<AppAlbum> albums = indexAlbumService.loadAlbums();
	  int k = 1;
	  List<AppAlbum> needToIndexAlbums = new ArrayList<AppAlbum>();
	  for(AppAlbum album: albums){
		  if (k++ % 100 == 0) {
			  List<IndexAlbumModel> indexAlbumModels = indexAlbumService.getIndexAlbumModels(needToIndexAlbums);
			  Map<Integer,Map<String,Object>> jsonMaps= indexAlbumService.getJsonHashMaps(indexAlbumModels, needToIndexAlbums);
			  for (Integer id : jsonMaps.keySet()) {
				  bp.add(esUtils.updateRequest(EsClient.TYPE_ALBUMS, id.toString(),jsonMaps.get(id)));
			  }
			  needToIndexAlbums = new ArrayList<AppAlbum>();
		  }
		  needToIndexAlbums.add(album);
	  }
	  
	  if (needToIndexAlbums.size() > 0) {
		  List<IndexAlbumModel> indexAlbumModels = indexAlbumService.getIndexAlbumModels(needToIndexAlbums);
		  Map<Integer,Map<String,Object>> jsonMaps= indexAlbumService.getJsonHashMaps(indexAlbumModels, needToIndexAlbums);
		  for (Integer id : jsonMaps.keySet()) {
			  bp.add(esUtils.updateRequest(EsClient.TYPE_ALBUMS, id.toString(),jsonMaps.get(id)));
		  }
	  }
	  bp.close();
  }
  
}
