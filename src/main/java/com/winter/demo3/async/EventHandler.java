package com.winter.demo3.async;

import java.util.List;

public interface EventHandler {
    void doHandler(EventModel model);

    List<EventType> getSupportEventTypes();
}
