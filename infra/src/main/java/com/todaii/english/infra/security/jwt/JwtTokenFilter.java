package com.todaii.english.infra.security.jwt;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.todaii.english.shared.constants.SecurityConstants;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

// kế thừa OncePerRequestFilter để đảm bảo mỗi request chỉ xác thực qua filter 1 lần
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

	private final JwtUtility jwtUtility;

	/*
	 * có interface JwtUserDetailsFactory chứa hàm trích thông tin từ claim và 2
	 * class AdminDetailsFactory/UserDetaildFactory implements interface này, khi
	 * inject kiểu này Spring sẽ tự nhận diện và inject thành map
	 * 
	 * { "adminDetailsFactory" -> AdminDetailsFactory instance, "userDetailsFactory"
	 * -> UserDetailsFactory instance }
	 */
	private final Map<String, JwtUserDetailsFactory> factories;

	@Qualifier("handlerExceptionResolver")
	private final HandlerExceptionResolver handlerExceptionResolver;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			// 1. Nếu không có Authorization header → cho đi luôn
			if (!hasAuthorizationBearer(request)) {
				filterChain.doFilter(request, response);
				return;
			}

			// 2. Lấy token và validate
			String token = getBearerToken(request);
			Claims claims = jwtUtility.validateAccessToken(token);

			// 3. Xác định actorType (ADMIN/USER)
			String actorType = claims.get("actorType", String.class);
			/*
			 * factories hiện tại là map chứa các key(tên bean đã implements
			 * JwtUserDetailsFactory)-value(class implements)
			 * 
			 * đang ghép lại thành tên bean để lấy trong map
			 */
			JwtUserDetailsFactory factory = factories.get(actorType.toLowerCase() + "DetailsFactory");
			if (factory == null) {
				throw new JwtValidationException("Unsupported actorType: " + actorType);
			}

			// 4. Build UserDetails và set vào SecurityContext
			UserDetails userDetails = factory.build(claims);
			setAuthenticationContext(userDetails, request);

			// 5. Cho request tiếp tục
			filterChain.doFilter(request, response);

		} catch (Exception ex) {
			LOGGER.error("JWT filter error: {}", ex.getMessage(), ex);
			throw ex;
		} finally {
			// 6. Clear context cho request này (tránh rò rỉ qua thread khác)
			SecurityContextHolder.clearContext();
		}
	}

	private boolean hasAuthorizationBearer(HttpServletRequest request) {
		String header = request.getHeader(SecurityConstants.HEADER_STRING);
		LOGGER.debug("Authorization header: {}", header);
		return !(ObjectUtils.isEmpty(header) || !header.startsWith(SecurityConstants.TOKEN_PREFIX));
	}

	private String getBearerToken(HttpServletRequest request) {
		String header = request.getHeader(SecurityConstants.HEADER_STRING);
		String[] array = header.split(" ");
		return array.length == 2 ? array[1] : null;
	}

	private void setAuthenticationContext(UserDetails userDetails, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authToken);
	}
}
