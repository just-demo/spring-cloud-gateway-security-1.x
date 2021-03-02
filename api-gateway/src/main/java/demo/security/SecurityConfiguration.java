package demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@Configuration
@EnableOAuth2Sso
@EnableResourceServer
@Order(value = 0)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private ResourceServerTokenServices resourceServerTokenServices;

    @Bean
    @Primary
    public OAuth2ClientContextFilter oauth2ClientContextFilterWithPath() {
        return new Oauth2ClientContextFilterWithPath();
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/authorization-server/**", "/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterAfter(
                        oAuth2AuthenticationProcessingFilter(),
                        AbstractPreAuthenticatedProcessingFilter.class)
                .logout().permitAll().logoutSuccessUrl("/");
    }

    private OAuth2AuthenticationProcessingFilter oAuth2AuthenticationProcessingFilter() {
        OAuth2AuthenticationProcessingFilter filter = new OAuth2AuthenticationProcessingFilter();
        filter.setAuthenticationManager(oauthAuthenticationManager());
        filter.setStateless(false);
        return filter;
    }

    private AuthenticationManager oauthAuthenticationManager() {
        OAuth2AuthenticationManager manager = new OAuth2AuthenticationManager();
        manager.setResourceId("apigateway"); // TODO: change it???
        manager.setTokenServices(resourceServerTokenServices);
        manager.setClientDetailsService(null);
        return manager;
    }
}
