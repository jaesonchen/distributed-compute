package com.asiainfo.dcompute.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.asiainfo.dcompute.model.Task;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月11日  下午4:08:44
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public interface IDTaskService {
    
    /**
     * 返回可执行任务，不包含正在执行的任务，判断依据是task的锁是否释放
     * 
     * @return
     */
    List<Task> getExecuteTask();
    
    /**
     * 服务器分配到的可执行任务
     * 
     * @param serverId
     * @return
     */
    List<Task> getExecuteTask(String serverId);
    
    /**
     * 设置任务执行锁
     * 
     * @param taskId
     * @param expire
     * @param unit
     * @return
     */
    String setExecuteLock(Task task, long expire, TimeUnit unit);
    
    /**
     * 设置任务执行锁过期时间
     * 
     * @param task
     * @param expire
     * @param unit
     */
    void expireTask(Task task, long expire, TimeUnit unit);
    
    /**
     * 任务执行后处理
     * 
     * @param task
     */
    void postExecuteTask(Task task);
    
    /**
     * 服务器任务列表后处理
     * 
     * @param serverId
     */
    void postSeverTask(String serverId);
}
