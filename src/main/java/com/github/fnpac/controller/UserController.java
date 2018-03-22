package com.github.fnpac.controller;

import com.github.fnpac.dao.UserDao;
import com.github.fnpac.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by 刘春龙 on 2018/3/22.
 */
@Controller
@RequestMapping(value = "api")
public class UserController {

    private final UserDao userDao;

    @Autowired
    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @RequestMapping(value = "user", method = RequestMethod.GET)
    public @ResponseBody UserInfo userInfo(@RequestParam("username") String username) {
        return userDao.findByUsername(username);
    }

    @RequestMapping(value = "user/save", method = RequestMethod.POST)
    public @ResponseBody UserInfo save(UserInfo userInfo) {
        StandardPasswordEncoder encoder = new StandardPasswordEncoder("53cr3t");
        String password = encoder.encode(userInfo.getPassword());
        userInfo.setPassword(password);
        userDao.save(userInfo);
        return userInfo;
    }
}
