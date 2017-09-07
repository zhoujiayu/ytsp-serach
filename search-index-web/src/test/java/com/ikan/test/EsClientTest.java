package com.ikan.test;

import java.io.IOException;

import javax.annotation.Resource;

import org.elasticsearch.ElasticsearchException;
import org.junit.Test;

import com.ikan.search.es.index.EsClient;
import com.ikan.test.base.BaseTest;

public class EsClientTest extends BaseTest{

	@Resource(name = "esutils")
	private EsClient client;
	@Test
	public void test() {
		try {
			client.createIndex();
		} catch (ElasticsearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
