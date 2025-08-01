package com.gba.client.support;

import com.gba.client.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Author: liuxudong
 * @Description:
 * @Date: Created in 2024/1/10
 */
@Order(1)
@Component
@Slf4j
public class LoadRoleStarter implements CommandLineRunner {
    @Autowired
    RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        roleService.getRoleAll();
        log.info("----------<角色信息>加载成功----------");
    }
}
