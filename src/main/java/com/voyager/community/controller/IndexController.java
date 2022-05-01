package com.voyager.community.controller;

/**
 * @author Voyager1
 * @create 2022-04-02 10:34
 */

import com.voyager.community.entity.DiscussPost;
import com.voyager.community.entity.Page;
import com.voyager.community.entity.User;
import com.voyager.community.service.DiscussPostService;
import com.voyager.community.service.LikeService;
import com.voyager.community.service.UserService;
import com.voyager.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页
 */
@Controller
public class IndexController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @GetMapping("/")
    public String root(){
        return "forward:/index";
    }

    /**
     * 进入首页
     * @param model
     * @param page
     * @param orderMode 默认是 0（最新） orderMode = 1（按热度排名）
     * @return
     */
    @GetMapping("/index")
    public String getIndexPage(Model model,Page page,@RequestParam(name = "orderMode",defaultValue = "0")int orderMode){
        //方法调用前，SpringMVC 会自动实例化 Model 和 Page 并将page注入到Model中
        //所以 thymeleaf 中可以直接访问 Page 对象中的数据

        //获取总页数
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);

        //分页查询
        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(),orderMode);
        //封装帖子和对应的用户信息
        List<Map<String,Object>> discussPosts  = new ArrayList<>();
        if(list != null){
            for (DiscussPost post:list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findUserById(post.getUserId());
                map.put("user",user);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount",likeCount);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode",orderMode);
        return "index";
    }

    /**
     * 进入 500 错误页面
     * @return
     */
    @GetMapping("/error")
    public String getErrorPage(){
        return "error/500";
    }

    /**
     * 没权限访问时的错误页面（也是404）
     * @return
     */
    @GetMapping("/denied")
    public String getDeniedPage(){
        return "error/404";
    }
}
