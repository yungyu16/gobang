package com.github.yungyu16.gobang;

import com.github.yungyu16.gobang.dao.entity.UserRecord;
import com.github.yungyu16.gobang.domain.UserDomain;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2019/11/10.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisTest {

    @Autowired
    private UserDomain userDomain;

    @Test
    public void test() {
        UserRecord entity = new UserRecord();
        entity.setUserName("11");
        entity.setMobile("11");
        entity.setPwd("11");
        userDomain.save(entity);
    }
}
