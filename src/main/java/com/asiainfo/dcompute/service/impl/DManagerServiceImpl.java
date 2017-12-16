package com.asiainfo.dcompute.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
     * @param time
     * @param unit
     * @return
     * @see com.asiainfo.dcompute.service.IDManagerService#getLeaderServer(long, java.util.concurrent.TimeUnit)
     */
    @Override
    public String getLeaderServer(long time, TimeUnit unit) {

        List<String> list = this.getSurviveServer(time, unit);
        list = null == list ? new ArrayList<String>() : list;
        Collections.sort(list);
        return list.isEmpty() ? null : list.get(0);
    }

    /* 
     * TODO
     * @param serverId
     * @param time
     * @param unit
     * @return
     * @see com.asiainfo.dcompute.service.IDManagerService#isLeaderServer(java.lang.String, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean isLeaderServer(String serverId, long time, TimeUnit unit) {
        
        String leader = this.getLeaderServer(time, unit);
        return null == leader ? false : leader.equals(serverId);
    }
    
    /* 
     * TODO
     * @param scheduleMap
     * @param time
     * @param unit
     * @see com.asiainfo.dcompute.service.IDManagerService#dispatcher(java.util.Map, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public void dispatcher(Map<String, List<Task>> scheduleMap, long time, TimeUnit unit) {

        for (Map.Entry<String, List<Task>> entry : scheduleMap.entrySet()) {
            this.redisService.setObject(DComputeCodes.DISTRIBUTE_COMPUTE_SERVER_PREFIX + entry.getKey(), 
                    entry.getValue(), unit.toSeconds(time));
        }
    }

    /* 
     * TODO
     * @param serverId
     * @return
     * @see com.asiainfo.dcompute.service.IDManagerService#getTaskList(java.lang.String)
     */
    @Override
    public List<Task> getTaskList(String serverId) {
        
        @SuppressWarnings("unchecked")
        List<Task> list = (List<Task>) this.redisService.getObject(DComputeCodes.DISTRIBUTE_COMPUTE_SERVER_PREFIX + serverId);
        return null == list ? new ArrayList<Task>() : list;
    }

    /* 
     * TODO
     * @param serverId
     * @see com.asiainfo.dcompute.service.IDManagerService#removeServerTask(java.lang.String)
     */
    @Override
    public void removeServerTask(String serverId) {
        this.redisService.remove(DComputeCodes.DISTRIBUTE_COMPUTE_SERVER_PREFIX + serverId);
    }
    
    /* 
     * TODO
     * @param key
     * @param time
     * @param unit
     * @return
     * @see com.asiainfo.dcompute.service.IDManagerService#acquireLock(java.lang.String, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public String acquireLock(String key, long time, TimeUnit unit) {
        return this.redisService.acquireLock(key, unit.toSeconds(time));
    }

    /* 
     * TODO
     * @param key
     * @param lockId
     * @param time
     * @param unit
     * @return
     * @see com.asiainfo.dcompute.service.IDManagerService#expireLock(java.lang.String, java.lang.String, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public boolean expireLock(String key, String lockId, long time, TimeUnit unit) {

        if (StringUtils.isEmpty(lockId)) {
            return this.redisService.expire(key, time, unit);
        }
        String currentLock = this.getLock(key);
        if (null != currentLock && currentLock.equals(lockId)) {
            return this.redisService.expire(key, time, unit);
        }
        return false;
    }

    /* 
     * TODO
     * @param key
     * @param lockId
     * @return
     * @see com.asiainfo.dcompute.service.IDManagerService#releaseLock(java.lang.String, java.lang.String)
     */
    @Override
    public boolean releaseLock(String key, String lockId) {
        return this.redisService.releaseLock(key, lockId);
    }

    /* 
     * TODO
     * @param key
     * @return
     * @see com.asiainfo.dcompute.service.IDManagerService#getLock(java.lang.String)
     */
    @Override
    public String getLock(String key) {
        
        Object lock = this.redisService.getObject(key);
        return null == lock ? null : String.valueOf(lock);
    }

    /* 
     * TODO
     * @param key
     * @return
     * @see com.asiainfo.dcompute.service.IDManagerService#existLock(java.lang.String)
     */
    @Override
    public boolean existLock(String key) {
        return this.redisService.containsKey(key);
    }
}
