package org.wcdevs.blog.core.rest.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtAuthTokenConverter extends Converter<Jwt, AbstractAuthenticationToken> {
}
