package com.spring_boot.ecommerce.trainSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

import static org.springframework.security.config.Customizer.withDefaults;

//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private DataSource dataSource;
//
//    @Autowired
//    private AuthEntryPointJwt authEntryPointJwt;
//
//    @Bean
//    public AuthTokenFilter authTokenFilter(){
//        return new AuthTokenFilter();
//    }
//
//    @Bean
//    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests((requests) ->
//                requests.requestMatchers("/signin").permitAll()
//                .anyRequest().authenticated()
//        );
//        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
////        http.formLogin(withDefaults());
////        http.httpBasic(withDefaults());
//        http.exceptionHandling(
//                e -> e.authenticationEntryPoint(authEntryPointJwt)
//        );
//
//        http.addFilterBefore(
//                authTokenFilter(),
//                UsernamePasswordAuthenticationFilter.class
//                );
//        return http.build();
//    }
//
//    @Bean
//    public UserDetailsService userDetailsService(){
//
//        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
//
//        // Check if user exists before creating
//        /*
//
//        if (!jdbcUserDetailsManager.userExists("user1")) {
//            UserDetails user1 = User.withDefaultPasswordEncoder()
//                    .username("user1")
//                    .password("password123")
//                    .roles("USER")
//                    .build();
//            jdbcUserDetailsManager.createUser(user1);
//        }
//
//        if (!jdbcUserDetailsManager.userExists("admin")) {
//            UserDetails admin = User.withDefaultPasswordEncoder()
//                    .username("admin")
//                    .password("admin123")
//                    .roles("ADMIN", "USER")
//                    .build();
//            jdbcUserDetailsManager.createUser(admin);
//        }
//         */
//
//        return jdbcUserDetailsManager;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
//        return authConfig.getAuthenticationManager();
//    }
//
//}
