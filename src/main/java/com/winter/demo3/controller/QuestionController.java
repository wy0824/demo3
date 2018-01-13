package com.winter.demo3.controller;

import com.winter.demo3.model.HostHolder;
import com.winter.demo3.model.Question;
import com.winter.demo3.service.QuestionService;
import com.winter.demo3.service.UserService;
import com.winter.demo3.util.DemoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
    @Autowired
    QuestionService questionService;
    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @RequestMapping(value="/question/add",method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,@RequestParam("content") String content){
        try{
            Question question = new Question();
            question.setContent(content);
            question.setTitle(title);
            question.setCreatedDate(new Date());
            question.setCommentCount(0);
            if(hostHolder.getUser() == null){
                question.setUserId(DemoUtil.ANONYMOUS_USERID);
            }else{
                question.setUserId(hostHolder.getUser().getId());
            }
            if(questionService.addQuestion(question) > 0){
                return DemoUtil.getJSONString(0);
            }
        }catch(Exception e){
            logger.error("增加题目失败"+e.getMessage());
        }
        return DemoUtil.getJSONString(1,"失败");
    }

    @RequestMapping(value="/question/{qid}")
    public String questionDetail(Model model,@PathVariable("qid") int qid){
        Question question = questionService.selectById(qid);
        model.addAttribute("question",question);
        model.addAttribute("user",userService.getUser(question.getUserId()));
        return "detail";
    }
}
