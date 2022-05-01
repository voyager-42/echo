package com.voyager.commuinty.mapperTest;

import com.voyager.community.CommunityApplication;
import com.voyager.community.dao.UserMapper;
import com.voyager.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Voyager1
 * @create 2022-04-24 20:55
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelectUser(){
        User user1 = userMapper.selectById(5);
        System.out.println(user1);

        User user2 = userMapper.selectByName("Voyager");
        System.out.println(user2);

        User user3 = userMapper.selectByEmail("2049678352@qq.com");
        System.out.println(user3);
    }

    @Test
    public void testInsertUser(){

    }

    @Test
    public void testUpdateUser(){

    }


}
