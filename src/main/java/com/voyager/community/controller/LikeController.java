package com.voyager.community.controller;

import com.voyager.community.entity.Event;
import com.voyager.community.entity.User;
import com.voyager.community.event.EventProducer;
import com.voyager.community.service.LikeService;
import com.voyager.community.util.CommunityConstant;
import com.voyager.community.util.CommunityUtil;
import com.voyager.community.util.HostHolder;
import com.voyager.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Voyager1
 * @create 2022-04-03 15:17
 */

/**
 * 点赞
 */
@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞
     * @param entityType 点赞主体类型（帖子/pinglun）
     * @param entityId 点赞主体Id
     * @param entityUserId 点赞 帖子/评论的作者 id
     * @param postId 帖子的 id （点赞了哪个帖子，点赞的评论属于哪个帖子，点赞的回复属于哪个帖子）
     * @return
     */
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId){
        User user = hostHolder.getUser();
        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //点赞数量
        Long likeCount = likeService.findEntityLikeCount(entityType,entityId);
        //点赞状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(),entityType,entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        //触发点赞事件（系统通知） - 取消点赞不通知
        if(likeStatus == 1){
            Event event = new Event();
            event.setTopic(TOPIC_LIKE);
            event.setUserId(user.getId());
            event.setEntityType(entityType);
            event.setEntityId(entityId);
            event.setEntityUserId(entityUserId);
            Map<String,Object> data = new HashMap<>();
            data.put("postId",postId);
            event.setData(data);
            eventProducer.fireEvent(event);
        }
        if(entityType == ENTITY_TYPE_POST){
            //计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey,postId);
        }
        return CommunityUtil.getJSONString(0,null,map);
    }
}
