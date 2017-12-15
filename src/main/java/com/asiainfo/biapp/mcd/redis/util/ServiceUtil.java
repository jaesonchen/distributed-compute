package com.asiainfo.biapp.mcd.redis.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

/**
 * @Description: TODO
 * 
 * @author       zq
 * @date         2017年2月26日  上午12:03:10
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public class ServiceUtil {

	private static final Logger logger = LoggerFactory.getLogger(ServiceUtil.class);
	
	/**
	 * 解析json字符串到Map中
	 * 
	 * @param jsonStr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> parseJsonString(final String jsonStr) {
		
		Map<String, Object> map = new HashMap<String, Object>(16);
		
		if (jsonStr == null || "".equals(jsonStr)) {
			return map;
		}
		
		try {
			map = (Map<String, Object>) JSON.parse(jsonStr);
		} catch (Exception ex) {
			logger.error("解析jsonStr：{}时出现异常，异常信息如下：\n{}", jsonStr, ex);
		}
		return map;
	}
	
	/**
	 * 转换map为json字符串
	 * 
	 * @param map
	 * @return
	 */
	public static String toJsonString(final Map<String, Object> map) {
		return (map == null || map.isEmpty()) ? "" : JSON.toJSONString(map);
	}
	
	/**
	 * 获取Map属性值，忽略属性名称的大小写
	 * 
	 * @param infoMap
	 * @param propertyName
	 * @param requiredType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getPropertyByKeyInsensitive(final Map<String, Object> infoMap, final String propertyName, final Class<E> requiredType) {
		
		if (infoMap == null || infoMap.isEmpty() || StringUtils.isEmpty(propertyName)) {
			return null;
		}
		for (Map.Entry<String, Object> entry : infoMap.entrySet()) {
			if (!StringUtils.isEmpty(entry.getKey()) && entry.getKey().equalsIgnoreCase(propertyName)) {
				return (E) entry.getValue();
			}
		}
		return null;
	}
	
	/**
	 * 睡眠等待timeout毫秒（ms）
	 * 
	 * @param timeout
	 */
	public static void waitFor(long timeout) {
		
		if (timeout <= 0) {
			return;
		}
		
		try {
			TimeUnit.MILLISECONDS.sleep(timeout);
		} catch (Exception e) {}
	}
	
	/**
	 * 睡眠等待timeout
	 * 
	 * @param timeout
	 * @param unit
	 */
	public static void waitFor(long timeout, TimeUnit unit) {
		
		if (timeout <= 0 || null == unit) {
			return;
		}
		
		try {
			TimeUnit.MILLISECONDS.sleep(unit.toMillis(timeout));
		} catch (Exception e) {}
	}
}
