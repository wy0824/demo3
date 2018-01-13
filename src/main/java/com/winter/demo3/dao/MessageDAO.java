package com.winter.demo3.dao;

import com.winter.demo3.model.Comment;
import com.winter.demo3.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageDAO {

    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id,to_id,content,has_read,conversation_id,created_date ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;
    @Insert({"insert into ", TABLE_NAME, "(",INSERT_FIELDS,
            ") values(#{fromId},#{toId},#{content},#{hasRead},#{conversationId},#{createdDate})"})
    int addMessage(Message message);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME,
            "where conversation_id=#{conversationId} order by created_date desc limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);
}
