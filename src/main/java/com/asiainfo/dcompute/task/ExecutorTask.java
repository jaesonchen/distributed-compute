package com.asiainfo.dcompute.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asiainfo.dcompute.model.Task;
import com.asiainfo.dcompute.service.IDExecutor;
import com.asiainfo.dcompute.service.IDTaskService;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月16日  下午2:19:33
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public class ExecutorTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorTask.class);
    
    private IDTaskService service;
    private Task task;
    private String serverId;
    
    public ExecutorTask(String serverId, IDTaskService service, Task task) {
        this.serverId = serverId;
        this.service = service;
        this.task = task;
    }
    
    /* 
     * TODO
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        Thread.currentThread().setName("服务器(" + this.serverId + ")任务(" + this.task.getTaskId() + ")运行线程");
        try {
            //任务预执行
            this.service.preTask(task);
            LOGGER.info("服务器({}) 开始运行任务({})！", this.serverId, this.task.getTaskId());
            //获取执行器
            IDExecutor executor = this.service.getExecutor(this.task);
            //运行任务
            LOGGER.info("服务器({}) 运行任务({})完成，运行结果：{}", this.serverId, this.task.getTaskId(), 
                    executor.executor(this.serverId, this.task));
        } catch (Exception ex) {
            LOGGER.error("服务器({}) 运行任务({})时出现异常。\n{}", this.serverId, this.task.getTaskId(), ex);
        } finally {
            LOGGER.info("服务器({})任务({})退出，进行任务后处理！", this.serverId, this.task.getTaskId());
            //任务后处理
            this.service.postTask(this.task);
        }
    }
}
