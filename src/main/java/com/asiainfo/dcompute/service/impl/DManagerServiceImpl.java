package com.asiainfo.dcompute.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.asiainfo.biapp.mcd.redis.IRedisService;
import com.asiainfo.dcompute.model.Task;
import com.asiainfo.dcompute.service.IDManagerService;
import com.asiainfo.dcompute.service.IDTaskService;
import com.asiainfo.dcompute.util.DComputeCodes;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月10日  下午6:18:51
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
@Service
public class DManagerServiceImpl implements IDManagerService {

    @Autowired
    private IRedisService redisService;

    @Autowired
    IDTaskService taskService;

    /* 
     * TODO
     * @param serverId
     * @see com.asiainfo.dcompute.service.IDComputeManagerService#heartbeat(java.lang.String)
     */
    @Override
    public void heartbeat(String serverId) {
        this.redisService.hSetString(DComputeCodes.DISTRIBUTE_COMPUTE_REGISTRY, serverId, String.valueOf(System.currentTimeMillis()));
    }
    
    /* 
     * TODO
     * @param time
     * @param unit
     * @return
     * @see com.asiainfo.dcompute.service.IDComputeManagerService#getSurviveServer(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public List<String> getSurviveServer(long time, TimeUnit unit) {
        
        List<String> result = new ArrayList<>();
        long expire = unit.toMillis(time);
        Map<String, String> servers = this.redisService.hGetAllString(DComputeCodes.DISTRIBUTE_COMPUTE_REGISTRY);
        for (Map.Entry<String, String> entry : servers.entrySet()) {
            if (Long.valueOf(entry.getValue()) > (System.currentTimeMillis() - expire)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    /* 
     * TODO
     * @param time
     * @param unit
     * @return
     * @see com.asiainfo.dcompute.service.IDManagerService#getFreeServer(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public List<String> getFreeServer(long time, TimeUnit unit) {
        
        List<String> result = new ArrayList<>();
        List<String> list = this.getSurviveServer(time, unit);
        list = null == list ? new ArrayList<String>() : list;
        for (String serverId : list) {
            if (!this.redisService.containsKey(DComputeCodes.DISTRIBUTE_COMPUTE_SERVER_PREFIX + serverId)) {
                result.add(serverId);
            }
        }
        return result;
    }
    
    /* 
     * TODO
     * @param lockId
     * @return
     * @see com.asiainfo.dcompute.service.IDManagerService#releaseManagerLock(java.lang.String)
     */
    @Override
    public boolean releaseManagerLock(String lockId) {
        return this.redisService.releaseLock(DComputeCodes.DISTRIBUTE_COMPUTE_MANAGER_LOCK, lockId);
    }

    /* 
     * TODO
     * @param key
     * @return
     * @see com.asiainfo.dcompute.service.IDManagerService#acquireManagerLock()
     */
    @Override
    public String acquireManagerLock() {
        return this.redisService.acquireLock(DComputeCodes.DISTRIBUTE_COMPUTE_MANAGER_LOCK, 60);
    }

    /* 
     * TODO
     * @param scheduleMap
     * @see com.asiainfo.dcompute.service.IDManagerService#schedule(java.util.Map)
     */
    @Override
    public void schedule(Map<String, List<Task>> scheduleMap) {
        
        for (Map.Entry<String, List<Task>> entry : scheduleMap.entrySet()) {
            this.redisService.setObject(DComputeCodes.DISTRIBUTE_COMPUTE_SERVER_PREFIX + entry.getKey(), 
                    entry.getValue(), 5L * 60);
        }
    }
}
