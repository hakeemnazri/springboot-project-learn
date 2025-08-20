package com.spring_boot.ecommerce.security;


import com.spring_boot.ecommerce.model.AppRole;
import com.spring_boot.ecommerce.model.Role;
import com.spring_boot.ecommerce.model.User;
import com.spring_boot.ecommerce.repositories.RoleRepository;
import com.spring_boot.ecommerce.repositories.UserRepository;
import com.spring_boot.ecommerce.security.jwt.services.UserDetailsServiceImpl;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Set;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    //init data DI
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;
    // //////////////////

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(){
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests((requests) ->
                requests
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
//                        .requestMatchers("/api/public/**").permitAll()
//                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .anyRequest().authenticated()
        )
        .exceptionHandling(
                e -> e.authenticationEntryPoint(unauthorizedHandler)
        );
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(
                authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class
        );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager( AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    //completely bypass security filter at global level
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return (web -> web.ignoring().requestMatchers(
                "v2/api-docs",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**"
                ));
    }

    //create users and roles
    @Bean
    @Transactional
    public CommandLineRunner initData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
            ){
        return args -> {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseGet(() -> {
                        Role newRoleUser = new Role(AppRole.ROLE_USER);
                        roleRepository.save(newRoleUser);
                        return newRoleUser;
                    }
            );

            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElseGet(() -> {
                Role newRoleSeller = new Role(AppRole.ROLE_SELLER);
                roleRepository.save(newRoleSeller);
                return newRoleSeller;
            });

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseGet(() -> {
                Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                roleRepository.save(newAdminRole);
                return newAdminRole;
            });

            Set<Role> userRoleSet = Set.of(userRole);
            Set<Role> sellerRoleSet = Set.of(userRole, sellerRole);
            Set<Role> adminRoleSet = Set.of(userRole, sellerRole, adminRole);

            if(!userRepository.existsByUserName("user")){
                User newUser = new User("user", "user@gmail.com", passwordEncoder.encode("userpassword"));
                userRepository.save(newUser);
            }

            if(!userRepository.existsByUserName("seller")){
                User newSeller = new User("seller", "seller@gmail.com", passwordEncoder.encode("sellerpassword"));
                userRepository.save(newSeller);
            }

            if(!userRepository.existsByUserName("admin")){
                User newAdmin = new User("admin", "admin@gmail.com", passwordEncoder.encode("adminpassword"));
                userRepository.save(newAdmin);
            }

            userRepository.findByUserName("user").ifPresent(user -> {
                    user.setRoles(userRoleSet);
                    userRepository.save(user);
            });
            userRepository.findByUserName("seller").ifPresent(user -> {
                    user.setRoles(sellerRoleSet);
                    userRepository.save(user);
            });
            userRepository.findByUserName("admin").ifPresent(user -> {
                    user.setRoles(adminRoleSet);
                    userRepository.save(user);
            });
        };

    }
}
