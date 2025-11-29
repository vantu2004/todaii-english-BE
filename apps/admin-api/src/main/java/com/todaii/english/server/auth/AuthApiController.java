package com.todaii.english.server.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.server.admin.AdminService;
import com.todaii.english.server.security.AdminTokenService;
import com.todaii.english.server.security.CustomAdminDetails;
import com.todaii.english.shared.request.AuthRequest;
import com.todaii.english.shared.request.RefreshTokenRequest;
import com.todaii.english.shared.response.AuthResponse;
import com.todaii.english.shared.utils.CookieUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/auth")
@Tag(
    name = "Authentication",
    description = "APIs for admin login, token refresh, and logout"
)
public class AuthApiController {

    private static final String USER_TYPE = "admin";

    private final AuthenticationManager authenticationManager;
    private final AdminTokenService adminTokenService;
    private final AdminService adminService;

    @PostMapping("/login")
    @Operation(
        summary = "Admin Login",
        description = "Authenticate admin using email and password, returning both access and refresh tokens",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = AuthRequest.class),
                examples = @ExampleObject(value = """
                    {
                      "email": "admin@gmail.com",
                      "password": "123456"
                    }
                """)
            )
        )
    )
    @ApiResponse(
        responseCode = "200",
        description = "Login successful",
        content = @Content(
            schema = @Schema(implementation = AuthResponse.class),
            examples = @ExampleObject(value = """
                {
                  "accessToken": "ACCESS_TOKEN_HERE",
                  "refreshToken": "REFRESH_TOKEN_HERE"
                }
            """)
        )
    )
    @ApiResponse(responseCode = "401", description = "Invalid email or password")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {

        String email = authRequest.getEmail();
        String password = authRequest.getPassword();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(email, password);

        Authentication result = authenticationManager.authenticate(authentication);

        CustomAdminDetails customAdminDetails = (CustomAdminDetails) result.getPrincipal();
        AuthResponse authResponse =
                this.adminTokenService.generateToken(customAdminDetails.getAdmin());

        this.adminService.updateLastLogin(email);

        ResponseCookie accessTokenCookie =
                CookieUtils.createAccessTokenCookie(authResponse.getAccessToken(), USER_TYPE);

        ResponseCookie refreshTokenCookie =
                CookieUtils.createRefreshTokenCookie(authResponse.getRefreshToken(), USER_TYPE);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(authResponse);
    }

    @PostMapping("/new-token")
    @Operation(
        summary = "Refresh access token",
        description = "Receive a refresh token and return a new pair of tokens",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                schema = @Schema(implementation = RefreshTokenRequest.class),
                examples = @ExampleObject(value = """
                    {
                      "refreshToken": "REFRESH_TOKEN_HERE"
                    }
                """)
            )
        )
    )
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        AuthResponse authResponse = this.adminTokenService.refreshTokens(refreshTokenRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Admin Logout",
        description = "Remove refresh token from database and clear cookies storing tokens",
        parameters = {
            @io.swagger.v3.oas.annotations.Parameter(
                name = "email",
                description = "Email of the admin to logout",
                example = "admin@gmail.com"
            )
        }
    )
    @ApiResponse(responseCode = "200", description = "Logout successful")
    public ResponseEntity<Void> logout(
            @RequestParam @Email(message = "Email format is invalid") String email,
            @CookieValue(name = "admin_refresh_token", required = false) String refreshToken) {

        this.adminTokenService.revokeRefreshToken(email, refreshToken);

        ResponseCookie removedAccessToken = CookieUtils.removeCookie("admin_access_token");
        ResponseCookie removedRefreshToken = CookieUtils.removeCookie("admin_refresh_token");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, removedAccessToken.toString())
                .header(HttpHeaders.SET_COOKIE, removedRefreshToken.toString())
                .build();
    }

}
