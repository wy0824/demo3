package com.winter.demo3.dao;

import com.winter.demo3.model.Comment;
import com.winter.demo3.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id,content,created_date,entity_id,entity_type,status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;
    @Insert({"insert into ", TABLE_NAME, "(",INSERT_FIELDS,
            ") values(#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})"})
    int addComment(Comment comment);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME,
            " where id=#{id}"})
    Comment getCommentById(int id);

    @Select({"select ",SELECT_FIELDS," from ",TABLE_NAME,
            " where entity_id=#{entityId} and entity_type=#{entityType} order by created_date desc"})
    List<Comment> selectCommentByEntity(@Param("entityId") int entityId,
                                         @Param("entityType") int entityType);

    @Select({"select count(id) from ",TABLE_NAME,
            " where entity_id=#{entityId} and entity_type=#{entityType}"})
    int getCommentCount(@Param("entityId") int entityId,@Param("entityType") int entityType);

    @Update({"update ",TABLE_NAME," set status=#{status} where entity_id=#{entityId} and entity_type=#{entityType}"})
    void updateStatus(@Param("entityId") int entityId,
                     @Param("entityType") int entityType,
                     @Param("status") int status);

    @Select({"select count(id) from ",TABLE_NAME," where user_id=#{userId}"})
    int getUserCommentCount(int userId);
//    @Update({"update ",TABLE_NAME, " set password=#{password} where id=#{id}"})
//    void updatePassword(User user);
//
//    @Delete({"delete from ",TABLE_NAME, " where id=#{id}"})
//    void deleteByid(int id);
}
