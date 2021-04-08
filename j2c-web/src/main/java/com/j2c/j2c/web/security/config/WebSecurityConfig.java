package com.j2c.j2c.web.security.config;

import com.j2c.j2c.web.security.filter.dao.DaoAuthenticationFilter;
import com.j2c.j2c.web.security.filter.jwt.JWTAccessDeniedHandler;
import com.j2c.j2c.web.security.filter.jwt.JWTAuthenticationEntryPoint;
import com.j2c.j2c.web.security.filter.jwt.JWTAuthenticationFilter;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler;
import org.springframework.security.web.firewall.RequestRejectedHandler;

import javax.servlet.Filter;

@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public WebSecurityConfig() {
        super(true);
    }

    @Bean
    public java.security.Key secretKey() {
        final String secret = getEnvironment().getProperty("j2c.web.security.jwt.secret");
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("Missing or invalid JWT secret");
        }
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Bean
    public FilterRegistrationBean<DaoAuthenticationFilter> disableDaoAuthenticationFilterAutoConfiguration(final DaoAuthenticationFilter filter) {
        return disableFilterAutoConfiguration(filter);
    }

    @Bean
    public FilterRegistrationBean<JWTAuthenticationFilter> disableJWTAuthenticationFilterAutoConfiguration(final JWTAuthenticationFilter filter) {
        return disableFilterAutoConfiguration(filter);
    }

    @Bean
    public RequestRejectedHandler requestRejectedHandler() {
        return new HttpStatusRequestRejectedHandler();
    }

    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(getBean(DaoAuthenticationUserResolver.class));
        authenticationProvider.setPasswordEncoder(getBean(PasswordEncoder.class));
        return authenticationProvider;
    }

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring()
                .antMatchers("/error");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .headers().and()
                .anonymous().and()
                .addFilterAt(getBean(DaoAuthenticationFilter.class), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(getBean(JWTAuthenticationFilter.class), DaoAuthenticationFilter.class)
                .exceptionHandling(
                        config -> config
                                .authenticationEntryPoint(getBean(JWTAuthenticationEntryPoint.class))
                                .accessDeniedHandler(getBean(JWTAccessDeniedHandler.class))
                );
    }

    private Environment getEnvironment() {
        return getApplicationContext().getEnvironment();
    }

    private <T> T getBean(final Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    private <T extends Filter> FilterRegistrationBean<T> disableFilterAutoConfiguration(final T filter) {
        final FilterRegistrationBean<T> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setEnabled(false);
        return registrationBean;
    }

}
