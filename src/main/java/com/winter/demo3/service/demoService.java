package com.winter.demo3.service;

import org.springframework.stereotype.Service;

@Service
public class demoService {
    public String getMessage(int id){
        return "Message is : " + String.valueOf(id);
    }
}
