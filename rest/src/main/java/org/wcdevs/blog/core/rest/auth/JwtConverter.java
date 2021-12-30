package org.wcdevs.blog.core.rest.auth;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Jwt custom token converter for this app.
 */
public interface JwtConverter extends Converter<Jwt, AbstractAuthenticationToken> {
}
