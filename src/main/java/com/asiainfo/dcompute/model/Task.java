package com.asiainfo.dcompute.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月11日  下午4:10:06
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public class Task implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private String taskId;
    private String desc;
    private int type;
    private String lockId;
    private String serverId;
    private int resultCode;
    private String resultDesc;
    private Map<String, Object> detail = new HashMap<>();
    
    public Task() {}
    public Task(String taskId, String desc, int type) {
        this.taskId = taskId;
        this.desc = desc;
        this.type = type;
    }
    public String getTaskId() {
        return taskId;
    }
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getLockId() {
        return lockId;
    }
    public void setLockId(String lockId) {
        this.lockId = lockId;
    }
    public String getServerId() {
        return serverId;
    }
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
    public int getResultCode() {
        return resultCode;
    }
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }
    public String getResultDesc() {
        return resultDesc;
    }
    public void setResultDesc(String resultDesc) {
        this.resultDesc = resultDesc;
    }
    public Map<String, Object> getDetail() {
        return detail;
    }
    public void setDetail(Map<String, Object> detail) {
        this.detail = detail;
    }
    @Override
    public boolean equals(Object obj) {
        
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Task)) {
            return false;
        }
        Task target = (Task) obj;
        return this.type == target.type && this.taskId.equals(target.taskId);
    }
    @Override
    public int hashCode() {
        
        final int prime = 31;
        int result = 1;
        result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
        result = prime * result + type;
        return result;
    }
    @Override
    public String toString() {
        return "Task [taskId=" + taskId + ", desc=" + desc + ", type=" + type + ", lockId=" + lockId + ", serverId="
                + serverId + ", resultCode=" + resultCode + ", resultDesc=" + resultDesc + ", detail=" + detail + "]";
    }
}
