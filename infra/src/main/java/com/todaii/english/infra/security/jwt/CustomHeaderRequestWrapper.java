package com.todaii.english.infra.security.jwt;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class CustomHeaderRequestWrapper extends HttpServletRequestWrapper {
	private final Map<String, String> customHeaders = new HashMap<>();

	public CustomHeaderRequestWrapper(HttpServletRequest request, String name, String value) {
		super(request);
		customHeaders.put(name, value);
	}

	@Override
	public String getHeader(String name) {
		return customHeaders.getOrDefault(name, super.getHeader(name));
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		Set<String> names = new HashSet<>(customHeaders.keySet());
		Enumeration<String> original = super.getHeaderNames();
		while (original.hasMoreElements()) {
			names.add(original.nextElement());
		}
		return Collections.enumeration(names);
	}
}
