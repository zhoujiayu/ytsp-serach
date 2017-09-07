package com.ikan.search.mongo.products.quartz;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ikan.search.mongo.products.ProductSaleVolumnHandler;

@Component("productSaleVolumnQuartz")
public class ProductSaleVolumnQuartz {
	@Resource(name = "productSaleVolumnHandler")
	private ProductSaleVolumnHandler productSaleVolumnHandler;
    private static final Logger logger = LoggerFactory.getLogger(ProductSaleVolumnQuartz.class);  

	@Scheduled(cron="0 0 23 * * ?")
	public void startIndex() {
		logger.info("开始计算商品销量");
		try {
			productSaleVolumnHandler.start();
		} catch( Exception e) {
			logger.error(e.toString());
		}
		logger.info("计算商品销量完毕");
	}
}
