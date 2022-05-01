package com.voyager.commuinty.mapperTest;

import com.voyager.community.CommunityApplication;
import com.voyager.community.dao.DiscussPostMapper;
import com.voyager.community.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Voyager1
 * @create 2022-04-21 22:20
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class DiscussPostMapperTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void findDiscussPostTest(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 5, 0);
        for(DiscussPost discussPost:list){
            System.out.println(discussPost);
        }
    }

    @Test
    public void testSelectDiscussPostById(){
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(2);
        System.out.println(discussPost);
    }

    @Test
    public void testFindDiscussCount(){
        int count = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(count);
    }
}
