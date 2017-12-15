package com.asiainfo.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年12月13日  下午1:34:11
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
@SpringBootApplication
@ComponentScan({"com.asiainfo.biapp.mcd.redis", "com.asiainfo.dcompute", "com.asiainfo.example.service"})
public class ApplicationOne {

    /** 
     * TODO
     * 
     * @param args
     */
    public static void main(String[] args) {
        
        SpringApplication app = new SpringApplication(new Object[] {ApplicationOne.class});
        app.setAdditionalProfiles(new String[] {"server1"});
        app.run(args);
    }
}
