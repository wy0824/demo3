package com.winter.demo3.async.handler;

import com.winter.demo3.async.EventConsumer;
import com.winter.demo3.async.EventHandler;
import com.winter.demo3.async.EventModel;
import com.winter.demo3.async.EventType;
import com.winter.demo3.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AddQuestionHandler implements EventHandler{
    @Autowired
    SearchService searchService;

    private static final Logger logger = LoggerFactory.getLogger(AddQuestionHandler.class);

    @Override
    public void doHandler(EventModel model) {
        try{
            searchService.indexQuestion(model.getEntityId(),model.getExts("title"),model.getExts("content"));
        }catch (Exception e){
            logger.error("增加题目索引失败"+e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.ADD_QUESTION);
    }
}
