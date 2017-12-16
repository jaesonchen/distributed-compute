package com.asiainfo.example.service;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.asiainfo.dcompute.model.Task;
import com.asiainfo.dcompute.service.IDExecutor;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月13日  下午1:46:42
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
@Service
public class TaskOne implements IDExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskOne.class);

    /* 
     * TODO
     * @param task
     * @return
     * @see com.asiainfo.dcompute.service.IDExecutor#isTaskExecutor(com.asiainfo.dcompute.model.Task)
     */
    @Override
    public boolean isTaskExecutor(Task task) {

        if (1 == task.getType() || 2 == task.getType()) {
            return true;
        }
        return false;
    }

    /* 
     * TODO
     * @param serverId
     * @param task
     * @return
     * @see com.asiainfo.dcompute.service.IDExecutor#executor(java.lang.String, com.asiainfo.dcompute.model.Task)
     */
    @Override
    public int executor(String serverId, Task task) {

        int resultCode = 1;
        try {
            //执行逻辑
            LOGGER.info("服务器({}) 任务({})开始逻辑处理！", serverId, task.getTaskId());
            TimeUnit.SECONDS.sleep(10L);
            LOGGER.info("服务器({}) 任务({})逻辑处理完成！", serverId, task.getTaskId());
            resultCode = 0;
        } catch (Exception ex) {
            LOGGER.error("服务器({}) 任务({})逻辑处理异常!", serverId, task.getTaskId());
            resultCode = 2;
        } finally {
            LOGGER.info("服务器({}) 任务({})保存处理结果({})！", serverId, task.getTaskId(), resultCode);
            task.setResultCode(resultCode);
            task.setResultDesc(0 == resultCode ? "成功" : 1 == resultCode ? "失败" : "异常");
        }
        return resultCode;
    }

    /* 
     * TODO
     * @param task
     * @return
     * @see com.asiainfo.dcompute.service.IDExecutor#getExecuteTime(com.asiainfo.dcompute.model.Task)
     */
    @Override
    public long getExecuteTime(Task task) {
        return 10L * 60;
    }
}
