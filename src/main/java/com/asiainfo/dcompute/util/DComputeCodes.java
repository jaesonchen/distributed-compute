package com.asiainfo.dcompute.util;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月10日  下午5:31:31
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public class DComputeCodes {
    
    /** 服务器注册地址，定时插入服务器id插入map中，key=服务器id，value=timestamp */
    public static final String DISTRIBUTE_COMPUTE_REGISTRY = "dcompute_registry";
    /** 任务分配锁，竞争锁，设置锁失效时间 */
    public static final String DISTRIBUTE_COMPUTE_MANAGER_LOCK = "dcompute_manager_lock";
    /** 服务器分配到的任务列表，值为分配到的执行锁id，设置失效时间 */
    public static final String DISTRIBUTE_COMPUTE_SERVER_PREFIX = "dcompute_server_";
    /** 任务执行锁前缀，由执行服务器在执行完成后释放，锁id被分配给相应的服务器，设置锁失效时间 */
    public static final String DISTRIBUTE_COMPUTE_TASK_PREFIX = "dcompute_task_";
}
