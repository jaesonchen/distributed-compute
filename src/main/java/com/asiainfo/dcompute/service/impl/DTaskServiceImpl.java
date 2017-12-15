package com.asiainfo.dcompute.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.asiainfo.biapp.mcd.redis.IRedisService;
import com.asiainfo.dcompute.model.Task;
import com.asiainfo.dcompute.service.IDTaskDetailService;
import com.asiainfo.dcompute.service.IDTaskService;
import com.asiainfo.dcompute.util.DComputeCodes;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月11日  下午4:29:30
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
@Service
public class DTaskServiceImpl implements IDTaskService {

    @Autowired
    private IRedisService redis;
    @Autowired
    private IDTaskDetailService detail;

    /* 
     * TODO
     * @return
     * @see com.asiainfo.dcompute.service.ITaskService#getExecuteTask()
     */
    @Override
    public List<Task> getExecuteTask() {

        List<Task> result = new ArrayList<>();
        List<Task> list = this.detail.queryScheduleTask();
        for (Task task : list)  {
            // 任务执行锁不为空，表示上次调度还未执行完成
            String lockId = this.redis.getString(DComputeCodes.DISTRIBUTE_COMPUTE_TASK_PREFIX + task.getTaskId());
            if (StringUtils.isEmpty(lockId)) {
                result.add(task);
            }
        }
        return result;
    }

    /* 
     * TODO
     * @param task
     * @param expire
     * @param unit
     * @return
     * @see com.asiainfo.dcompute.service.ITaskService#setExecuteLock(com.asiainfo.dcompute.model.Task, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public String setExecuteLock(Task task, long expire, TimeUnit unit) {
        return this.redis.acquireLock(DComputeCodes.DISTRIBUTE_COMPUTE_TASK_PREFIX + task.getTaskId(), unit.toSeconds(expire));
    }

    /* 
     * TODO
     * @param task
     * @param expire
     * @param unit
     * @see com.asiainfo.dcompute.service.IDTaskService#expireTask(com.asiainfo.dcompute.model.Task, long, java.util.concurrent.TimeUnit)
     */
    @Override
    public void expireTask(Task task, long expire, TimeUnit unit) {
        this.redis.expire(DComputeCodes.DISTRIBUTE_COMPUTE_TASK_PREFIX + task.getTaskId(), unit.toSeconds(expire));
    }
    
    /* 
     * TODO
     * @param task
     * @see com.asiainfo.dcompute.service.IDTaskService#postExecuteTask(com.asiainfo.dcompute.model.Task)
     */
    @Override
    public void postExecuteTask(Task task) {
        this.redis.releaseLock(DComputeCodes.DISTRIBUTE_COMPUTE_TASK_PREFIX + task.getTaskId(), task.getLockId());
    }

    /* 
     * TODO
     * @param serverId
     * @return
     * @see com.asiainfo.dcompute.service.ITaskService#getExecuteTask(java.lang.String)
     */
    @Override
    public List<Task> getExecuteTask(String serverId) {
        
        List<Task> result = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Task> list = (List<Task>) this.redis.getObject(DComputeCodes.DISTRIBUTE_COMPUTE_SERVER_PREFIX + serverId);
        list = null == list ? new ArrayList<Task>() : list;
        for (Task task : list) {
            Object currentLock = this.redis.getObject(DComputeCodes.DISTRIBUTE_COMPUTE_TASK_PREFIX + task.getTaskId());
            if (!StringUtils.isEmpty(currentLock) 
                    && currentLock.equals(task.getLockId())
                    && serverId.equals(task.getServerId())) {
                result.add(task);
            }
        }
        //读取后直接删除服务器任务列表
        this.redis.remove(DComputeCodes.DISTRIBUTE_COMPUTE_SERVER_PREFIX + serverId);
        return result;
    }

    /* 
     * TODO
     * @param serverId
     * @see com.asiainfo.dcompute.service.IDTaskService#postSeverTask(java.lang.String)
     */
    @Override
    public void postSeverTask(String serverId) {
        //this.redis.remove(DComputeCodes.DISTRIBUTE_COMPUTE_SERVER_PREFIX + serverId);
    }
}
