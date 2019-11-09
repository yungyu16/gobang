package com.github.yungyu16.gobang.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("开始进行插入时字段自动填充...{}", metaObject.getOriginalObject().getClass().getSimpleName());
        if (getFieldValByName("isDeleted", metaObject) == null) {
            this.setInsertFieldValByName("isDeleted", false, metaObject);
        }
        if (getFieldValByName("createTime", metaObject) == null) {
            this.setInsertFieldValByName("createTime", LocalDateTime.now(), metaObject);
        }
        if (getFieldValByName("modifyTime", metaObject) == null) {
            this.setInsertFieldValByName("modifyTime", LocalDateTime.now(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("开始进行更新时字段自动填充...{}", metaObject.getOriginalObject().getClass().getSimpleName());
        if (getFieldValByName("modifyTime", metaObject) == null) {
            this.setInsertFieldValByName("modifyTime", LocalDateTime.now(), metaObject);
        }
    }
}