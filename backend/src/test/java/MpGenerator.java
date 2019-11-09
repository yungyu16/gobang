package com.github.yungyu16.gobang;

import cn.xiaoshidai.common.toolkit.system.SystemProperiesTools;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: ============================
 * <p>
 * CreatedDate: 2019/8/31
 * Author: songjialin
 */
public class MpGenerator {

    private static final String BASE_PACKAGE = "com.github.yungyu16.gobang";

    private static Map<String, DataSourceConfig> dataSourceConfigMap = Maps.newHashMap();

    @Test
    public void appGenerate() {
        generate("t_user");
    }

    public void generate(String tableName) {
        AutoGenerator gen = new AutoGenerator();
        gen.setDataSource(datasource());

        String baseProjectPath = Paths.get(".").toAbsolutePath().toString();

        String currentSystemUserName = SystemProperiesTools.getCurrentSystemUserName();

        gen.setGlobalConfig(new GlobalConfig()
                .setOutputDir(Paths.get(baseProjectPath, "./src/main/java").toAbsolutePath().toString())
                .setFileOverride(false)
                .setActiveRecord(false)
                .setEnableCache(false)
                .setBaseResultMap(true)
                .setBaseColumnList(true)
                .setOpen(false)
                .setAuthor(currentSystemUserName)
                .setMapperName("%sMapper")
                .setXmlName("%sMapper")
                .setServiceName("%sDomain")
                .setServiceImplName("%sDomainImpl")
                .setControllerName("%sController")
                .setEntityName("%sRecord")
        );

        gen.setStrategy(
                new StrategyConfig()
                        .setTablePrefix("t_")
                        .setNaming(NamingStrategy.underline_to_camel)
                        .setInclude(tableName)
                        .setRestControllerStyle(true)
                        //.setSuperEntityClass("cn.xiaoshidai.web.open.platform.boot.base.dao.BaseEntity")
                        //.setSuperEntityColumns("id", "remark", "modify_time", "create_time", "is_deleted")
                        //.setSuperMapperClass("cn.xiaoshidai.web.open.platform.boot.base.dao.BaseMapper")
                        .setEntityColumnConstant(true)
                        .setEntityLombokModel(true)
                        .setRestControllerStyle(false)
        );

        gen.setPackageInfo(new PackageConfig()
                .setParent(BASE_PACKAGE)// 自定义包路径
                .setController("web.http")// 这里是控制器包名，默认 web
                .setEntity("dao.entity")
                .setMapper("dao.mapper")
                .setService("domain")
                .setServiceImpl("domain.impl")
        );

        InjectionConfig abc = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        };
        List<FileOutConfig> fileOutList = new ArrayList<>();
        fileOutList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                return baseProjectPath + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper.xml";
            }
        });
        abc.setFileOutConfigList(fileOutList);
        gen.setCfg(abc);

        gen.setTemplateEngine(new FreemarkerTemplateEngine());
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.setXml(null);
        templateConfig.setController(null);
        gen.setTemplate(templateConfig);

        gen.execute();
    }

    public static DataSourceConfig datasource() {
        try {
            Yaml yaml = new Yaml();
            Map result = yaml.loadAs(new ClassPathResource("application.yml").getInputStream(), Map.class);
            Map spring = (Map) result.get("spring");
            Map<String, String> datasource = (Map<String, String>) spring.get("datasource");
            System.out.println(datasource);
            DataSourceConfig appConfig = new DataSourceConfig();
            appConfig.setDbType(DbType.MYSQL);
            appConfig.setDriverName("com.mysql.jdbc.Driver");
            appConfig.setUsername(datasource.get("username"));
            appConfig.setPassword(datasource.get("password"));
            appConfig.setUrl(datasource.get("url"));
            return appConfig;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}