package com.todaii.english.infra.security.jwt;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

/*
 * interface có tác dụng tách logic build UserDetails từ Claims tuỳ theo
 * actorType (ADMIN / USER)
 */
public interface JwtUserDetailsFactory {
  UserDetails build(Claims claims);
}
