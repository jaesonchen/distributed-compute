package com.asiainfo.dcompute.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.asiainfo.dcompute.model.Task;
import com.asiainfo.dcompute.service.IDExecutor;
import com.asiainfo.dcompute.service.IDManagerService;
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
    private IDManagerService manager;
    @Autowired
    private IDTaskDetailService detail;
    private List<IDExecutor> executors;
    @Autowired
    public void setExecutors(List<IDExecutor> executors) {
        this.executors = executors;
    }

    /* 
     * TODO
     * @param task
     * @return
     * @see com.asiainfo.dcompute.service.IDTaskService#getExecutor(com.asiainfo.dcompute.model.Task)
     */
    @Override
    public IDExecutor getExecutor(Task task) {
        
        for (IDExecutor executor : this.executors) {
            if (executor.isTaskExecutor(task)) {
                return executor;
            }
        }
        return (null == this.executors || this.executors.isEmpty()) ? null : this.executors.get(0);
    }
    
    /* 
     * TODO
     * @return
     * @see com.asiainfo.dcompute.service.ITaskService#getScheduleTask()
     */
    @Override
    public List<Task> getScheduleTask() {

        List<Task> result = new ArrayList<>();
        List<Task> list = this.detail.queryScheduleTask();
        for (Task task : list)  {
            // 任务执行锁不为空，表示上次调度还未执行完成
            if (!this.manager.existLock(DComputeCodes.DISTRIBUTE_COMPUTE_TASK_PREFIX + task.getTaskId())) {
                result.add(task);
            }
        }
        // 防止查询任务时未完成，遍历锁时任务执行完成
        List<Task> deleteDup = this.detail.queryScheduleTask();
        result.retainAll(deleteDup);
        return result;
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
        List<Task> list = this.manager.getTaskList(serverId);
        for (Task task : list) {
            String currentLock = this.manager.getLock(DComputeCodes.DISTRIBUTE_COMPUTE_TASK_PREFIX + task.getTaskId());
            if (!StringUtils.isEmpty(currentLock) 
                    && currentLock.equals(task.getLockId())
                    && serverId.equals(task.getServerId())) {
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
        return this.manager.acquireLock(DComputeCodes.DISTRIBUTE_COMPUTE_TASK_PREFIX + task.getTaskId(), expire, unit);
    }

    /* 
     * TODO
     * @param serverId
     * @see com.asiainfo.dcompute.service.IDTaskService#preServerTask(java.lang.String)
     */
    @Override
    public void preServerTask(String serverId) {
        this.manager.expireLock(DComputeCodes.DISTRIBUTE_COMPUTE_SERVER_PREFIX + serverId, null, 5, TimeUnit.MINUTES);
    }
    
    /* 
     * TODO
     * @param serverId
     * @see com.asiainfo.dcompute.service.IDTaskService#postSeverTask(java.lang.String)
     */
    @Override
    public void postSeverTask(String serverId) {
        this.manager.removeServerTask(serverId);
    }

    /* 
     * TODO
     * @param task
     * @see com.asiainfo.dcompute.service.IDTaskService#preTask(com.asiainfo.dcompute.model.Task)
     */
    @Override
    public void preTask(Task task) {
        
        IDExecutor executor = this.getExecutor(task);
        boolean expire = this.manager.expireLock(DComputeCodes.DISTRIBUTE_COMPUTE_TASK_PREFIX + task.getTaskId(), 
                task.getLockId(), executor.getExecuteTime(task), TimeUnit.SECONDS);
        if (!expire) {
            throw new RuntimeException("task(" + task.getTaskId() + ") expired!");
        }
    }

    /* 
     * TODO
     * @param task
     * @see com.asiainfo.dcompute.service.IDTaskService#postTask(com.asiainfo.dcompute.model.Task)
     */
    @Override
    public void postTask(Task task) {
        this.manager.releaseLock(DComputeCodes.DISTRIBUTE_COMPUTE_TASK_PREFIX + task.getTaskId(), task.getLockId());
    }
}
