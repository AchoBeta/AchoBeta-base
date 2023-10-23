package com.achobeta.www.oauth.config.handler.logout;

import com.achobeta.www.common.util.GlobalServiceStatusCode;
import com.achobeta.www.oauth.exception.LogoutRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Optional;
import java.util.function.Function;

/**
 * <span>
 * authentication logout handler, logoutHandler - > logout success
 * </span>
 *
 * @author jettcc in 2023/10/23
 * @version 1.0
 */
@Configuration
@Slf4j
public class AuthenticationLogoutHandler implements ServerLogoutHandler {
    @Override
    public Mono<Void> logout(WebFilterExchange exchange, Authentication authentication) {
        log.info("logout handler");
        return exchange.getExchange().getSession().flatMap(
                webSession -> {
                    // cancel authentication
                    SecurityContext context = SecurityContextHolder.getContext();
                    context.setAuthentication(null);
                    // clear context
                    SecurityContextHolder.clearContext();
                    return webSession.invalidate();
                }
        )
                .contextWrite(ReactiveSecurityContextHolder.clearContext());
    }
}