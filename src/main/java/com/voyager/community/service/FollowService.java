package com.voyager.community.service;

import com.voyager.community.entity.User;
import com.voyager.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.voyager.community.util.CommunityConstant.ENTITY_TYPE_USER;

/**
 * @author Voyager1
 * @create 2022-03-21 15:54
 */

@Service
public class FollowService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    /**
     * 关注
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void follow(int userId,int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //生成redis 的 key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                //开启事务管理
                redisOperations.multi();

                //插入数据
                redisOperations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                redisOperations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                //提交事务
                return redisOperations.exec();
            }
        });
    }

    /**
     * 取消关注
     * @param userId
     * @param entityType
     * @param entityId
     */
    public void unfollow(int userId, int entityType,int entityId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //生成redis 的 key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);

                //开启事务管理
                redisOperations.multi();

                //删除数据
                redisOperations.opsForZSet().remove(followeeKey,entityId);
                redisOperations.opsForZSet().remove(followerKey,userId);

                //提交事务
                return redisOperations.exec();
            }
        });
    }

    /**
     * 查询某个用户关注的实体数量
     * @param userId
     * @param entityType
     * @return
     */
    public long findFolloweeCount(int userId, int entityType){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    /**
     * 查询某个实体的粉丝数量
     * @param entityTpe
     * @param entityId
     * @return
     */
    public long findFollowerCount(int entityTpe,int entityId){
        String followerKey = RedisKeyUtil.getFollowerKey(entityTpe, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    /**
     * 判断当前用户是否已关注该实体
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public boolean hasFollowed(int userId, int entityType, int entityId){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().score(followeeKey,entityId) != null;
    }

    /**
     * 分页查询某个用户关注的人（偷个懒，此处没有做对其他实体的关注）
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Map<String, Object>> findFollowees(int userId,int offset, int limit){
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followeeKey,offset,offset + limit-1);
        if(targetIds == null ){
            return null;
        }
        List<Map<String,Object>> list = new ArrayList<>();
        for(Integer targetId:targetIds){
            Map<String, Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followeeKey,targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    /**
     * 分页查询某个用户的粉丝（偷个懒，此处没有做对其他实体的粉丝）
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    public List<Map<String,Object>> findFollowers(int userId,int offset, int limit){
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> targetIds = redisTemplate.opsForZSet().reverseRange(followerKey,offset,offset + limit - 1);
        List<Map<String,Object>> list = new ArrayList<>();
        for(Integer targetId:targetIds){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followerKey,targetId);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}



