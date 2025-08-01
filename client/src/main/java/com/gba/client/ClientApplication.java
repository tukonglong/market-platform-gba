package com.gba.client;

import com.alicp.jetcache.anno.config.EnableMethodCache;
import com.alicp.jetcache.anno.support.JetCacheBaseBeans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @Author: liuxudong
 * @Description:
 * @Date: Created in ${DATE}
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableMethodCache(basePackages = "com.gba.client.service.impl")
@Import(JetCacheBaseBeans.class)
@ComponentScan(basePackages = {"com.gba.client", "com.gba.common"})
@EnableAspectJAutoProxy(exposeProxy = true)
public class ClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
