package com.ikan.search.es.index.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.common.collect.Maps;
import org.springframework.stereotype.Component;

import com.ikan.core.entity.OttPlatform;
import com.ikan.core.entity.PlatAlbumRelation;
import com.ikan.core.entity.RelationAge;
import com.ikan.core.entity.Tag;
import com.ikan.core.entity.app.AppAlbum;
import com.ikan.core.entity.app.AppCategory;
import com.ikan.core.service.OttPlatformService;
import com.ikan.core.service.PlatAlbumRelationService;
import com.ikan.core.service.RelationAgeService;
import com.ikan.core.service.TagService;
import com.ikan.core.service.app.AppAlbumService;
import com.ikan.search.es.index.model.IndexAlbumModel;
import com.ikan.search.es.web.utils.PinyinUtils;

@Component("indexAlbumService")
public class IndexAlbumService {
	
	@Resource(name = "appAlbumService")
	AppAlbumService appAlbumService;
	
	@Resource(name = "tagService")
	private TagService tagService;
	
	@Resource(name = "relationAgeService")
	private RelationAgeService relationAgeService;
	
	@Resource(name = "ottPlatformService")
	private OttPlatformService ottPlatformService;
	
	@Resource(name = "platAlbumRelationService")
	private PlatAlbumRelationService platAlbumRelationService;
	
	private static String TAGNAME_PREFIX = "tag_name_";
	
	private static String PLATFORM_PREFIX = "platform";
	
	public List<AppAlbum> loadAlbums(){
		List<AppAlbum> albums  = appAlbumService.getAll();
		//List<AppAlbum> albums  = appAlbumService.retrieveChangeAlbums();
		return albums;
	}
	
	public List<IndexAlbumModel> getIndexAlbumModels(List<AppAlbum> albums){
		List<IndexAlbumModel> indexAlbumModels = new ArrayList<IndexAlbumModel>();
		for(AppAlbum album: albums){
			IndexAlbumModel indexAlbumModel = new IndexAlbumModel();
			AppCategory appCategory = album.getAppCategory();	
			if(appCategory!=null){
				indexAlbumModel.setAlbumCategory(appCategory.getCname());
				indexAlbumModel.setAlbumCategoryId(appCategory.getId());
			}
			indexAlbumModel.setAlbumId(album.getId());
			indexAlbumModel.setAlbumName(album.getName());
			indexAlbumModel.setAlbumNamePinyin(PinyinUtils.getInstance().getHanyupinyinByDictionary(album.getName()));
		    indexAlbumModel.setAlbumNamePinyinIndex(PinyinUtils.getInstance().getPinYinHeadCharByDictionary(album.getName()));
			indexAlbumModel.setNowCount(album.getNowCount());
			indexAlbumModel.setTotalCount(album.getTotalCount());
			indexAlbumModel.setPlayTimes(album.getPlayCount());
			indexAlbumModel.setReviewTime(album.getReviewTime() == null ? new Date() : album.getReviewTime());
			indexAlbumModel.setImageSrc(album.getCover());
			indexAlbumModel.setSpecialType(album.getSpecialType());
			indexAlbumModel.setIosUplow(album.getIosUplow());
			indexAlbumModel.setAndroidUplow(album.getAndroidUplow());
			indexAlbumModel.setReviewStatue(album.getReview());
			indexAlbumModel.setVip(album.getVip());
			indexAlbumModel.setReviewHide(album.getReviewHide());
			//设置未来电视审核状态
			indexAlbumModel.setFutureStatus(album.getFutureStatus() == null ? 0:album.getFutureStatus());
			//设置是否早教标识
			indexAlbumModel.setIsEarlyEdu(album.getIsEarlyEdu() == null ? false
					: album.getIsEarlyEdu());
			RelationAge relationAge = relationAgeService.retrieveAlbumAge(album.getId());
			if(relationAge!=null){
				indexAlbumModel.setAgeValue(relationAge.getShowValue());
			}
			
			indexAlbumModel.setOtt(album.getOtt());
			
			
			indexAlbumModels.add(indexAlbumModel);
		}
		return indexAlbumModels;
	}
	
	public Map<Integer,Map<String,Object>> getJsonHashMaps(List<IndexAlbumModel> indexAlbumModels, List<AppAlbum> albums){
		Map<Integer,Map<String,Object>> results = Maps.newHashMap();
		Map<Integer,List<Tag>> tagMap = loadAlbumsTags(albums);
		Map<Integer,OttPlatform> platforms = loadOttPlatforms();
		for(IndexAlbumModel indexAlbumModel: indexAlbumModels){
			Integer albumId = indexAlbumModel.getAlbumId();
			List<Tag> tags = tagMap.get(albumId);
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
			Map<String, Object> jsonMap = indexAlbumModel.toJsonMap();
			if (maps != null && maps.size() > 0) {
				for (String key : maps.keySet()) {
					jsonMap.put(key,maps.get(key));
				}
			}
			List<Integer> platAlbumList = loadPlatAlbumRelation(albumId, platforms);
			if(platAlbumList != null && !platAlbumList.isEmpty()){
				jsonMap.put(PLATFORM_PREFIX,platAlbumList);
			}
			jsonMap.put("index-date", new Date());
			results.put(indexAlbumModel.getAlbumId(), jsonMap);
		}
		return results;
	}
	
	/**
	* 功能描述:获取专辑平台关系 
	* 参数：@param albumid
	* 参数：@return
	* 返回类型:List<PlatAlbumRelation>
	 */
	public List<Integer> loadPlatAlbumRelation(Integer albumId,Map<Integer,OttPlatform> platforms){
		List<PlatAlbumRelation> platAlbumList = platAlbumRelationService.getPlatAlbumByAlbumId(albumId);
		if(platAlbumList == null || platAlbumList.isEmpty()){
			return null;
		}
		List<Integer> ret = new ArrayList<Integer>();
		for (PlatAlbumRelation platAlbumRelation : platAlbumList) {
			Integer platId = platAlbumRelation.getPlatId();
			//判断专辑关系所对应的平台是否有效，过滤掉无效的平台专辑关系
			if(platforms.containsKey(platId)){
				ret.add(platId);
			}
		}
		
		return ret;
	}
	
	/**
	* 功能描述:获取所有有效的平台
	* 参数：@return
	* 返回类型:Map<Integer,OttPlatform>
	 */
	public Map<Integer,OttPlatform> loadOttPlatforms(){
		List<OttPlatform> platList = ottPlatformService.getAllPlatform();
		Map<Integer,OttPlatform> ret = new HashMap<Integer,OttPlatform>();
		if(platList == null || platList.isEmpty()){
			return ret;
		}
		for (OttPlatform ottPlatform : platList) {
			ret.put(ottPlatform.getId(), ottPlatform);
		}
		return ret;
	}
	
	public Map<Integer,List<Tag>> loadAlbumsTags(List<AppAlbum> albums){
		Map<Integer,List<Tag>> map= new HashMap<Integer,List<Tag>>();
		for(AppAlbum album: albums){
			List<Tag> tags = tagService.retrieveValidAlbumTags(album);
			map.put(album.getId(), tags);
		}
		return map;
	}
}
