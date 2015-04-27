package com.victor.springsec.example.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.victor.springsec.example.domain.User;
import com.victor.springsec.example.service.UserService;

@RestController
public class MainController
{
	@Autowired
	private UserService userService;
	
	public void setUserService(UserService pUserService)
	{
		this.userService = pUserService;
	}

	@RequestMapping(value="/api", method=RequestMethod.GET)
	public User getRoot()
	{
		System.out.println("*********************** Called getRoot()");
		return new User(300L, "USER TEST");
	}

	@RequestMapping(value="/api/user", method=RequestMethod.GET)
	public User getUserInfo(Principal pPrincipal)
	{
		System.out.println("*********************** Called getUserInfo()");
		User aUser = this.userService.getUserByUserName(pPrincipal.getName());
		
		return aUser;
	}
}
