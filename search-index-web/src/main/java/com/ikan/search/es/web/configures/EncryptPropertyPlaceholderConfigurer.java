package com.ikan.search.es.web.configures;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.ikan.search.es.web.utils.DESUtils;

public class EncryptPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer{
	
	private String[] encryptPropNames = {};
	
	@Override
	protected String convertProperty(String propertyName, String propertyValue) {
		if(isEncryptProp(propertyName)){
			return DESUtils.getDecryptString(propertyValue);
		}else{
			return propertyValue;
		}
	}
	
	private boolean isEncryptProp(String propertyName){
		for(String name : encryptPropNames){
			if(name.equals(propertyName)){
				return true;
			}
		}
		return false;
	}
	
	
}
