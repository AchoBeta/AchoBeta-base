package com.achobeta.www.oauth.config;

import com.achobeta.www.oauth.config.auth.*;
import com.achobeta.www.oauth.config.handler.login.AuthenticationFailureHandler;
import com.achobeta.www.oauth.config.handler.login.AuthenticationSuccessHandler;
import com.achobeta.www.oauth.config.handler.logout.AuthenticationLogoutHandler;
import com.achobeta.www.oauth.config.handler.logout.AuthenticationLogoutSuccessHandler;
import com.achobeta.www.oauth.config.auth.manager.AuthenticationUsernameManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.DelegatingReactiveAuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import java.util.LinkedList;
import java.util.List;

import static org.springframework.security.authorization.AuthorityReactiveAuthorizationManager.hasRole;

/**
 * <span>
 * security config
 * </span>
 *
 * @author jettcc in 2023/10/18
 * @version 1.0
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class AchoBetaWebSecurityConfig {

    private final AuthenticationWhitelistConfig whitelistConfig;
    private final AuthenticationFailureHandler authenticationFailureHandler;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationLogoutHandler authenticationLogoutHandler;
    private final AuthenticationLogoutSuccessHandler authenticationLogoutSuccessHandler;
    private final AuthenticationUsernameManager authenticationUsernameManager;
    private final AuthenticationLoginConverter authenticationLoginConverter;
    private final AuthenticationContextRepository authenticationContextRepository;
    private final AuthorizationContextManager authorizationContextManager;
    private final AuthenticationAccessDeniedHandler authenticationAccessDeniedHandler;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * constant
     */
    private static final String USER_SECURITY_FILTER_LOGIN_URL = "/api/v1/auth/login";

    @Bean
    @Order(1)
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        String[] urls = whitelistConfig.getUrls().toArray(new String[0]);
        http
                .authorizeExchange((authorize) -> authorize
                        // 白名单路径
                        .pathMatchers(urls)
                        .permitAll()
                        .pathMatchers("/admin/**")
                        .hasRole("ADMIN")
                        .pathMatchers("/db/**")
                        .access((authentication, context) ->
                                hasRole("ADMIN").check(authentication, context)
                                        .filter(decision -> !decision.isGranted())
                                        .switchIfEmpty(hasRole("DBA").check(authentication, context))
                        )
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().access(authorizationContextManager)
                )
                .exceptionHandling(spec -> spec
                        .accessDeniedHandler(authenticationAccessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint))
                .securityMatcher(ServerWebExchangeMatchers.anyExchange())
                .securityContextRepository(authenticationContextRepository)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(spec -> spec
                        .logoutHandler(authenticationLogoutHandler)
                        .logoutSuccessHandler(authenticationLogoutSuccessHandler))
                .addFilterAt(authenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
        ;
        return http.build();
    }

    private AuthenticationWebFilter authenticationWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(reactiveAuthenticationManager());
        filter.setSecurityContextRepository(authenticationContextRepository);
        filter.setServerAuthenticationConverter(authenticationLoginConverter);
        filter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        filter.setRequiresAuthenticationMatcher(
                ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/login")
        );

        return filter;
    }

    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager() {
        List<ReactiveAuthenticationManager> managers = new LinkedList<>() {{
            add(authenticationUsernameManager);
        }};
        return new DelegatingReactiveAuthenticationManager(managers);
    }
}

