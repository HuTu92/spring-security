package com.github.fnpac.config;

import com.github.fnpac.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import javax.sql.DataSource;

/**
 * Created by 刘春龙 on 2018/3/21.
 */
@EnableWebSecurity
public class WebSecurityConfig {

    private static UserDao userDao;
    private static DataSource dataSource;

    @Autowired
    public WebSecurityConfig(DataSource dataSource, UserDao userDao) {
        WebSecurityConfig.dataSource = dataSource;
        WebSecurityConfig.userDao = userDao;
    }

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {
        /**
         * 请求重定向路径为：
         * http://localhost:8080/spring-security/rest/user?username=liucl
         * -> https://localhost:8443/spring-security/rest/user?username=liucl
         * -> https://localhost:8443/spring-security/login
         * -> https://localhost:8443/spring-security/rest/user?username=liucl
         *
         * @param http
         * @throws Exception
         */
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/api/**")
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    /*
                        请求接口，会返回如下响应：
                                HTTP/1.1 401 Unauthorized
                                ...
                                WWW-Authenticate: Basic realm="fnpac"

                        浏览器会弹出对话框，要求输入用户名密码，验证成功后，会生成“Authorization”请求头，其值为Basic bGl1Y2w6cXdlNTIxMzE0
                        其中bGl1Y2w6cXdlNTIxMzE0 为 liucl:qwe521314 的base64编码，将密码暴露出来了，一定要使用SSL加密通道
                     */
                    .httpBasic()
                    // 浏览器会弹出对话框并提示：https://localhost:8443 正在请求您的用户名和密码。该网站称：“fnpac”
                    .realmName("fnpac")
                    .and()
                    // 防止跨站请求伪造
                    // 默认值：
                    // static final String DEFAULT_CSRF_COOKIE_NAME = "XSRF-TOKEN";
                    // static final String DEFAULT_CSRF_PARAMETER_NAME = "_csrf";
                    // static final String DEFAULT_CSRF_HEADER_NAME = "X-XSRF-TOKEN";
                    // private String parameterName = DEFAULT_CSRF_PARAMETER_NAME;
                    // private String headerName = DEFAULT_CSRF_HEADER_NAME;
                    // private String cookieName = DEFAULT_CSRF_COOKIE_NAME;
                /*
                    从Spring Security 3.2开始，默认就会启用CSRF防护。实际上，除非你
                    采取行为处理CSRF防护或者将这个功能禁用，否则的话，在应用中提交表单时，你可能会遇到问题

                    Spring Security通过一个同步token的方式来实现CSRF防护的功能。
                    它将会拦截状态变化的请求（例如，非GET、HEAD、OPTIONS和TRACE的请求）并检查CSRF token。
                    如果请求中不包含CSRF token的话，或者token不能与服务器端的token相匹配，请求将会失败，并抛出CsrfException异常。
                 */
                    .csrf()
                    .ignoringAntMatchers("/api/**") // 通过此设置，可以指定path不开启csrf
//                    .disable() // 禁用
                    .and()
                    .requiresChannel()
                    // 强制通道的安全性，此外需要配置tomcat证书，以支持ssl
                    // 不论何时， 只要是对“/api”的请求， Spring Security都视为需要安全通道（通过调用requiresChannel()确定的）
                    // 并自动将请求重定向到HTTPS上。
                    .antMatchers("/api/**").requiresSecure();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    // 基于数据库表进行认证
                    .jdbcAuthentication()
                    .dataSource(dataSource)
                    // 配置自定义的用户服务
//                    .userDetailsService(new CustomUserService(userDao))
                    .passwordEncoder(new StandardPasswordEncoder("53cr3t"));
        }
    }

    @Configuration
    @Order(2)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) throws Exception {
            web
                    .ignoring()
                    .antMatchers("/resources/**", "/static/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    // form 表单增加“remember-me”请求参数
                    // 生成“remember-me”Cookie，其值为bGl1Y2w6MTUyNDEyNjU4OTUyNjpiMzU2ZTFmMjA3ZGU2NjA0NTUyYTliODBjZTAzNTJjYg
                    // 存储在cookie中的token包含用户名、密码、过期时间和一个私钥——在写入cookie前都进行了MD5哈希。
                    // 默认情况下，私钥的名为SpringSecured，但在这里我们将其设置为fnpacKey
                    .rememberMe()
                    .tokenValiditySeconds(2419200)
                    .key("fnpacKey")
                    .and()
                    // 默认登陆地址：http://localhost:8080/spring-security/login
                    .formLogin(); // 生成login页面参见DefaultLoginPageGeneratingFilter
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    // 基于数据库表进行认证
                    .jdbcAuthentication()
                    .dataSource(dataSource)
                    // 配置自定义的用户服务
//                    .userDetailsService(new CustomUserService(userDao))
                    .passwordEncoder(new StandardPasswordEncoder("53cr3t"));
        }
    }
}
