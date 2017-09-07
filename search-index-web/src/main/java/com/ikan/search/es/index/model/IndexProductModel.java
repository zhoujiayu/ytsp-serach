package com.ikan.search.es.index.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.collect.Maps;

import com.ikan.search.es.index.annotations.IndexNameAnnotation;

public class IndexProductModel {
	
	private Integer id;
	private Integer productCode;
	private Integer skuCode;
	private String productName;
	private String productNamePinyin;
	private Integer productStatus;
	private String productColor;
	private Double productMarketPrice;
	private Double productSalePrice;
	private Double vipPrice;
	private Double rebate;
	private Integer saleNum;
	private Integer quarterSaleNum;
	private Integer storeNum;
	private Integer storeStatus;
	private String imageSrc;
	private Double score;
	private String size;
	private String sizeValue;
	private String vendor;
	private String productNamePinyinIndex;
/*	private String priceSpan;*/
	private Integer viewNum;
	private String brand = StringUtils.EMPTY;
	private Date onShelfTime = new Date();
	private String productCategory = StringUtils.EMPTY;
	private String productCategoryPath = StringUtils.EMPTY;
	private String rootCategory = StringUtils.EMPTY;
	
	private String productCategoryBak = StringUtils.EMPTY;
	private String rootCategoryBak = StringUtils.EMPTY;
	private String rootCategoryBakRaw = StringUtils.EMPTY;
	private String productCategoryBakRaw = StringUtils.EMPTY;
	private Integer productCategoryBakId = 0;
	private Integer rootCategoryBakId = 0;
	
	
	private String ageValue = StringUtils.EMPTY;
	
	//private String forAge;
	//动漫周边
	private String productAlbum = StringUtils.EMPTY;
	private Integer productAlbumId = 0;
	
	private String brandLogo = StringUtils.EMPTY;
	private Integer brandSortNum = 0;
	private Integer brandId = 0;
	private Integer productCategoryId = 0;
	private Integer rootCategoryId = 0;
	
	private String brandRaw = StringUtils.EMPTY;
	private String rootCategoryRaw = StringUtils.EMPTY;
	private String productCategoryRaw = StringUtils.EMPTY;
	private String brandAn = StringUtils.EMPTY;
	
	
	@IndexNameAnnotation("quarter_sale_num")
	public Integer getQuarterSaleNum() {
		return quarterSaleNum;
	}
	public void setQuarterSaleNum(Integer quarterSaleNum) {
		this.quarterSaleNum = quarterSaleNum;
	}
	@IndexNameAnnotation("brand_an")
	public String getBrandAn() {
		return brandAn;
	}
	public void setBrandAn(String brandAn) {
		this.brandAn = brandAn;
	}
	@IndexNameAnnotation("product_category_raw")
	public String getProductCategoryRaw() {
		return productCategoryRaw;
	}
	public void setProductCategoryRaw(String productCategoryRaw) {
		this.productCategoryRaw = productCategoryRaw;
	}
	@IndexNameAnnotation("root_category_raw")
	public String getRootCategoryRaw() {
		return rootCategoryRaw;
	}
	public void setRootCategoryRaw(String rootCategoryRaw) {
		this.rootCategoryRaw = rootCategoryRaw;
	}
	@IndexNameAnnotation("brand_raw")
	public String getBrandRaw() {
		return brandRaw;
	}
	public void setBrandRaw(String brandRaw) {
		this.brandRaw = brandRaw;
	}
	@IndexNameAnnotation("product_category_id")
	public Integer getProductCategoryId() {
		return productCategoryId;
	}
	public void setProductCategoryId(Integer productCategoryId) {
		this.productCategoryId = productCategoryId;
	}
	@IndexNameAnnotation("root_category_id")
	public Integer getRootCategoryId() {
		return rootCategoryId;
	}
	public void setRootCategoryId(Integer rootCategoryId) {
		this.rootCategoryId=rootCategoryId;
	}
	@IndexNameAnnotation("product_category_path")
	public String getProductCategoryPath() {
		return productCategoryPath;
	}
	public void setProductCategoryPath(String productCategoryPath) {
		this.productCategoryPath = productCategoryPath;
	}

	
	@IndexNameAnnotation("brand_id")
	public Integer getBrandId() {
		return brandId;
	}
	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}
	@IndexNameAnnotation("brand_logo")
	public String getBrandLogo() {
		return brandLogo;
	}
	public void setBrandLogo(String brandLogo) {
		this.brandLogo = brandLogo;
	}
	@IndexNameAnnotation("brand_sort_num")
	public Integer getBrandSortNum() {
		return brandSortNum;
	}
	public void setBrandSortNum(Integer brandSortNum) {
		this.brandSortNum = brandSortNum;
	}
	@IndexNameAnnotation("product_category")
	public String getProductCategory() {
		return productCategory;
	}
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}
	@IndexNameAnnotation("root_category")
	public String getRootCategory() {
		return rootCategory;
	}
	public void setRootCategory(String rootCategory) {
		this.rootCategory = rootCategory;
	}
	
	//bak
	
	@IndexNameAnnotation("product_category_bak")
	public String getProductCategoryBak() {
		return productCategoryBak;
	}
	public void setProductCategoryBak(String productCategoryBak) {
		this.productCategoryBak = productCategoryBak;
	}
	@IndexNameAnnotation("root_category_bak")
	public String getRootCategoryBak() {
		return rootCategoryBak;
	}
	public void setRootCategoryBak(String rootCategoryBak) {
		this.rootCategoryBak = rootCategoryBak;
	}
	@IndexNameAnnotation("product_category_bak_raw")
	public String getProductCategoryBakRaw() {
		return productCategoryBakRaw;
	}
	public void setProductCategoryBakRaw(String productCategoryBakRaw) {
		this.productCategoryBakRaw = productCategoryBakRaw;
	}
	@IndexNameAnnotation("root_category_bak_raw")
	public String getRootCategoryBakRaw() {
		return rootCategoryBakRaw;
	}
	public void setRootCategoryBakRaw(String rootCategoryBakRaw) {
		this.rootCategoryBakRaw = rootCategoryBakRaw;
	}
	@IndexNameAnnotation("product_category_bak_id")
	public Integer getProductCategoryBakId() {
		return productCategoryBakId;
	}
	public void setProductCategoryBakId(Integer productCategoryBakId) {
		this.productCategoryBakId = productCategoryBakId;
	}
	@IndexNameAnnotation("root_category_bak_id")
	public Integer getRootCategoryBakId() {
		return rootCategoryBakId;
	}
	public void setRootCategoryBakId(Integer rootCategoryBakId) {
		this.rootCategoryBakId=rootCategoryBakId;
	}
	
	
	
	
	
	
	@IndexNameAnnotation("brand")
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	@IndexNameAnnotation("product_onshelf_time")
	public Date getOnShelfTime() {
		return onShelfTime;
	}
	public void setOnShelfTime(Date onShelfTime) {
		this.onShelfTime = onShelfTime;
	}
/*	@IndexNameAnnotation("price_span")
	public String getPriceSpan() {
		return priceSpan;
	}
	public void setPriceSpan(String priceSpan) {
		this.priceSpan = priceSpan;
	}*/
	@IndexNameAnnotation("view_num")
	public Integer getViewNum() {
		return viewNum;
	}
	public void setViewNum(Integer viewNum) {
		this.viewNum = viewNum;
	}
	@IndexNameAnnotation("sku_code")
	public Integer getSkuCode() {
		return skuCode;
	}
	public void setSkuCode(Integer skuCode) {
		this.skuCode = skuCode;
	}
	
	@IndexNameAnnotation("product_name_pinyin_index")
	public String getProductNamePinyinIndex() {
		return productNamePinyinIndex;
	}
	public void setProductNamePinyinIndex(String productNamePinyinIndex) {
		this.productNamePinyinIndex = productNamePinyinIndex;
	}
	@IndexNameAnnotation("id")
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	@IndexNameAnnotation("product_code")
	public Integer getProductCode() {
		return productCode;
	}
	public void setProductCode(Integer productCode) {
		this.productCode = productCode;
	}
	@IndexNameAnnotation("product_name")
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	@IndexNameAnnotation("product_name_pinyin")
	public String getProductNamePinyin() {
		return productNamePinyin;
	}
	public void setProductNamePinyin(String productNamePinyin) {
		this.productNamePinyin = productNamePinyin;
	}
	@IndexNameAnnotation("product_status")
	public Integer getProductStatus() {
		return productStatus;
	}
	public void setProductStatus(Integer productStatus) {
		this.productStatus = productStatus;
	}
	@IndexNameAnnotation("product_color")
	public String getProductColor() {
		return productColor;
	}
	public void setProductColor(String productColor) {
		this.productColor = productColor;
	}
	@IndexNameAnnotation("product_market_price")
	public Double getProductMarketPrice() {
		return productMarketPrice;
	}
	public void setProductMarketPrice(Double productMarketPrice) {
		this.productMarketPrice = productMarketPrice;
	}
	@IndexNameAnnotation("product_sale_price")
	public Double getProductSalePrice() {
		return productSalePrice;
	}
	public void setProductSalePrice(Double productSalePrice) {
		this.productSalePrice = productSalePrice;
	}
	@IndexNameAnnotation("vip_price")
	public Double getVipPrice() {
		return vipPrice;
	}
	public void setVipPrice(Double vipPrice) {
		this.vipPrice = vipPrice;
	}
	@IndexNameAnnotation("rebate")
	public Double getRebate() {
		return rebate;
	}
	public void setRebate(Double rebate) {
		this.rebate = rebate;
	}
	@IndexNameAnnotation("sale_num")
	public Integer getSaleNum() {
		return saleNum;
	}
	public void setSaleNum(Integer saleNum) {
		this.saleNum = saleNum;
	}
	@IndexNameAnnotation("store_num")
	public Integer getStoreNum() {
		return storeNum;
	}
	public void setStoreNum(Integer storeNum) {
		this.storeNum = storeNum;
	}
	@IndexNameAnnotation("store_status")
	public Integer getStoreStatus() {
		return storeStatus;
	}
	public void setStoreStatus(Integer storeStatus) {
		this.storeStatus = storeStatus;
	}
	@IndexNameAnnotation("pic_default")
	public String getImageSrc() {
		return imageSrc;
	}
	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}
	@IndexNameAnnotation("score")
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	@IndexNameAnnotation("size")
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	@IndexNameAnnotation("size_value")
	public String getSizeValue() {
		return sizeValue;
	}
	public void setSizeValue(String sizeValue) {
		this.sizeValue = sizeValue;
	}
	@IndexNameAnnotation("vendor")
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	@IndexNameAnnotation("product_album_name")
	public String getProductAlbum() {
		return productAlbum;
	}
	public void setProductAlbum(String productAlbum) {
		this.productAlbum = productAlbum;
	}
	@IndexNameAnnotation("product_album_id")
	public Integer getProductAlbumId() {
		return productAlbumId;
	}
	public void setProductAlbumId(Integer productAlbumId) {
		this.productAlbumId = productAlbumId;
	}
	@IndexNameAnnotation("age_value")
	public String getAgeValue() {
		return ageValue;
	}
	public void setAgeValue(String ageValue) {
		this.ageValue = ageValue;
	}
	/*	@IndexNameAnnotation("for_age")
	public String getForAge() {
		return forAge;
	}
	public void setForAge(String forAge) {
		this.forAge = forAge;
	}*/
	public Map<String,Object> toJsonMap() {
		Map<String,Object> jsonMap = Maps.newHashMap();
		Method[] methods = this.getClass().getDeclaredMethods();
		for (int i=0;i<methods.length;i++) {
			Method method = methods[i];
            //查看是否指定注释：
            if (method.isAnnotationPresent(IndexNameAnnotation.class)) {
            	IndexNameAnnotation indexNameAnnotation = method.getAnnotation(IndexNameAnnotation.class);
            	try {
            		Object result = method.invoke(this, null);
            		String name = indexNameAnnotation.value();
            		jsonMap.put(name, result == null ? "" : result);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					
				} catch (InvocationTargetException e) {
					
				}
            }
		}
		return jsonMap;
	}
}
