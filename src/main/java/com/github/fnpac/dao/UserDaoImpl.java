package com.github.fnpac.dao;

import com.github.fnpac.domain.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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
    public List<UserInfo> findAll() {
        return jdbcTemplate.query("SELECT * FROM users", (resultSet, i) -> {
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(resultSet.getString("username"));
            userInfo.setPassword(resultSet.getString("password"));
            userInfo.setEnabled(resultSet.getBoolean("enabled"));
            return userInfo;
        });
    }

    @Override
    public UserInfo findByUsername(String username) {
        return jdbcTemplate.query("SELECT * FROM users WHERE username = ?", new Object[]{username}, (resultSet, i) -> {
            UserInfo userInfo = new UserInfo();
            userInfo.setUsername(resultSet.getString("username"));
            userInfo.setPassword(resultSet.getString("password"));
            userInfo.setEnabled(resultSet.getBoolean("enabled"));
            return userInfo;
        }).get(0);
    }

    @Override
    public void save(UserInfo userInfo) {
        jdbcTemplate.update("INSERT INTO users VALUES (?, ?, ?)", userInfo.getUsername(), userInfo.getPassword(), userInfo.isEnabled());
    }

    /**
     * 基于spring security校验只能更新当前用户的UserInfo
     *
     * @param userInfos
     */
    @PreFilter(filterTarget = "userInfos", value = "filterObject.username == authentication.name")
    @Override
    public int[] update(List<UserInfo> userInfos) {
        int[] ids = jdbcTemplate.batchUpdate("UPDATE users SET enabled = ? WHERE username = ?", new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setBoolean(1, !userInfos.get(i).isEnabled());
                ps.setString(2, userInfos.get(i).getUsername());
            }

            /**
             * 这个方法设定更新记录数，通常List里面存放的都是我们要更新的，所以返回list.size()
             *
             * @return
             */
            @Override
            public int getBatchSize() {
                return userInfos.size();
            }
        });

        return ids;

//        int id = jdbcTemplate.update("UPDATE users SET enabled = ? WHERE username = ?", new PreparedStatementSetter() {
//
//            @Override
//            public void setValues(PreparedStatement ps) throws SQLException {
//                ps.setBoolean(1, !userInfos.get(0).isEnabled());
//                ps.setString(2, userInfos.get(0).getUsername());
//            }
//        });
//
//        return new int[]{id};
    }
}
