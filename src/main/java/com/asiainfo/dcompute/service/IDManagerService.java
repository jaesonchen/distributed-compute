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
     */
    void schedule(Map<String, List<Task>> scheduleMap);
    
    /**
     * 获取锁
     * 
     * @return
     */
    String acquireManagerLock();
    
    /**
     * 释放锁
     * 
     * @param lockId
     * @return
     */
    boolean releaseManagerLock(String lockId); 
}
