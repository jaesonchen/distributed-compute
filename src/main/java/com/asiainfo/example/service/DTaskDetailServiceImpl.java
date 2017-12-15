package com.asiainfo.example.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.asiainfo.dcompute.model.Task;
import com.asiainfo.dcompute.service.IDTaskDetailService;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月13日  下午1:41:31
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
@Service
public class DTaskDetailServiceImpl implements IDTaskDetailService {

    /* 
     * TODO
     * @return
     * @see com.asiainfo.dcompute.service.IDTaskDetailService#queryScheduleTask()
     */
    @Override
    public List<Task> queryScheduleTask() {
        
        return new ArrayList<Task>(Arrays.asList(new Task[] {
                new Task("10001", "task 10001", 1), 
                new Task("10002", "task 10002", 1), 
                new Task("10003", "task 10003", 2), 
                new Task("10004", "task 10004", 2),
                new Task("10005", "task 10007", 3),
                new Task("10006", "task 10006", 3),
                new Task("10007", "task 10007", 4),
                new Task("10008", "task 10008", 4),}));
    }

    /* 
     * TODO
     * @param taskId
     * @return
     * @see com.asiainfo.dcompute.service.IDTaskDetailService#queryByTaskId(java.lang.String)
     */
    @Override
    public Task queryByTaskId(String taskId) {

        List<Task> tasks = this.queryScheduleTask();
        for (Task task : tasks) {
            if (!StringUtils.isEmpty(taskId) && taskId.equals(task.getTaskId())) {
                return task;
            }
        }
        return null;
    }
}
