package com.voyager.community.dao;

import com.voyager.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Voyager1
 * @create 2022-03-18 22:02
 */
@Mapper
public interface MessageMapper {

    /**
     * 查询当前用户发会话列表，针对每个会话只返回一条最新的私信
     * @param userId 用户id
     * @param offset 煤业起始索引
     * @param limit 煤业显示多少条数据
     * @return
     */
    List<Message> selectConversations(int userId, int offset,int limit);

    /**
     * 查询当前用户会话数量
     * @param userId
     * @return
     */
    int selectConversationCount(int userId);

    /**
     * 查询某个会话所包含的私信列表
     * @param conversationId
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectLetters(String conversationId,int offset,int limit);

    /**
     * 查询某个会话所包含的私信数量
     * @param conversationId
     * @return
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询未读私信的数量
     * @param userId
     * @param conversationId conversationId = null, 则查询该用户所有会话的未读私信数量
     *                        conversationId != null, 则查询该用户某个会话的未读私信数量
     * @return
     */
    int selectLetterUnreadCount(int userId,String conversationId);

    /**
     * 修改消息的状态
     * @param ids
     * @param status
     * @return
     */
    int updateStatus(List<Integer> ids, int status);

    /**
     * 新增一条私信
     * @param message
     * @return
     */
    int insertMessage(Message message);

    /**
     * 查询某个主题下最新的通知
     * @param userId
     * @param topic
     * @return
     */
    Message selectLatestNotice(int userId,String topic);

    /**
     * 查询某个主题下包含的系统通知数量
     * @param userId
     * @param topic
     * @return
     */
    int selectNoticeCount(int userId,String topic);

    /**
     * 查询未读的系统通知数量
     * @param userId
     * @param topic
     * @return
     */
    int selectNoticeUnReadCount(int userId,String topic);

    /**
     * 查询某个主题所包含的通知列表
     * @param userId
     * @param topic
     * @param offset
     * @param limit
     * @return
     */
    List<Message> selectNotices(int userId,String topic,int offset,int limit);
}
