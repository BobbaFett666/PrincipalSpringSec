package com.victor.springsec.example.controller

import java.security.Principal

import javax.annotation.Resource

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockHttpSession
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

import spock.lang.Specification

import com.victor.springsec.example.config.AppConfiguration
import com.victor.springsec.example.domain.User
import com.victor.springsec.example.service.UserService

@WebAppConfiguration
@ContextConfiguration(classes=[AppConfiguration.class])
class MainControllerSpec extends Specification
{
	
	@Resource
	private FilterChainProxy springSecurityFilterChain;
	
	@Autowired
	private WebApplicationContext webCtx
	
	@Autowired
	MainController testController
	
	AuthenticationManager authMgr = { authentication ->
		[
			isAuthenticated: { true },
			getPrincipal: { [
					getName: { "Test User"}
				] as Principal },
			getAuthorities: { [] },
			getCredentials: { null },
			getDetails: { null },
			setAuthenticated: {  }
		] as Authentication
	} as AuthenticationManager
	
	def "Verify creation of the Authentication Manager"()
	{
		expect:
			authMgr
			webCtx
			testController
			
	}
	
	def "Calling the Controller with no arguments"() 
	{
		given:
			Principal principal = getTestPrincipal()
			MockHttpSession session = new MockHttpSession();

			session.setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				new MockSecurityContext(principal))

			//MockMvc mockMvc = MockMvcBuilders.standaloneSetup(mainController).build()
			MockMvc mockMvc = setupMockMvc()
			
		when:
			MvcResult aResult = mockMvc.perform(MockMvcRequestBuilders.get("/api").session(session))
										.andDo(MockMvcResultHandlers.print())
										.andReturn()
		
		then:
			aResult
			
	}
	
	def "Calling the Controller"() 
	{
		given:
			UserService userServiceMock = Mock()
			testController.setUserService(userServiceMock)
			
			Principal principal = getTestPrincipal()
			MockHttpSession session = new MockHttpSession();

			session.setAttribute(
				HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
				new MockSecurityContext(principal))

			MockMvc mockMvc = setupMockMvc()
			
		when:
			MvcResult aResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/user").session(session))
				.andDo(MockMvcResultHandlers.print())
				.andReturn()
		
		then:
			1 * userServiceMock.getUserByUserName(_ as String) >> new User(10L, "TEST USER")
			println aResult.getResponse().getContentAsString()
	}
	
	
	Authentication getTestPrincipal()
	{
		User aUser = new User(9L, 'Victor')
		new UsernamePasswordAuthenticationToken(aUser, aUser.getName(), [])
	}
	
	MockMvc setupMockMvc()
	{
		MockMvcBuilders.webAppContextSetup(this.webCtx).addFilters(this.springSecurityFilterChain).build()
	}

	public static class MockSecurityContext implements SecurityContext
	{
		private static final long serialVersionUID = -1386535243513362694L;
		
		private Authentication authentication;
	
		public MockSecurityContext(Authentication authentication)
		{
			this.authentication = authentication;
		}
	
		@Override
		public Authentication getAuthentication()
		{
			return this.authentication;
		}
	
		@Override
		public void setAuthentication(Authentication authentication)
		{
			this.authentication = authentication;
		}
		
	}
	
}



