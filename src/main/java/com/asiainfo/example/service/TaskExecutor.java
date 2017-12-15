package com.asiainfo.example.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asiainfo.dcompute.model.Task;
import com.asiainfo.dcompute.service.IDTaskService;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月15日  上午10:37:12
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
@Component
public class TaskExecutor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskExecutor.class);
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    
    @Autowired
    private IDTaskService service;
    @Value("${dcompute.serverid:1001}")
    private String serverId;
    
    /* 
     * TODO
     * @see java.lang.Runnable#run()
     */
    @Override
    @Scheduled(fixedDelay=60000)
    public void run() {

        Thread.currentThread().setName("分发线程(" + this.serverId + ")");
        try {
            LOGGER.info("服务器({}) 查询待执行任务！", this.serverId);
            //任务查询
            List<Task> list = this.service.getExecuteTask(this.serverId);
            if (null == list || list.isEmpty()) {
                LOGGER.info("服务器({}) 没有分配到待执行任务！", this.serverId);
                return;
            }
            LOGGER.info("服务器({}) 分配到{}个待执行任务！", this.serverId, list.size());
            //任务分发
            for (Task task : list) {
                if (1 == task.getType() || 2 == task.getType()) {
                    executor.execute(new TaskOne(this.service, this.serverId, task));
                } else {
                    executor.execute(new TaskTwo(this.service, this.serverId, task));
                }
            }
            LOGGER.info("服务器({}) 任务分发完成！", this.serverId);
        } catch (Exception ex) {
            LOGGER.info("服务器({}) 分发任务时出现异常。\n{}", this.serverId, ex);
        } finally {
            LOGGER.info("服务器({}) 清除待执行任务列表！", this.serverId);
            //服务器调度后处理
            this.service.postSeverTask(this.serverId);
        }
    }
}
