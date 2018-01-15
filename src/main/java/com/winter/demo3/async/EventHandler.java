package com.winter.demo3.async;

import java.util.List;

public interface EventHandler {
    void doHandler(EventModel medel);

    List<EventType> getSupportEventTypes();
}
