package com.github.fnpac.controller;

import com.alibaba.fastjson.JSON;
import com.github.fnpac.dao.UserDao;
import com.github.fnpac.domain.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by 刘春龙 on 2018/3/22.
 */
@Controller
@RequestMapping(value = "api")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

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

    @RequestMapping(value = "user/update", method = RequestMethod.GET)
    public @ResponseBody String userInfo() {

        List<UserInfo> userInfos = userDao.findAll();
        int[] ids = userDao.update(userInfos);
        logger.info(JSON.toJSONString(userInfos));
        return "{'success': true" + JSON.toJSONString(ids) + "}";
    }
}
