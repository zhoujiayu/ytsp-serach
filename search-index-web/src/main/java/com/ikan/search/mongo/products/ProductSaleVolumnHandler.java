package com.ikan.search.mongo.products;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.ikan.core.entity.EbOrder;
import com.ikan.core.entity.EbOrderDetail;
import com.ikan.core.entity.mongo.ProductStatistic;
import com.ikan.core.service.EbCommentService;
import com.ikan.core.service.EbOrderService;
import com.ikan.search.es.index.EsClient;
import com.ikan.search.es.quartz.index.IndexQuartz;
import com.ikan.search.mongo.products.model.MongoProductOperation;


@Component("productSaleVolumnHandler")
public class ProductSaleVolumnHandler {
	@Resource(name = "mongoTemplate")
	private MongoTemplate mongoTemplate;
	
	@Resource(name = "ebOrderService")
	private EbOrderService ebOrderService;
	
	@Resource(name = "ebCommentService")
	private EbCommentService ebCommentService;
	
	@Resource(name = "esutils")
    private EsClient esUtils;

	public void start() {
		MongoProductOperation mongoProductOperation = mongoTemplate.findOne(new Query(), MongoProductOperation.class);
		Date startTime = null;
		if (mongoProductOperation != null) {
			//证明没有计算过order,那么从头开始计算.
			startTime = mongoProductOperation.getDate();
		} else {
			mongoProductOperation = new MongoProductOperation();
		}
		
		if (startTime == null) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, 1970);
			startTime = c.getTime();
		}
		Date endTime = new Date();
		mongoProductOperation.setDate(endTime);
		List<EbOrder> orders = ebOrderService.retrieveValidEbOrder(startTime, endTime);
		if (orders != null) {
			for (EbOrder order : orders) {
				for (EbOrderDetail detail : order.getOrderDetails()) {
					if (detail.getAmount() == null) {//如果是购买会员的话,是没有amount的.
						continue;
					}
					Query query = new Query(Criteria.where("productCode").is(detail.getProductCode().intValue()));
					Update update = new Update().inc("saleNum", detail.getAmount().intValue());
					ProductStatistic p = mongoTemplate.findAndModify(query, update ,ProductStatistic.class);
					if (p == null) {
						p = new ProductStatistic();
						p.setProductCode(detail.getProductCode().intValue());
						p.setSaleNum(detail.getAmount().intValue());
						p.setViewNum(0);
						mongoTemplate.save(p);
					} else {
						mongoTemplate.updateMulti(query, update, ProductStatistic.class);
					}
					
				}
			}
		}
		
		List<ProductStatistic> list = mongoTemplate.findAll(ProductStatistic.class);
		for(ProductStatistic p:list){
			Query query = new Query(Criteria.where("productCode").is(p.getProductCode()));
			Update update = new Update().set("quarterSaleNum", 0);
			update.set("score", ebCommentService.retrieveProductScore(p.getProductCode()));
			mongoTemplate.updateMulti(query, update, ProductStatistic.class);
		}
		
		List<EbOrder> quarterOrders = ebOrderService.retrieveQuarterEbOrder();
		for (EbOrder order : quarterOrders) {
			for (EbOrderDetail detail : order.getOrderDetails()) {
				if (detail.getAmount() == null) {
					continue;
				}
				Query query = new Query(Criteria.where("productCode").is(detail.getProductCode().intValue()));
				Update update = new Update().inc("quarterSaleNum", detail.getAmount().intValue());
				ProductStatistic p = mongoTemplate.findAndModify(query, update ,ProductStatistic.class);
				if (p != null) {
					mongoTemplate.updateMulti(query, update, ProductStatistic.class);
				}
			}
		}
		mongoTemplate.save(mongoProductOperation);
	}
	
}
