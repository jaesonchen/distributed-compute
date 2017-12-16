package com.asiainfo.dcompute.service;

import java.util.List;

import com.asiainfo.dcompute.model.Task;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月13日  上午11:35:16
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public interface IDTaskDetailService {
    
    /**
     * 查询所有待调度的任务，包含已分配未执行完成的任务
     * 
     * @return
     */
    List<Task> queryScheduleTask();
}
