package com.ikan.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.ikan.core.entity.EbProduct;
import com.ikan.core.entity.Tag;
import com.ikan.core.entity.app.AppAlbum;
import com.ikan.core.service.EbProductService;
import com.ikan.core.service.TagService;
import com.ikan.core.service.app.AppAlbumService;
import com.ikan.search.es.index.handler.IndexHandler;
import com.ikan.test.base.BaseTest;

public class IndexProductServiceTest extends BaseTest{

	@Resource(name = "indexHandler")
	private IndexHandler indexHandler;
	
	
	@Resource(name = "ebProductService")
	private EbProductService ebProductService;
	@Resource(name = "appAlbumService")
	private AppAlbumService appAlbumService;
	
	@Test
	public void test() {
		List<EbProduct> products = ebProductService.retrieveEbProducts();
		System.out.println(products.size());
		
		List<AppAlbum> albums = appAlbumService.getAll();
		System.out.println(albums.size());
	}
}
