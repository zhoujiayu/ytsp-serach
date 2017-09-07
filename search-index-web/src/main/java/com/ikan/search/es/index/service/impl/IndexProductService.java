package com.ikan.search.es.index.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.elasticsearch.common.collect.Maps;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.ikan.core.entity.EbBrand;
import com.ikan.core.entity.EbCatagoryBak;
import com.ikan.core.entity.EbProduct;
import com.ikan.core.entity.EbSku;
import com.ikan.core.entity.RelationAge;
import com.ikan.core.entity.Tag;
import com.ikan.core.entity.app.AppAlbum;
import com.ikan.core.entity.mongo.ProductStatistic;
import com.ikan.core.enums.EbRootCategoryEnum;
import com.ikan.core.service.EbCommentService;
import com.ikan.core.service.EbProductParamService;
import com.ikan.core.service.EbProductService;
import com.ikan.core.service.EbSkuService;
import com.ikan.core.service.RelationAgeService;
import com.ikan.core.service.TagService;
import com.ikan.core.service.app.AppAlbumService;
import com.ikan.search.es.index.model.IndexProductModel;
import com.ikan.search.es.web.utils.PinyinUtils;

@Component("indexProductService")
public class IndexProductService {
	
	@Resource(name = "ebProductService")
	private EbProductService ebProductService;
	
	@Resource(name = "ebProductParamService")
	private EbProductParamService ebProductParamService;
	
	@Resource(name = "ebCommentService")
	private EbCommentService ebCommentService;
	
	@Resource(name = "ebSkuService")
	private EbSkuService ebSkuService;
	
	@Resource(name = "appAlbumService")
	private AppAlbumService appAlbumService;
	
	@Resource(name = "relationAgeService")
	private RelationAgeService relationAgeService;
	
	@Resource(name = "tagService")
	private TagService tagService;
	
	private static String TAGNAME_PREFIX = "tag_name_";
	
	@Resource(name = "mongoTemplate")
	private MongoTemplate mongoTemplate;
	
	public static Cache<String,Map<Integer,ProductStatistic>> productStatisticsCache = CacheBuilder.newBuilder()  
            .expireAfterWrite(6, TimeUnit.HOURS).maximumSize(1)  
            .build();
	
	public Map<Integer,ProductStatistic> retrieveAllProductStatistics() {
		Map<Integer,ProductStatistic> ret = new HashMap<Integer,ProductStatistic>();
		List<ProductStatistic> productStatistics = mongoTemplate.findAll(ProductStatistic.class);
		if (productStatistics != null) {
			for (ProductStatistic p : productStatistics) {
				ret.put(p.getProductCode(), p);
			}
		}
		return ret;
	}
	
	  
	public List<EbProduct> loadProducts () {
		List<EbProduct> ebProducts = ebProductService.retrieveEbProducts();
		//List<EbProduct> ebProducts = ebProductService.retrieveChangeProducts();
		return ebProducts;
	}
	
	
	
	public List<IndexProductModel> getIndexProductModels(List<EbProduct> ebProducts) {
		List<IndexProductModel> indexProductModels = Lists.newArrayList();
		for (EbProduct ebProduct : ebProducts) {
			IndexProductModel indexModel = new IndexProductModel();
			try {
				indexModel = trans2IndexProductModel(ebProduct);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			indexProductModels.add(indexModel);
		}
		return indexProductModels;
	}
	
	
	public Map<Integer,Map<String,Object>> getJsonHashMaps(List<IndexProductModel> indexProductModels,
			List<EbProduct> ebProducts) {
		Map<Integer,Map<String,Object>> results = Maps.newHashMap();
		if (indexProductModels != null && indexProductModels.size() > 0) {
			Map<Integer,List<Tag>> tagMap = loadProductTags(ebProducts);
			for (IndexProductModel indexProductModel : indexProductModels) {
				Integer productCode = indexProductModel.getId();
				
				List<Tag> tags = tagMap.get(productCode);
				Map<String,List<Integer>> maps = new HashMap<String,List<Integer>>();
				for (Tag tag : tags) {
					if (!maps.containsKey(TAGNAME_PREFIX + tag.getGroupId())) {
						List<Integer> tagIds = new ArrayList<Integer>();
						tagIds.add(tag.getId());
						maps.put(TAGNAME_PREFIX + tag.getGroupId(), tagIds);
					}else{
						maps.get(TAGNAME_PREFIX + tag.getGroupId()).add(tag.getId());
					}
				}
				
				Map<String, Object> jsonMap = indexProductModel.toJsonMap();
				if (maps != null && maps.size() > 0) {
					for (String key : maps.keySet()) {
						jsonMap.put(key,maps.get(key));
					}
				}
				
				jsonMap.put("index-date", new Date());
				results.put(indexProductModel.getId(), jsonMap);
			}
		}
		return results;
	}
	
	public Map<Integer,List<Tag>> loadProductTags(List<EbProduct> ebProducts){
		Map<Integer,List<Tag>> map= new HashMap<Integer,List<Tag>>();
		for(EbProduct product: ebProducts){
			List<Tag> tags = tagService.retrieveValidProductTags(product);
			map.put(product.getProductCode(), tags);
		}
		return map;
	}
	
	private IndexProductModel trans2IndexProductModel(EbProduct ebProduct) throws ExecutionException {
		IndexProductModel indexProductModel = new IndexProductModel();
		EbBrand ebBrand = ebProduct.getEbBrand();
		if (ebBrand != null) {
			indexProductModel.setBrand(ebBrand.getBrandName());
			indexProductModel.setBrandLogo(ebBrand.getBrandLogo());
			indexProductModel.setBrandSortNum(ebBrand.getSortNum());
			indexProductModel.setBrandId(ebBrand.getBrandId());
			indexProductModel.setBrandRaw(ebBrand.getBrandShort());
			indexProductModel.setBrandAn(ebBrand.getBrandName());
		}
		
		EbCatagoryBak ebCategory = ebProduct.getEbCatagoryBak();
		if (ebCategory != null) {
			indexProductModel.setProductCategory(ebCategory.getCname());
			//indexProductModel.setProductCategoryPath(getCategoryPath(ebCategory));
			indexProductModel.setProductCategoryRaw(PinyinUtils.getInstance().getHanyupinyin(ebCategory.getCname()));
			indexProductModel.setProductCategoryId(ebCategory.getId());
			if(ebCategory.getParent()!=null){
				indexProductModel.setRootCategoryId(ebCategory.getParent());
				EbRootCategoryEnum parentEnum = EbRootCategoryEnum.valueOf(ebCategory.getParent());
				if(parentEnum!=null){
					String rootCategoryName = parentEnum.getText();
					indexProductModel.setRootCategory(rootCategoryName);
					indexProductModel.setRootCategoryRaw(PinyinUtils.getInstance().getHanyupinyin(rootCategoryName));
				}
			}
		}
		
		
		EbCatagoryBak ebCategoryBak = ebProduct.getEbCatagoryBak();
		if (ebCategoryBak != null) {
			indexProductModel.setProductCategoryBak(ebCategoryBak.getCname());
			//indexProductModel.setProductCategoryPath(getCategoryPath(ebCategory));
			indexProductModel.setProductCategoryBakRaw(PinyinUtils.getInstance().getHanyupinyin(ebCategoryBak.getCname()));
			indexProductModel.setProductCategoryBakId(ebCategoryBak.getId());
			if(ebCategoryBak.getParent()!=null){
				indexProductModel.setRootCategoryBakId(ebCategoryBak.getParent());
				EbRootCategoryEnum parentEnum = EbRootCategoryEnum.valueOf(ebCategoryBak.getParent());
				if(parentEnum!=null){
					String rootCategoryName = parentEnum.getText();
					indexProductModel.setRootCategoryBak(rootCategoryName);
					indexProductModel.setRootCategoryBakRaw(PinyinUtils.getInstance().getHanyupinyin(rootCategoryName));
				}
			}
		}
		
		
		indexProductModel.setId(ebProduct.getProductCode());
		indexProductModel.setProductCode(ebProduct.getProductCode());
		indexProductModel.setImageSrc(ebProduct.getImgUrl());
		indexProductModel.setProductMarketPrice(ebProduct.getPrice());
		indexProductModel.setProductName(ebProduct.getProductName());
		indexProductModel.setProductNamePinyin(PinyinUtils.getInstance().getHanyupinyin(ebProduct.getProductName()));
		indexProductModel.setProductSalePrice(ebProduct.getVprice());
		indexProductModel.setVipPrice(ebProduct.getSvprice());
		indexProductModel.setProductStatus(ebProduct.getStatus());
		indexProductModel.setRebate(indexProductModel.getProductSalePrice()/indexProductModel.getProductMarketPrice());
	   Map<Integer,ProductStatistic> productStatistics = productStatisticsCache.get("productStatistics",new Callable<Map<Integer,ProductStatistic>>(){
			@Override
			public Map<Integer, ProductStatistic> call() throws Exception {
				return retrieveAllProductStatistics();
			}
		});
     	if (productStatistics != null) {
			ProductStatistic p = productStatistics.get(ebProduct.getProductCode());
			if (p != null) {
				indexProductModel.setSaleNum(p.getSaleNum());
				indexProductModel.setViewNum(p.getViewNum());
				indexProductModel.setQuarterSaleNum(p.getQuarterSaleNum());
				indexProductModel.setScore(p.getScore());
			} else {
				indexProductModel.setSaleNum(0);
				indexProductModel.setViewNum(0);
				indexProductModel.setQuarterSaleNum(0);
				indexProductModel.setScore(0d);
			}
		} else {
			indexProductModel.setSaleNum(0);
			indexProductModel.setViewNum(0);
			indexProductModel.setQuarterSaleNum(0);
			indexProductModel.setScore(0d);
		}
		//indexProductModel.setPriceSpan(getPriceSpan(ebProduct.getVprice()));
     	indexProductModel.setStoreStatus(0);
     	for(EbSku sku:ebProduct.getSkus()){
     		if(sku.getStatus()==1 && sku.getStorage().getAvailable()>0){
     			indexProductModel.setStoreStatus(1);
     			break;
     		}
     	}
		indexProductModel.setVendor(ebProduct.getVendorProductCode());
		indexProductModel.setProductNamePinyinIndex(PinyinUtils.getInstance().getPinYinHeadChar(ebProduct.getProductName()));
		if (ebProduct.getOnShelfTime() != null) {
			indexProductModel.setOnShelfTime(ebProduct.getOnShelfTime());
		}
		
		AppAlbum album = appAlbumService.retrieveProductAlbum(ebProduct);
		if(album!=null){
			indexProductModel.setProductAlbum(album.getName());
			indexProductModel.setProductAlbumId(album.getId());
		}
		
		RelationAge relationAge = relationAgeService.retrieveProductAge(ebProduct.getProductCode());
		if(relationAge!=null){
			indexProductModel.setAgeValue(relationAge.getShowValue());
		}
		return indexProductModel;
	}
	
	
	/*public String getPriceSpan(double productSalePrice) {
		if (productSalePrice <= 100) {
			return "0"; //0-100
		} else if (productSalePrice <= 300){
			return "1"; //100-300
		} else if (productSalePrice <=500) {
			return "2"; //300-500
		} else if (productSalePrice <=800) {
			return "3"; //500-800
		} else {
			return "4"; //800+
		}
	}*/
	
	
	/*public String getCategoryPath(EbCatagory ebCategory) {
		StringBuilder sb = new StringBuilder();
		while(ebCategory != null) {
			sb.append(sb.toString() + "<");
			//ebCategory = ebCategory.getParent();
		}
		return sb.toString();
	}
	*/
	
}
