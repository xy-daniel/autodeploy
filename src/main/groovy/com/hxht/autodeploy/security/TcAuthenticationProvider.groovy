package com.hxht.autodeploy.security

import com.hxht.autodeploy.manager.authority.RoleMenu
import com.hxht.autodeploy.manager.authority.User
import grails.gorm.transactions.Transactional
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException

import javax.security.auth.login.AccountExpiredException
import javax.security.auth.login.AccountLockedException

@Transactional
class TcAuthenticationProvider implements AuthenticationProvider {

    private static final Log log = LogFactory.getLog(TcAuthenticationProvider.class)

    @Override
    Authentication authenticate(Authentication authentication) throws AuthenticationException {
        def username = authentication.getName()
        def password = authentication.getCredentials().toString()

        def user = User.findByUsernameAndPassword(username, password)
        if (!user) {
            throw new UsernameNotFoundException("用户名或密码错误！")//用户不存在
        }
        if (user.accountExpired || user.accountLocked) {
            throw new AccountExpiredException("账号已经过期！")//账户过期与密码过期
        }
        if (user.accountLocked) {
            throw new AccountLockedException("账号已被锁定！")//账号被锁定
        }
        def grantedAuth = []
        def roleList = user.authorities*.authority

        //所有角色的url写入到权限
        def roles = user.authorities
        for (def role : roles) {
            if (role.authority == "ROLE_SUPER" || role.authority == "ROLE_ADMIN") {
                continue
            }
            def roleMenuList = RoleMenu.findAllByRole(role)
            for (def roleMenu : roleMenuList) {
                def url = roleMenu.menu.url
                roleList.add("ROLE_" + url)
            }
            roleList.remove(role.authority)
        }
        for (def userRole : roleList) {
            grantedAuth.add(new SimpleGrantedAuthority(userRole))
        }
        println user.toString()
        return new UsernamePasswordAuthenticationToken(user, password, grantedAuth)
    }

    @Override
    boolean supports(Class<?> authentication) {
        return authentication == UsernamePasswordAuthenticationToken.class
    }
}
