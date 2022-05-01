package com.voyager.community.controller;

import com.voyager.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Voyager1
 * @create 2022-04-30 15:56
 */

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    //ajax测试
    @PostMapping("/ajax")
    @ResponseBody
    public String testAjax(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }
}
