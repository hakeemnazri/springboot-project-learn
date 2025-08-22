package com.spring_boot.ecommerce.controller;


import com.spring_boot.ecommerce.model.AppRole;
import com.spring_boot.ecommerce.model.Role;
import com.spring_boot.ecommerce.model.User;
import com.spring_boot.ecommerce.repositories.RoleRepository;
import com.spring_boot.ecommerce.security.jwt.services.UserDetailsImpl;
import com.spring_boot.ecommerce.repositories.UserRepository;
import com.spring_boot.ecommerce.security.payload.LoginRequest;
import com.spring_boot.ecommerce.security.payload.ManageResponse;
import com.spring_boot.ecommerce.security.payload.SignupRequest;
import com.spring_boot.ecommerce.security.payload.UserInfoResponse;
import com.spring_boot.ecommerce.security.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

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
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(authority -> authority.getAuthority()).collect(Collectors.toList());

        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setId(userDetails.getId());
        userInfoResponse.setRoles(roles);
        userInfoResponse.setUsername(userDetails.getUsername());

        return ResponseEntity.ok()
                .header(
                HttpHeaders.SET_COOKIE,
                jwtCookie.toString())
                .body(userInfoResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody SignupRequest signupRequest
    ){
        if(userRepository.existsByUserName(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body(new ManageResponse("Error: Username is already taken!"));
        }

        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body(new ManageResponse("Error: Email is already taken!"));
        }

        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword())
        );

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if(strRoles == null){
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found!"));
            roles.add(userRole);
        }else{
            for (String role: strRoles){
                if(role.equals("admin")){
                    Role admin = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                    roles.add(admin);

                    continue;
                }

                if(role.equals("seller")){
                    Role seller = roleRepository.findByRoleName(AppRole.ROLE_SELLER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));

                    roles.add(seller);

                    continue;
                }

                Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                roles.add(userRole);
            }
        }

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new ManageResponse("User registered"));
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication){
        if(authentication != null){
            return authentication.getName();
        }else {
            return "";
        }
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> getUserDetails(Authentication authentication){
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream().map(role -> role.getAuthority()).toList();

        UserInfoResponse userInfoResponse = new UserInfoResponse();
        userInfoResponse.setId(userDetails.getId());
        userInfoResponse.setRoles(roles);
        userInfoResponse.setUsername(userDetails.getUsername());

        return new ResponseEntity<UserInfoResponse>(userInfoResponse, HttpStatus.OK);
    }

    @PostMapping("/signout")
    public ResponseEntity<String> signoutUser(){

        ResponseCookie cleanJwtCookie = jwtUtils.getCleanJwtCookie();

        return ResponseEntity.ok()
                .header(
                HttpHeaders.SET_COOKIE,
                cleanJwtCookie.toString()
                )
                .body("Successfully signed out");
    }


}
