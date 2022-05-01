package com.voyager.community.controller;

/**
 * @author Voyager1
 * @create 2022-04-01 10:55
 */

import com.voyager.community.entity.Comment;
import com.voyager.community.entity.DiscussPost;
import com.voyager.community.entity.Event;
import com.voyager.community.event.EventProducer;
import com.voyager.community.service.CommentService;
import com.voyager.community.service.DiscussPostService;
import com.voyager.community.util.CommunityConstant;
import com.voyager.community.util.HostHolder;
import com.voyager.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 评论/回复
 */
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 添加评论
     * @param discussPostId
     * @param comment
     * @return
     */
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 触发评论事件（系统通知）
        Event event = new Event();
        event.setTopic(TOPIC_COMMNET);
        event.setUserId(hostHolder.getUser().getId());
        event.setEntityType(comment.getEntityType());
        event.setEntityId(comment.getEntityId());
        Map<String,Object> data = new HashMap<>();
        data.put("postId",discussPostId);
        event.setData(data);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件，通过消息队列将其存入 Elasticsearch 服务器
            event = new Event();
            event.setTopic(TOPIC_PUBLISH);
            event.setUserId(comment.getUserId());
            event.setEntityType(ENTITY_TYPE_POST);
            event.setEntityId(discussPostId);
            eventProducer.fireEvent(event);

            // 计算帖子分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }

}
