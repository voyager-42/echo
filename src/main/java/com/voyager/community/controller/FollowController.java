package com.voyager.community.controller;

import com.voyager.community.entity.Event;
import com.voyager.community.entity.Page;
import com.voyager.community.entity.User;
import com.voyager.community.event.EventProducer;
import com.voyager.community.service.FollowService;
import com.voyager.community.service.UserService;
import com.voyager.community.util.CommunityConstant;
import com.voyager.community.util.CommunityUtil;
import com.voyager.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * @author Voyager1
 * @create 2022-04-03 15:42
 */

/**
 * 关注（只做了关注用户）
 */
@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    /**
     * 关注
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/follow")
    @ResponseBody
    public String follow(int entityType,int entityId){
        User user = hostHolder.getUser();

        followService.follow(user.getId(),entityType,entityId);

        //触发关注事件（系统通知）
        Event event = new Event();
        event.setTopic(TOPIC_FOLLOW);
        event.setUserId(user.getId());
        event.setEntityType(entityType);
        event.setEntityId(entityId);
        event.setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0,"已关注");
    }

    /**
     * 取消关注
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/unfollow")
    @ResponseBody
    public String unfollow(int entityType,int entityId){
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(),entityType,entityId);

        return CommunityUtil.getJSONString(0,"已经取消关注");
    }

    /**
     * 某个用户的关注列表（人）
     * @param userId
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findUserById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId,ENTITY_TYPE_USER));

        //获取关注列表
        List<Map<String,Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());

        if(userList != null){
            for(Map<String,Object> map : userList){
                User u = (User) map.get("user");// 被关注的用户
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",userList);

        return "/site/followee";
    }

    /**
     * 某个用户的粉丝列表
     * @return
     */
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId,Page page,Model model){
        User user = hostHolder.getUser();
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);

        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER,userId));

        //获取关注列表
        List<Map<String,Object>> userList = followService.findFollowers(userId, page.getOffset(),page.getLimit());

        if(userList != null){
            for(Map<String,Object> map : userList){
                User u = (User) map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users",userList);

        return "/site/follower";
    }

    /**
     * 判断当前登录用户是否已关注某个用户
     * @param userId 某个用户
     * @return
     */
    public boolean hasFollowed(int userId){
        if (hostHolder.getUser() == null){
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(),ENTITY_TYPE_USER,userId);
    }
}
