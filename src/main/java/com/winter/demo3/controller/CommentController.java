package com.winter.demo3.controller;

import com.winter.demo3.model.Comment;
import com.winter.demo3.model.EntityType;
import com.winter.demo3.model.HostHolder;
import com.winter.demo3.model.Question;
import com.winter.demo3.service.CommentService;
import com.winter.demo3.service.QuestionService;
import com.winter.demo3.util.DemoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
public class CommentController {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @RequestMapping(path = {"/addComment"},method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            if (hostHolder.getUser() != null) {
                comment.setUserId(hostHolder.getUser().getId());
            } else {
                comment.setUserId(DemoUtil.ANONYMOUS_USERID);
//            return "redirect:/relogin"
            }
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_QUESTION);//添加问题？？？
            comment.setEntityId(questionId);
            comment.setStatus(0);
            commentService.addComment(comment);
            int count = commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            questionService.updateCommentCount(comment.getEntityId(),count);
        }catch (Exception e){
            logger.error("增加评论失败 "+e.getMessage());
        }
        return "redirect:/question/" + String.valueOf(questionId);
    }
}
