package com.gba.client.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.gba.client.util.UserContextUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Author liuxudong
 * @Date 2024/1/10
 * @Description mybatisPlus属性自动填充配置
 **/
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "creator", Long.class, UserContextUtil.getUserId());
        this.strictInsertFill(metaObject, "operator", Long.class, UserContextUtil.getUserId());
        this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
        this.strictInsertFill(metaObject, "version", Integer.class, 0);
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "operator", Long.class, UserContextUtil.getUserId());
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
