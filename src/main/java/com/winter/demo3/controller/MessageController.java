package com.winter.demo3.controller;

import com.winter.demo3.dao.MessageDAO;
import com.winter.demo3.model.HostHolder;
import com.winter.demo3.model.Message;
import com.winter.demo3.model.User;
import com.winter.demo3.service.MessageService;
import com.winter.demo3.service.UserService;
import com.winter.demo3.util.DemoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class MessageController {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @RequestMapping(path = {"/msg/addMessage"},method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content){
        try{
            if(hostHolder.getUser() == null){
                return DemoUtil.getJSONString(999,"未登录");
            }
            User user  = userService.selectByName(toName);
            if(user == null){
                return DemoUtil.getJSONString(1,"用户不存在");
            }

            Message message = new Message();
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setContent(content);
            messageService.addMessage(message);
            return DemoUtil.getJSONString(0);
        }catch(Exception e){
            logger.error("发送消息失败"+e.getMessage());
            return DemoUtil.getJSONString(1,"发信失败");
        }
    }

}
