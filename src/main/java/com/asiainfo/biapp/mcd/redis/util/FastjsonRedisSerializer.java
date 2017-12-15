package com.asiainfo.biapp.mcd.redis.util;

import java.nio.charset.Charset;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * redis对象序列化
 * 
 * @author       zq
 * @date         2017年11月7日  上午10:56:50
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public class FastjsonRedisSerializer implements RedisSerializer<Object> {

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static SerializerFeature[] features = {SerializerFeature.WriteClassName};
    
    /*
     * 反序列化
     * 
     * @param bytes
     * @return
     * @throws SerializationException
     * @see org.springframework.data.redis.serializer.RedisSerializer#deserialize(byte[])
     */
    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
    	
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        
        try {
            return JSON.parse(bytes);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: "
                    + ex.getMessage(), ex);
        }
    }

    /*
     * 序列化
     * 
     * @param t
     * @return
     * @throws SerializationException
     * @see org.springframework.data.redis.serializer.RedisSerializer#serialize(java.lang.Object)
     */
    @Override
    public byte[] serialize(Object t) throws SerializationException {
    	
        if (t == null) {
            return new byte[0];
        }
        
        try {
            return JSON.toJSONBytes(t, features);
        } catch (Exception ex) {
            throw new SerializationException("Could not write JSON: "
                    + ex.getMessage(), ex);
        }
    }
}