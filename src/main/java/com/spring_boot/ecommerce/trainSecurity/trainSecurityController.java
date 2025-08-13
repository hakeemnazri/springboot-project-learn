package com.spring_boot.ecommerce.trainSecurity;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
public class trainSecurityController {

    private AuthenticationManager authenticationManager;

    private JwtUtils jwtUtils;

    public trainSecurityController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this. jwtUtils = jwtUtils;
    }

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello";
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String user(){
        return "User";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String admin(){
        return "Admin";
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(
            @RequestBody LoginRequest loginRequest
    ){
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

        }catch (AuthenticationException e){
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad Credentials");
            map.put("status", false);
            return new ResponseEntity<Map<String, Object>>(map, HttpStatus.NOT_FOUND);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateTokenFromUsername(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(authority -> authority.getAuthority()).collect(Collectors.toList());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setJwtToken(jwt);
        loginResponse.setRoles(roles);
        loginResponse.setUsername(userDetails.getUsername());

        return ResponseEntity.ok(loginResponse);
    }

}
