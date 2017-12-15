package com.asiainfo.example.service;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.dcompute.model.Task;
import com.asiainfo.dcompute.service.IDTaskService;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月13日  下午1:46:42
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public class TaskOne implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskOne.class);
    
    private IDTaskService service;
    private String serverId;
    private Task task;
    
    public TaskOne(IDTaskService service, String serverId, Task task) {
        this.service = service;
        this.serverId = serverId;
        this.task = task;
    }
    
    /* 
     * TODO
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        Thread.currentThread().setName("任务1/2处理线程(" + this.serverId + ")");
        try {
            LOGGER.info("服务器({}) 开始执行执行任务task={}", this.serverId, this.task);
            //设置任务执行时间
            this.service.expireTask(this.task, 5L, TimeUnit.MINUTES);
            //执行逻辑
            TimeUnit.SECONDS.sleep(10L);
            LOGGER.info("服务器({}) 执行任务(taskId={})完成！", this.serverId, this.task.getTaskId());
        } catch (Exception ex) {
            LOGGER.info("服务器({}) 执行任务(taskId={})异常。\n{}", this.serverId, ex);
        } finally {
            LOGGER.info("服务器({}) 释放任务(taskId={})执行锁！", this.serverId, this.task.getTaskId());
            //任务后处理
            this.service.postExecuteTask(task);
        }
    }
}
