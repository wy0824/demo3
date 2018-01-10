package com.winter.demo3.service;

import com.winter.demo3.dao.UserDAO;
import com.winter.demo3.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    public User getUser(int id){
        return userDAO.selectByid(id);
    }
}
