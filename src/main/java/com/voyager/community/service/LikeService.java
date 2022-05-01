package com.voyager.community.service;

import com.voyager.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author Voyager1
 * @create 2022-03-21 15:53
 */

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞
     * @param userId 点赞用户Id
     * @param entityType
     * @param entityId
     * @param entityUserId  被点赞的帖子/评论的作者Id
     */
    public void like(int userId,int entityType,int entityId,int entityUserId){
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);

                //判断用户是否已经点过赞了
                boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey,userId);

                //开启事务
                redisOperations.multi();

                if(isMember){
                    //如果用户已经点过赞了，则取消第二次点赞
                    redisOperations.opsForSet().remove(entityLikeKey,userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                }else{
                    redisTemplate.opsForSet().add(entityLikeKey,userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }
                return redisOperations.exec();//提交事务
            }
        });
    }

    /**
     * 查询某实体被点赞的数量
     * @param entityType
     * @param entityId
     * @return
     */
    public long findEntityLikeCount(int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    /**
     * 查询某个用户对某个实体的点赞状态（是否点赞）
     * @param userId
     * @param entityType
     * @param entityId
     * @return
     */
    public int findEntityLikeStatus(int userId, int entityType,int entityId){
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId) ? 1:0;
    }

    /**
     * 查询某个用户获得赞数量
     * @param userId
     * @return
     */
    public int findUserLikeCount(int userId){
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0:count;
    }
}
