package com.todaii.english.client;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;

import com.todaii.english.client.security.CustomUserDetails;

public class UserUtils {
  private static final Parser PARSER = Parser.builder().build();
  private static final HtmlRenderer RENDERER = HtmlRenderer.builder().build();

  public static Long getCurrentUserId(Authentication authentication) {
    if (authentication == null) {
      throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
    }

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    return customUserDetails.getUser().getId();
  }

  public static String toHtml(String markdown) {
    if (markdown == null || markdown.isBlank()) {
      return "";
    }
    Node document = PARSER.parse(markdown);

    return RENDERER.render(document);
  }
}
