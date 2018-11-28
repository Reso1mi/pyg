package com.pyg.cart.confirmService;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class UserDetailServiceImpl implements UserDetailsService {
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("经过认证类");
        List<GrantedAuthority> authorities=new ArrayList();
        //???????????? 我去最开始把这里的role写错了，然后cas登陆成功后也一直被403 forbid，因为SpringSecurity认证没通过
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        //不通过本项目做登陆 所以密码无所谓，在执行这个方法的时候就已经登陆成功了
        return new User(username,"",authorities);
    }
}
