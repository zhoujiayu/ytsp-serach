package com.ikan.search.es.quartz.index;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ikan.core.service.EbStorageService;
import com.ikan.search.es.index.handler.IndexHandler;

@Component("indexQuartz")
@Lazy(false)
public class IndexQuartz {
	
    private static final Logger logger = LoggerFactory.getLogger(IndexQuartz.class);  

    @Resource(name = "indexHandler")
	private IndexHandler indexHandler;
    
    @Resource(name = "logHibernateTemplate", type = HibernateTemplate.class)
	private HibernateTemplate hibernateTemplate;
	
   @Resource(name = "ebStorageService")
   private EbStorageService ebStorageService;
   
   public static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    
    @Scheduled(cron="0 0/5 * * * ?")
	public void startIndex() {
    	Date now = new Date();
    	System.out.println("start index on #### "+sdf.format(now));
		try {
			indexHandler.index();
		} catch (Exception e) {
			System.out.println(" Exception end index on #### "+sdf.format(now));
			e.printStackTrace();
		}
		now = new Date();
    	System.out.println("end index on #### "+sdf.format(now));
	}
}
