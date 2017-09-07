package com.ikan.test;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.ikan.core.entity.EbOrder;
import com.ikan.core.service.EbOrderService;
import com.ikan.search.mongo.products.ProductSaleVolumnHandler;
import com.ikan.test.base.BaseTest;

public class OrderTest extends BaseTest{
	@Resource(name = "ebOrderService")
	private EbOrderService ebOrderService;
	
	@Resource(name = "productSaleVolumnHandler")
	private ProductSaleVolumnHandler productSaleVolumnHandler;
	
	
	public void test() {
		productSaleVolumnHandler.start();
	}
	
	
	public static void main(String[] orgs) {
		String[] value = new String[2];
		System.out.println(value instanceof Object[]);
	}
}
