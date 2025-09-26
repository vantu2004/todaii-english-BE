package com.todaii.english.infra.security.jwt;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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

import com.todaii.english.core.admin.admin.Admin;
import com.todaii.english.core.admin.admin.AdminRole;
import com.todaii.english.core.admin.admin.AdminRoleRepository;
import com.todaii.english.infra.security.admin.CustomAdminDetails;
import com.todaii.english.shared.constants.SecurityConstants;
import com.todaii.english.shared.enums.error_code.AdminErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

//kế thừa OncePerRequestFilter để đảm bảo mỗi request chỉ xác thực qua filter 1 lần
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
	private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

	private final JwtUtility jwtUtility;
	private final AdminRoleRepository adminRoleRepository;

	// chỉ rõ Spring nên dùng bean có tên là handlerExceptionResolver
	@Qualifier("handlerExceptionResolver")
	/*
	 * HandlerExceptionResolver là một interface giúp xử lý ngoại lệ và chuyển đổi
	 * thành HTTP response tương ứng.
	 */
	private final HandlerExceptionResolver handlerExceptionResolver;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// dùng để debug xem luồng khi test add student
		// Authentication authentication =
		// SecurityContextHolder.getContext().getAuthentication();

		if (!this.hasAuthorizationBearer(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = this.getBearerToken(request);
		LOGGER.info("Token: " + token);

		try {
			Claims claims = this.jwtUtility.validateAccessToken(token);

			UserDetails userDetails = this.getUserDetails(claims);
			this.setAuthenticationContext(userDetails, request);

			filterChain.doFilter(request, response);

			this.clearAuthenticationContext();
		} catch (JwtValidationException e) {
			LOGGER.error(e.getMessage(), e);

			handlerExceptionResolver.resolveException(request, response, null, e);
		}
	}

	// kiểm tra có token trong header ko
	private boolean hasAuthorizationBearer(HttpServletRequest request) {
		String header = request.getHeader(SecurityConstants.HEADER_STRING);
		LOGGER.info("Authorization header: " + header);

		if (ObjectUtils.isEmpty(header) || !header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			return false;
		}
		return true;
	}

	// lấy token
	private String getBearerToken(HttpServletRequest request) {
		String header = request.getHeader(SecurityConstants.HEADER_STRING);
		String[] array = header.split(" ");
		if (array.length == 2) {
			return array[1];
		}

		return null;
	}

	private UserDetails getUserDetails(Claims claims) {
		String subject = claims.getSubject();
		String[] array = subject.split(",");

		Long id = Long.parseLong(array[0]);
		String displayName = array[1];

		// lấy roles từ claim "roles"
		String rolesClaim = claims.get("roles", String.class);
		Set<AdminRole> roles = new HashSet<>();
		if (rolesClaim != null && !rolesClaim.isBlank()) {
			for (String roleCode : rolesClaim.split(",")) {
				AdminRole adminRole = this.adminRoleRepository.findById(roleCode)
						.orElseThrow(() -> new BusinessException(AdminErrorCode.ROLE_NOT_FOUND));
				roles.add(adminRole);
			}
		}

		Admin admin = new Admin();
		admin.setId(id);
		admin.setDisplayName(displayName);
		admin.setRoles(roles);

		LOGGER.info("User parsed from JWT: {}, {}, {}", admin.getId(), admin.getDisplayName(),
				admin.getRoles().stream().map(AdminRole::getCode).toList());

		return new CustomAdminDetails(admin);
	}

	// báo cho Spring Security hiểu rằng user là login và có quyền truy cập tiếp
	private void setAuthenticationContext(UserDetails userDetails, HttpServletRequest request) {
		// Tạo một đối tượng xác thực (authentication token) với:
		// - Principal: userDetails (thông tin người dùng đã xác thực)
		// - Credentials: null (vì không cần mật khẩu ở giai đoạn này)
		// - Authorities: danh sách quyền (roles) của người dùng
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		/*
		 * Gắn thông tin chi tiết của request vào token (ví dụ: địa chỉ IP, session
		 * ID,...) Điều này giúp tăng tính bảo mật và kiểm tra thêm thông tin người dùng
		 */
		authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		/*
		 * Đưa token vào SecurityContextHolder — đây là nơi Spring Security lưu trữ
		 * thông tin xác thực của người dùng cho toàn bộ request hiện tại
		 */
		SecurityContextHolder.getContext().setAuthentication(authToken);

	}

	// sau khi user truy cập xog thì cho logout ngay lập tức
	private void clearAuthenticationContext() {
		/*
		 * Xóa thông tin xác thực khỏi SecurityContextHolder → người dùng không còn được
		 * xem là "đã đăng nhập"
		 */
		SecurityContextHolder.clearContext();

	}

}
