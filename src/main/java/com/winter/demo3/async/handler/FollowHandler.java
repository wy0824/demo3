package com.winter.demo3.async.handler;

import com.winter.demo3.async.EventHandler;
import com.winter.demo3.async.EventModel;
import com.winter.demo3.async.EventType;
import com.winter.demo3.model.EntityType;
import com.winter.demo3.model.Message;
import com.winter.demo3.model.User;
import com.winter.demo3.service.FollowService;
import com.winter.demo3.service.MessageService;
import com.winter.demo3.service.UserService;
import com.winter.demo3.util.DemoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
@Component
public class FollowHandler implements EventHandler{
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;


    @Override
    public void doHandler(EventModel model) {
        Message message = new Message();
        message.setFromId(DemoUtil.SYSTEM_USERID);
        message.setToId(model.getEntityOwnerId());
//        message.setToId(model.getActorId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(model.getActorId());
        if(model.getEntityType()== EntityType.ENTITY_QUESTION) {
            message.setContent("用户" + user.getName() +
                    "关注了你,http://127.0.0.1:8080/question/" + model.getEntityId());
        }else if(model.getEntityType() == EntityType.ENTITY_USER){
            message.setContent("用户"+user.getName()+
            "关注了你,http://127.0.0.1:8080/user/"+model.getActorId());
        }
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
