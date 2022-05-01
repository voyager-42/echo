package com.voyager.community.controller;


import com.voyager.community.service.DataService;
import com.voyager.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

/**
 * @author Voyager1
 * @create 2022-04-04 20:06
 */

/**
 * 网站数据
 */
@Controller
public class DataController implements CommunityConstant {

    @Autowired
    private DataService dataService;

    /**
     * 进入统计页面
     * @return
     */
    @RequestMapping(value = "/data", method = {RequestMethod.GET,RequestMethod.POST})
    public String getDataPage(){
        return "/site/admin/data";
    }

    /**
     * 统计网站 uv(独立用户访问)
     * @param start
     * @param end
     * @param model
     * @return
     */
    @PostMapping("/data/uv")
    public String getUV(@DateTimeFormat(pattern = "yy-MM-dd")Date start,
                        @DateTimeFormat(pattern = "yy-MM-dd")Date end,
                        Model model){
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate",start);
        model.addAttribute("uvEndDate",end);
        return "forward:/data";
    }

    /**
     * 统计网站 dau(日活跃用户数量)
     * @param start
     * @param end
     * @param model
     * @return
     */
    @PostMapping("/data/dau")
    public String getDAU(@DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                         @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                         Model model){
        long dau = dataService.calculateDAU(start,end);
        model.addAttribute("dauResult",dau);
        model.addAttribute("dauStartDAte",start);
        model.addAttribute("dauEndDate",end);
        return "forward:/data";
    }
}
