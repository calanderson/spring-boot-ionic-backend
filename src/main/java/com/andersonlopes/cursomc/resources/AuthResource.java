package com.andersonlopes.cursomc.resources;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.andersonlopes.cursomc.dto.EmailDTO;
import com.andersonlopes.cursomc.security.JWTUtil;
import com.andersonlopes.cursomc.security.UserSS;
import com.andersonlopes.cursomc.services.AuthService;
import com.andersonlopes.cursomc.services.UserService;

@RestController
@RequestMapping(value = "/auth")
public class AuthResource {
	
	private static final String BEARER = "Bearer ";
	private static final String AUTHORIZATION = "Authorization";
	private static final String EXPOSE_HEADERS = "access-control-expose-headers";
	@Autowired
	private JWTUtil jwtUtil;
	@Autowired
	private AuthService authService;
	
	@RequestMapping(value = "/refresh_token", method = RequestMethod.POST)
	public ResponseEntity<Void> refreshToken(HttpServletResponse response){
		UserSS user = UserService.authenticated();
		String token = jwtUtil.generateToken(user.getUsername());
		response.addHeader(AUTHORIZATION, BEARER + token);
		response.addHeader(EXPOSE_HEADERS, AUTHORIZATION);
		return ResponseEntity.noContent().build();
	}
	
	@RequestMapping(value = "/forgot", method = RequestMethod.POST)
	public ResponseEntity<Void> forgot(@Valid @RequestBody EmailDTO emailDTO){
		authService.sendNewPassword(emailDTO.getEmail());
		return ResponseEntity.noContent().build();
	}

}
