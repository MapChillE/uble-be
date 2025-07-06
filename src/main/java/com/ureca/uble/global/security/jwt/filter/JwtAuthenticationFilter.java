package com.ureca.uble.global.security.jwt.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ureca.uble.domain.auth.exception.AuthErrorCode;
import com.ureca.uble.domain.users.exception.UserErrorCode;
import com.ureca.uble.domain.users.repository.UserRepository;
import com.ureca.uble.entity.User;
import com.ureca.uble.global.exception.GlobalException;
import com.ureca.uble.global.security.jwt.JwtValidator;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtValidator jwtValidator;
	private final UserRepository userRepository;

	public JwtAuthenticationFilter(JwtValidator jwtValidator, UserRepository userRepository) {
		this.jwtValidator = jwtValidator;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		ServletException, IOException {

		String token = jwtValidator.extractAccessToken(request);

		if(token != null && jwtValidator.validateToken(token)) {
			Long userId = jwtValidator.getUserIdFromToken(token);
			User user = userRepository.findById(userId)
				.orElseThrow(() -> new GlobalException(UserErrorCode.USER_NOT_FOUND));
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole())));
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}


}
