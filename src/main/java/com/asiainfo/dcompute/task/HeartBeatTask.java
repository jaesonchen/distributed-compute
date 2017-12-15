package com.asiainfo.dcompute.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.asiainfo.dcompute.service.IDManagerService;

/**
 * 心跳任务，定时发送心跳
 * 
 * @author       zq
 * @date         2017年12月10日  下午6:08:50
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
@Component
public class HeartBeatTask implements Runnable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatTask.class);
    
    @Autowired
    private IDManagerService manager;
    @Value("${dcompute.serverid:1001}")
    private String serverId;

    /* 
     * TODO
     * @see java.lang.Runnable#run()
     */
    @Override
    @Scheduled(fixedDelay=60000)
    public void run() {
        
        Thread.currentThread().setName("心跳线程(" + this.serverId + ")");
        LOGGER.info("服务器({})开始发送心跳...", this.serverId);
        try {
            this.manager.heartbeat(this.serverId);
        } catch (Exception ex) {
            LOGGER.error("服务器({})发送心跳异常！\n{}", this.serverId, ex);
        }
    }
}
