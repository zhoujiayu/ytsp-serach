package com.ikan.search.es.index.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.collect.Maps;

import com.ikan.search.es.index.annotations.IndexNameAnnotation;


public class IndexAlbumModel {
	// 视频id
	private Integer albumId;
	// 视频名称
	private String albumName;
	private String albumNamePinyin;
	private String albumNamePinyinIndex;
	//视频封面
	private String imageSrc;
	// 分类名称
	private String albumCategory = StringUtils.EMPTY;
	private Integer albumCategoryId;
	private String ageValue = StringUtils.EMPTY;
	/*//视频适用年龄 tag
	private String forAge;
	//性别 tag
	private String sex;
	//文化  tag
	private String culture;
	//主题  tag
	private String theme;
	//形象  tag
	private String figure;
	//语言  tag
	private String language;
	*/
	
	// 1上线
	private Integer iosUplow;
	// 1上线
	private Integer androidUplow;
	//审核时间   排序
	private Date reviewTime = null;
	//审核状态
	private Integer reviewStatue;
	//播放次数   排序
	private Integer playTimes;
	//总集数
	private Integer totalCount;
	//最新集数
	private Integer nowCount;
	//知识or动漫
	private Integer specialType;
	
	private Integer reviewHide;
	
	//vip视频，需要付费收看
	private Boolean vip;
	
	//OTT
	private Integer ott;
	//未来电视审核状态
	private Integer futureStatus;
	//是否早教
	private Boolean isEarlyEdu = false;
	
	@IndexNameAnnotation("album_isEarlyEdu")
	public Boolean getIsEarlyEdu() {
		return isEarlyEdu;
	}

	public void setIsEarlyEdu(Boolean isEarlyEdu) {
		this.isEarlyEdu = isEarlyEdu;
	}

	@IndexNameAnnotation("album_futureStatus")
	public Integer getFutureStatus() {
		return futureStatus;
	}


	public void setFutureStatus(Integer futureStatus) {
		this.futureStatus = futureStatus;
	}


	@IndexNameAnnotation("album_id")
	public Integer getAlbumId() {
		return albumId;
	}


	public void setAlbumId(Integer albumId) {
		this.albumId = albumId;
	}

	@IndexNameAnnotation("album_name")
	public String getAlbumName() {
		return albumName;
	}


	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}
	
	@IndexNameAnnotation("album_name_pinyin")
	public String getAlbumNamePinyin() {
		return albumNamePinyin;
	}


	public void setAlbumNamePinyin(String albumNamePinyin) {
		this.albumNamePinyin = albumNamePinyin;
	}

	@IndexNameAnnotation("album_name_pinyin_index")
	public String getAlbumNamePinyinIndex() {
		return albumNamePinyinIndex;
	}


	public void setAlbumNamePinyinIndex(String albumNamePinyinIndex) {
		this.albumNamePinyinIndex = albumNamePinyinIndex;
	}


	@IndexNameAnnotation("pic_default")
	public String getImageSrc() {
		return imageSrc;
	}
	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}
	
	@IndexNameAnnotation("album_category")
	public String getAlbumCategory() {
		return albumCategory;
	}


	public void setAlbumCategory(String albumCategory) {
		this.albumCategory = albumCategory;
	}
	
	@IndexNameAnnotation("album_category_id")
	public Integer getAlbumCategoryId() {
		return albumCategoryId;
	}
	public void setAlbumCategoryId(Integer albumCategoryId) {
		this.albumCategoryId = albumCategoryId;
	}
	
	

	/*@IndexNameAnnotation("age")
	public String getForAge() {
		return forAge;
	}


	public void setForAge(String forAge) {
		this.forAge = forAge;
	}

	@IndexNameAnnotation("sex")
	public String getSex() {
		return sex;
	}


	public void setSex(String sex) {
		this.sex = sex;
	}

	@IndexNameAnnotation("culture")
	public String getCulture() {
		return culture;
	}


	public void setCulture(String culture) {
		this.culture = culture;
	}

	@IndexNameAnnotation("theme")
	public String getTheme() {
		return theme;
	}


	public void setTheme(String theme) {
		this.theme = theme;
	}

	@IndexNameAnnotation("figure")
	public String getFigure() {
		return figure;
	}


	public void setFigure(String figure) {
		this.figure = figure;
	}

	@IndexNameAnnotation("language")
	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}
	*/
	@IndexNameAnnotation("ios_uplow")
	public Integer getIosUplow() {
		return iosUplow;
	}


	public void setIosUplow(Integer iosUplow) {
		this.iosUplow = iosUplow;
	}

	@IndexNameAnnotation("android_uplow")
	public Integer getAndroidUplow() {
		return androidUplow;
	}


	public void setAndroidUplow(Integer androidUplow) {
		this.androidUplow = androidUplow;
	}
	
	@IndexNameAnnotation("special_type")
	public Integer getSpecialType() {
		return specialType;
	}


	public void setSpecialType(Integer specialType) {
		this.specialType = specialType;
	}


	@IndexNameAnnotation("review_time")
	public Date getReviewTime() {
		return reviewTime;
	}


	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}

	@IndexNameAnnotation("play_times")
	public Integer getPlayTimes() {
		return playTimes;
	}


	public void setPlayTimes(Integer playTimes) {
		this.playTimes = playTimes;
	}
	
	
	@IndexNameAnnotation("total_count")
	public Integer getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	
	@IndexNameAnnotation("now_count")
	public Integer getNowCount() {
		return nowCount;
	}
	public void setNowCount(Integer nowCount) {
		this.nowCount = nowCount;
	}
	
	@IndexNameAnnotation("review_status")
	public Integer getReviewStatue() {
		return reviewStatue;
	}
	
	@IndexNameAnnotation("age_value")
	public String getAgeValue() {
		return ageValue;
	}
	public void setAgeValue(String ageValue) {
		this.ageValue = ageValue;
	}


	public void setReviewStatue(Integer reviewStatue) {
		this.reviewStatue = reviewStatue;
	}
    
	@IndexNameAnnotation("vip")
	public Boolean getVip() {
		return vip;
	}


	public void setVip(Boolean vip) {
		this.vip = vip;
	}

	
	@IndexNameAnnotation("review_hide")
	public Integer getReviewHide() {
		return reviewHide;
	}


	public void setReviewHide(Integer reviewHide) {
		this.reviewHide = reviewHide;
	}
	
	@IndexNameAnnotation("album_ott")
	public Integer getOtt() {
		return ott;
	}

	public void setOtt(Integer ott) {
		this.ott = ott;
	}



	public Map<String, Object> toJsonMap() {
		Map<String, Object> jsonMap = Maps.newHashMap();
		Method[] methods = this.getClass().getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			// 查看是否指定注释：
			if (method.isAnnotationPresent(IndexNameAnnotation.class)) {
				IndexNameAnnotation indexNameAnnotation = method
						.getAnnotation(IndexNameAnnotation.class);
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
