package com.accommodation_management_booking.config;

import com.accommodation_management_booking.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SpingSecurity {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomOAuth2UserService customOAuth2UserService) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/static/**").permitAll()
                .requestMatchers("/fpt-dorm/register/**").permitAll()
                .requestMatchers("/fpt-dorm/forgot-password").permitAll()
                .requestMatchers("/fpt-dorm/reset-password").permitAll()
                .requestMatchers("/fpt-dorm/home").permitAll()
//                .requestMatchers("/fpt-dorm/home-user/**").permitAll()
                .requestMatchers("/fpt-dorm/home/**").permitAll()
                .requestMatchers("/fpt-dorm").permitAll()
                .requestMatchers("/fpt-dorm/admin/**").authenticated()
                .requestMatchers("/fpt-dorm/profile/complete").authenticated()
                .anyRequest().authenticated()
                .and()
                .formLogin(form -> form
                        .loginPage("/fpt-dorm/login")
                        .loginProcessingUrl("/fpt-dorm/login")
                        .defaultSuccessUrl("/fpt-dorm/home-user")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/fpt-dorm/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .defaultSuccessUrl("/fpt-dorm/profile/complete", true)
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/fpt-dorm/logout"))
                        .permitAll()
                );
        return http.build();
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }


    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User oauth2User = delegate.loadUser(request);
            return new DefaultOAuth2User(
                    oauth2User.getAuthorities(),
                    oauth2User.getAttributes(),
                    "email"); // Use "email" as the key for the email attribute
        };
    }

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withDefaultPasswordEncoder()
                .username("user@example.com")
                .password("password")
                .roles("USER")
                .build());
        manager.createUser(User.withDefaultPasswordEncoder()
                .username("employee@example.com")
                .password("password")
                .roles("EMPLOYEE")
                .build());

        manager.createUser(User.withDefaultPasswordEncoder()
                .username("admin@example.com")
                .password("password")
                .roles("ADMIN")
                .build());
        return manager;
    }
}
