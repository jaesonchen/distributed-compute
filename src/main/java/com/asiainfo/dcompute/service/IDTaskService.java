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
     * 任务执行器
     * 
     * @param task
     * @return
     */
    IDExecutor getExecutor(Task task);
    
    /**
     * 返回可调度任务，不包含正在执行的任务
     * 
     * @return
     */
    List<Task> getScheduleTask();
    
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
     * 任务预处理
     * 
     * @param task
     */
    void preTask(Task task);
    
    /**
     * 任务后处理
     * 
     * @param task
     */
    void postTask(Task task);
    
    /**
     * 服务器任务预处理
     * 
     * @param serverId
     */
    void preServerTask(String serverId);
    
    /**
     * 服务器任务后处理
     * 
     * @param serverId
     */
    void postSeverTask(String serverId);
}
