package com.github.fnpac.dao;

import com.github.fnpac.domain.UserInfo;

/**
 * Created by 刘春龙 on 2018/3/22.
 */
public interface UserDao {

    UserInfo findByUsername(String username);

    void save(UserInfo userInfo);
}
