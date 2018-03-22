package com.github.fnpac.dao;

import com.github.fnpac.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by 刘春龙 on 2018/3/22.
 */
@Repository
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserInfo findByUsername(String username) {
        return jdbcTemplate.query("select * from users where username = ?", new Object[]{username}, (resultSet, i) -> {
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(resultSet.getString("username"));
            userInfo.setPassword(resultSet.getString("password"));
            userInfo.setEnabled(resultSet.getBoolean("enabled"));
            return userInfo;
        }).get(0);
    }

    @Override
    public void save(UserInfo userInfo) {
        jdbcTemplate.update("insert into users values (?, ?, ?)", userInfo.getUsername(), userInfo.getPassword(), userInfo.isEnabled());
    }
}
