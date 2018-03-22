package com.github.fnpac.config;

import com.github.fnpac.dao.UserDao;
import com.github.fnpac.domain.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 刘春龙 on 2018/3/21.
 */
public class CustomUserService implements UserDetailsService {

    private UserDao userDao;

    public CustomUserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserInfo userInfo = userDao.findByUsername(s);

        if (userInfo != null) {

            List<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add((GrantedAuthority) () -> "ROLE_FNPAC");
            // User 实现了 UserDetails 接口
            User user = new User(userInfo.getUsername(), userInfo.getPassword(), authorities);

            return user;
        } else {
            throw new UsernameNotFoundException("User '" + userInfo.getUsername() + "' not found.");
        }
    }
}
