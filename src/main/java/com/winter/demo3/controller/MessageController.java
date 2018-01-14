package com.winter.demo3.controller;

import com.winter.demo3.dao.MessageDAO;
import com.winter.demo3.model.HostHolder;
import com.winter.demo3.model.Message;
import com.winter.demo3.model.User;
import com.winter.demo3.model.ViewObject;
import com.winter.demo3.service.MessageService;
import com.winter.demo3.service.UserService;
import com.winter.demo3.util.DemoUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.reflect.MethodDelegate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @RequestMapping(path={"msg/list"},method = {RequestMethod.GET})
    public String getConversationList(Model model){
        if(hostHolder.getUser() == null){
            return "redirect:/relogin";
        }
        int localUserId = hostHolder.getUser().getId();
        List<Message> conversationList = messageService.getConversationList(localUserId,0,10);
        List<ViewObject> conversations = new ArrayList<ViewObject>();
        for(Message message : conversationList){
            ViewObject vo = new ViewObject();
            vo.set("conversation",message);//???
            int targetId = message.getFromId() == localUserId ? message.getToId() : message.getFromId();
            vo.set("user",userService.getUser(targetId));
            vo.set("unread",messageService.getConversationUnreadCount(localUserId,message.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations",conversations);
        return "letter";
    }

    @RequestMapping(path={"msg/detail"},method = {RequestMethod.GET})
    public String getConversationDetail(Model model,@Param("conversationId") String conversationId){
        try{
            List<Message> messageList = messageService.getConversationDetail(conversationId,0,10);
            List<ViewObject> messages = new ArrayList<ViewObject>();
            for(Message message : messageList){
                ViewObject vo = new ViewObject();
                vo.set("message",message);
                User user = userService.getUser(message.getFromId());
                if(user == null){
                    continue;
                }
                vo.set("user",userService.getUser(message.getFromId()));//必要？
                vo.set("headUrl",user.getHeadUrl());
                vo.set("userId",user.getId());
                messages.add(vo);
            }
            model.addAttribute("massages",messages);
        }catch(Exception e){
            logger.error("获取详情失败"+e.getMessage());
        }
        return "letterDetail";
    }

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
