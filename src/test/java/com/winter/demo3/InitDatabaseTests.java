package com.winter.demo3;

import com.winter.demo3.dao.QuestionDAO;
import com.winter.demo3.dao.UserDAO;
import com.winter.demo3.model.EntityType;
import com.winter.demo3.model.Question;
import com.winter.demo3.model.User;
import com.winter.demo3.service.FollowService;
import com.winter.demo3.util.JedisAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Random;
@RunWith(SpringRunner.class)
@SpringBootTest
@Sql("/init-schema.sql")
public class InitDatabaseTests {
    @Autowired
    UserDAO userDAO;

    @Autowired
    QuestionDAO questionDAO;
    @Autowired
    FollowService followService;
    @Autowired
    JedisAdapter jedisAdapter;
    @Test
    public void InitDatabaseTests(){
        Random random = new Random();
        jedisAdapter.getJedis().flushDB();
        for(int i = 0; i < 11; ++i){
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("USER%d",i+1));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);

            for(int j = 1; j < i; ++j){
                followService.follow(j, EntityType.ENTITY_USER,i);
            }
            user.setPassword("xxx");
            userDAO.updatePassword(user);
            Question question = new Question();
            question.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime()+1000*3600*i);
            question.setCreatedDate(date);
            question.setUserId(i+1);
            question.setTitle(String.format("TITLE{%d}",i));
            question.setContent(String.format("yeah such a fucking beautiful day %d",i));
            questionDAO.addQuestion(question);
        }
        Assert.assertEquals("xxx",userDAO.selectByid(1).getPassword());
//        userDAO.deleteByid(1);
//        Assert.assertNull(userDAO.selectByid(1));
//        System.out.print(questionDAO.selectLatestQuestions(0,0,10));
    }


}
