package com.example.demo.security.config;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.debug.DebugFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.web.filter.CompositeFilter;

import com.example.demo.services.CustomUserDetailsService;
import com.example.demo.services.TokenCookieService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.jwk.OctetSequenceKey;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
	private final CustomUserDetailsService userDetailsService;
	
	@Bean
	TokenCookieAuthenticationStrategy tokenCookieAuthenticationStrategy(
			@Value("${COOKIE_TOKEN_KEY}") String cookieTokenKey
			) throws ParseException, JOSEException {
		return new TokenCookieAuthenticationStrategy(
				new TokenCookieService(
						new DirectEncrypter(OctetSequenceKey.parse(cookieTokenKey)),
						new DirectDecrypter(OctetSequenceKey.parse(cookieTokenKey))));
	}
	
	@Bean
	TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer(
			@Value("${COOKIE_TOKEN_KEY}") String cookieTokenKey
			) throws ParseException, JOSEException {
		return new TokenCookieAuthenticationConfigurer(
				new TokenCookieService(
						new DirectEncrypter(OctetSequenceKey.parse(cookieTokenKey)),
						new DirectDecrypter(OctetSequenceKey.parse(cookieTokenKey))));
	}
	
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
    		TokenCookieAuthenticationStrategy tokenCookieAuthenticationStrategy,
    		TokenCookieAuthenticationConfigurer tokenCookieAuthenticationConfigurer) throws Exception {
        http
        .httpBasic(Customizer.withDefaults())
        .with(tokenCookieAuthenticationConfigurer, Customizer.withDefaults())
        .csrf(csrf -> csrf.ignoringRequestMatchers("/api/user/registration")
        		.csrfTokenRepository(new CookieCsrfTokenRepository())
        		.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        		.sessionAuthenticationStrategy((authentication, request, response) ->{}))
            .authorizeHttpRequests(authorize -> authorize
            	.requestMatchers("/api/user/registration", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(sessionManagment -> sessionManagment
            		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            		.sessionAuthenticationStrategy(tokenCookieAuthenticationStrategy))
            .logout(LogoutConfigurer::permitAll);
        
        return http.build();
    }
    
    @Bean
	AuthenticationManager authenticationManager(
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(this.userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authenticationProvider);
	}
    
    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(8);
    }
    
    
    //TODO Удалить 
    //Для дебаг мода
    @Bean
    static BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor() {
        return registry -> registry.getBeanDefinition(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME).setBeanClassName(CompositeFilterChainProxy.class.getName());
    }

static class CompositeFilterChainProxy extends FilterChainProxy {

        private final Filter doFilterDelegate;

        private final FilterChainProxy springSecurityFilterChain;

        CompositeFilterChainProxy(List<? extends Filter> filters) {
            this.doFilterDelegate = createDoFilterDelegate(filters);
            this.springSecurityFilterChain = findFilterChainProxy(filters);
        }

        @Override
        public void afterPropertiesSet() {
            this.springSecurityFilterChain.afterPropertiesSet();
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            this.doFilterDelegate.doFilter(request, response, chain);
        }

        @Override
        public List<Filter> getFilters(String url) {
            return this.springSecurityFilterChain.getFilters(url);
        }

        @Override
        public List<SecurityFilterChain> getFilterChains() {
            return this.springSecurityFilterChain.getFilterChains();
        }

        @Override
        public void setSecurityContextHolderStrategy(SecurityContextHolderStrategy securityContextHolderStrategy) {
            this.springSecurityFilterChain.setSecurityContextHolderStrategy(securityContextHolderStrategy);
        }

        @Override
        public void setFilterChainValidator(FilterChainValidator filterChainValidator) {
            this.springSecurityFilterChain.setFilterChainValidator(filterChainValidator);
        }

        @Override
        public void setFilterChainDecorator(FilterChainDecorator filterChainDecorator) {
            this.springSecurityFilterChain.setFilterChainDecorator(filterChainDecorator);
        }

        @Override
        public void setFirewall(HttpFirewall firewall) {
            this.springSecurityFilterChain.setFirewall(firewall);
        }

        @Override
        public void setRequestRejectedHandler(RequestRejectedHandler requestRejectedHandler) {
            this.springSecurityFilterChain.setRequestRejectedHandler(requestRejectedHandler);
        }

        private static Filter createDoFilterDelegate(List<? extends Filter> filters) {
            CompositeFilter delegate = new CompositeFilter();
            delegate.setFilters(filters);
            return delegate;
        }

        private static FilterChainProxy findFilterChainProxy(List<? extends Filter> filters) {
            for (Filter filter : filters) {
                if (filter instanceof FilterChainProxy fcp ) {
                    return fcp;
                }
                if (filter instanceof DebugFilter debugFilter ) {
                    return debugFilter.getFilterChainProxy();
                }
            }
            throw new IllegalStateException("Couldn't find FilterChainProxy in " + filters);
        }

    }

}
