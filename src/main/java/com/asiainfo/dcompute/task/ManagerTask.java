package com.asiainfo.dcompute.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.asiainfo.dcompute.model.Task;
import com.asiainfo.dcompute.service.IDManagerService;
import com.asiainfo.dcompute.service.IDTaskService;
import com.asiainfo.dcompute.util.DComputeCodes;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月13日  上午9:55:59
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
@Component
public class ManagerTask implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagerTask.class);
    
    @Autowired
    private IDManagerService manager;
    @Autowired
    private IDTaskService task;
    @Value("${dcompute.serverid:1001}")
    private String serverId;
    
    /* 
     * TODO
     * @see java.lang.Runnable#run()
     */
    @Override
    @Scheduled(fixedDelay=60000)
    public void run() {
        
        Thread.currentThread().setName("分布式任务调度线程(" + this.serverId + ")");
        LOGGER.info("服务器({})开始运行分布式任务调度...", this.serverId);
        //判断是否leader服务器
        if (!this.manager.isLeaderServer(this.serverId, 2, TimeUnit.MINUTES)) {
            LOGGER.info("服务器({})不是当前leader服务器，直接返回！", this.serverId);
            return;
        }
        //获取redis调度分布式锁
        String lockId = this.manager.acquireLock(DComputeCodes.DISTRIBUTE_COMPUTE_MANAGER_LOCK, 5, TimeUnit.MINUTES);
        if (StringUtils.isEmpty(lockId)) {
            LOGGER.info("服务器({})未能获取到分布式任务调度锁，未到调度间隔或者上次调度leader宕机，直接返回！", this.serverId);
            return;
        }
        LOGGER.info("服务器({})成功获取到分布式任务调度锁(lockId={})，开始执行任务调度...", this.serverId, lockId);
        //执行任务调度
        try {
            //心跳正常的空闲服务器
            List<String> servers = this.manager.getFreeServer(2, TimeUnit.MINUTES);
            if (null == servers || servers.isEmpty()) {
                LOGGER.info("未能查询到心跳正常的空闲服务器，本次调度直接返回！");
                return;
            }
            LOGGER.info("心跳正常的空闲服务器{}台！", servers.size());
            //待调度任务（剔除执行中的任务）
            List<Task> tasks = this.task.getScheduleTask();
            if (null == tasks || tasks.isEmpty()) {
                LOGGER.info("当前没有待执行的任务，调度返回！");
                return;
            }
            LOGGER.info("待执行任务{}个！", tasks.size());
            //任务分配、执行锁设置
            Map<String, List<Task>> scheduleMap = new HashMap<>();
            int i = 0;
            for (Task task : tasks) {
                String execLock = this.task.setExecuteLock(task, 5L, TimeUnit.MINUTES);
                if (!StringUtils.isEmpty(execLock)) {
                    String serverId = servers.get(i++ % servers.size());
                    if (null == scheduleMap.get(serverId)) {
                        scheduleMap.put(serverId, new ArrayList<Task>());
                    }
                    task.setLockId(execLock);
                    task.setServerId(serverId);
                    scheduleMap.get(serverId).add(task);
                }
            }
            //任务分配
            this.manager.dispatcher(scheduleMap, 2, TimeUnit.MINUTES);
            LOGGER.info("服务器({})任务调度完成, 任务调度结果:{}", this.serverId, scheduleMap);
        } catch (Exception ex) {
            LOGGER.error("服务器({})执行任务调度时出现异常！\n{}", this.serverId, ex);
        } finally {
            LOGGER.info("服务器({})任务调度退出, 设置下次调度时间(2分钟之后){}！", this.serverId, 
                    this.manager.expireLock(DComputeCodes.DISTRIBUTE_COMPUTE_MANAGER_LOCK, lockId, 2, TimeUnit.MINUTES) ? "成功" : "失败");
        }
    }
}
