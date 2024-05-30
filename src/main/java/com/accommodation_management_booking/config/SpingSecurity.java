package com.accommodation_management_booking.config;

import com.accommodation_management_booking.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
}
