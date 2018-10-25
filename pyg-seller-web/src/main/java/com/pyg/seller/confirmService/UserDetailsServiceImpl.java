package com.pyg.seller.confirmService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.pyg.pojo.TbSeller;
import com.pyg.sellergoods.service.SellerService;

public class UserDetailsServiceImpl implements UserDetailsService{

	//这里就不引用了，直接配个bean熟悉下以前的
	private SellerService service;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		//先得到那个对象
		TbSeller seller = service.findOne(username);
		if(seller!=null) {
			if(seller.getStatus().equals("1")) {
				//构建角色列表
				List<GrantedAuthority> grantAuths=new ArrayList();
				grantAuths.add(new SimpleGrantedAuthority("ROLE_SELLER"));
				return new User(username, seller.getPassword(), grantAuths);
			}
		}
		return null;
	}

	//要通过bean的方式来注入必须要get方法
	public void setService(SellerService service) {
		this.service = service;
	}

}
