package com.asiainfo.dcompute.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.asiainfo.dcompute.model.Task;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月10日  下午6:01:25
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public interface IDManagerService {
    
    /**
     * 心跳
     * 
     * @param serverId
     */
    void heartbeat(String serverId);
    
    /**
     * 查询redis中注册的指定心跳的服务器
     * 
     * @param time
     * @param unit
     * @return
     */
    List<String> getSurviveServer(long time, TimeUnit unit);
    
    /**
     * 返回指定心跳的leader服务器
     * 
     * @param time
     * @param unit
     * @return
     */
    String getLeaderServer(long time, TimeUnit unit);
    
    /**
     * 当前服务器是否存活服务器中的leader
     * 
     * @param serverId
     * @param time
     * @param unit
     * @return
     */
    boolean isLeaderServer(String serverId, long time, TimeUnit unit);
    
    /**
     * 查询当前空闲的服务器
     * 
     * @param time
     * @param unit
     * @return
     */
    List<String> getFreeServer(long time, TimeUnit unit);
    
    /**
     * 任务调度
     * 
     * @param scheduleMap
     * @param time
     * @param unit
     */
    void dispatcher(Map<String, List<Task>> scheduleMap, long time, TimeUnit unit);
    
    /**
     * 获取服务器任务列表
     * 
     * @param serverId
     * @return
     */
    List<Task> getTaskList(String serverId);
    
    /**
     * 删除服务器任务列表
     * 
     * @param serverId
     */
    void removeServerTask(String serverId);
    
    /**
     * 获取锁
     * 
     * @param key
     * @param time
     * @param unit
     * @return
     */
    String acquireLock(String key, long time, TimeUnit unit);
    
    /**
     * 设置锁失效时间
     * 
     * @param key
     * @param lockId
     * @param time
     * @param unit
     * @return
     */
    boolean expireLock(String key, String lockId, long time, TimeUnit unit);
    
    /**
     * 释放锁
     * 
     * @param key
     * @param lockId
     * @return
     */
    boolean releaseLock(String key, String lockId);
    
    /**
     * 返回当前锁
     * 
     * @param key
     * @return
     */
    String getLock(String key);
    
    /**
     * 是否存在key
     * 
     * @param key
     * @return
     */
    boolean existLock(String key);
}
