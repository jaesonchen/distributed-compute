package com.asiainfo.dcompute.service;

import com.asiainfo.dcompute.model.Task;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月16日  下午2:07:46
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public interface IDExecutor {

    /**
     * 任务对应的执行器
     * 
     * @param task
     * @return
     */
    boolean isTaskExecutor(Task task);
    
    /**
     * 任务处理方法
     * 
     * @param serverId
     * @param task
     * @return
     */
    int executor(String serverId, Task task);
    
    /**
     * 任务有效期时长(单位秒)
     * 
     * @param task
     * @return
     */
    long getExecuteTime(Task task);
}
