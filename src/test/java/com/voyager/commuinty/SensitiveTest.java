package com.voyager.commuinty;

import com.voyager.community.CommunityApplication;
import com.voyager.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Voyager1
 * @create 2022-04-22 10:56
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
@SpringBootTest
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String text1 = "社会主义新中国不容许存在赌博，吸毒，嫖娼等不良行为";
        String filterText = sensitiveFilter.filter(text1);
        System.out.println(filterText);
    }
}
