package com.uu2.tinyagents.memory.mybatis.mapper;

import com.uu2.tinyagents.memory.mybatis.MessageModal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageModalMapper {

    @Select("select id,chat_id as chatId, clazz, message, created_at as createdAt from t_chat_message where chat_id = #{chatId}")
    List<MessageModal> selectList(@Param("chatId") String chatId);

    @Select("select id,chat_id as chatId, clazz, message, created_at as createdAt from t_chat_message where chat_id = #{chatId} order by id desc limit #{count}")
    List<MessageModal> selectPage(@Param("count") int count,
                                  @Param("chatId") String chatId);

    @Select("insert into t_chat_message (chat_id, clazz, message, created_at) values (#{chatId}, #{clazz}, #{message}, #{createdAt})")
    void insert(MessageModal messageModal);

    @Select("delete from t_chat_message where chat_id = #{chatId}")
    void deleteByChatId(@Param("chatId") String chatId);
}
