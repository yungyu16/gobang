package com.github.yungyu16.gobang.config;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.type.JdbcType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Configuration
public class MybatisPlusConfig implements MetaObjectHandler {

    @Bean
    public MybatisSqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setMapperLocations(getMapperLocations());
        sqlSessionFactoryBean.setDataSource(dataSource);
        return config(sqlSessionFactoryBean);
    }

    private MybatisSqlSessionFactoryBean config(MybatisSqlSessionFactoryBean sqlSessionFactoryBean) {
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);

        GlobalConfig globalConfig = GlobalConfigUtils.defaults();
        globalConfig.setMetaObjectHandler(this);

        sqlSessionFactoryBean.setGlobalConfig(globalConfig);

        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        sqlSessionFactoryBean.setConfiguration(configuration);
        sqlSessionFactoryBean.setPlugins(interceptors());
        return sqlSessionFactoryBean;
    }

    private Resource[] getMapperLocations() {
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        try {
            return patternResolver.getResources("classpath*:mapper/**/*.xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PaginationInterceptor[] interceptors() {
        return new PaginationInterceptor[]{new PaginationInterceptor()};
    }

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