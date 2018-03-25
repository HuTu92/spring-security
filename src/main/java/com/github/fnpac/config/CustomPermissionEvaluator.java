package com.github.fnpac.config;

import com.github.fnpac.domain.UserInfo;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;

/**
 * Created by 刘春龙 on 2018/3/25.
 */
public class CustomPermissionEvaluator implements PermissionEvaluator {

    // 管理员权限
    private static final GrantedAuthority ADMIN_AUTHORITY =
            new SimpleGrantedAuthority("ROLE_ADMIN");

    /**
     * @param authentication
     * @param targetDomainObject 待评估的对象
     * @param permission         操作的权限
     * @return
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

        if (targetDomainObject instanceof UserInfo) {
            UserInfo userInfo = (UserInfo) targetDomainObject;
            String username = userInfo.getUsername();
            if ("delete".equals(permission)) {
                return isAdmin(authentication) ||
                        username.equals(authentication.getName());
            }
        }
        throw new UnsupportedOperationException(
                "hasPermission not supported for object <" + targetDomainObject
                        + "> and permission <" + permission + ">");
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new UnsupportedOperationException();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().contains(ADMIN_AUTHORITY);
    }
}
