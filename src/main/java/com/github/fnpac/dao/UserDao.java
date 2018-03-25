package com.github.fnpac.dao;

import com.github.fnpac.domain.UserInfo;

import java.util.List;

/**
 * Created by 刘春龙 on 2018/3/22.
 */
public interface UserDao {

    List<UserInfo> findAll();

    UserInfo findByUsername(String username);

    void save(UserInfo userInfo);

    int[] update(List<UserInfo> userInfos);
}
