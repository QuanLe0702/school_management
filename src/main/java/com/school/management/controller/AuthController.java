package com.school.management.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.management.model.ERole;
import com.school.management.model.Role;
import com.school.management.model.User;
import com.school.management.payload.request.LoginRequest;
import com.school.management.payload.request.SignupRequest;
import com.school.management.payload.response.JwtResponse;
import com.school.management.payload.response.MessageResponse;
import com.school.management.repository.RoleRepository;
import com.school.management.repository.UserRepository;
import com.school.management.security.jwt.JwtUtils;
import com.school.management.security.service.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);
    String jwt = jwtUtils.generateJwtToken(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    return ResponseEntity.ok(new JwtResponse(jwt,
        userDetails.getId(),
        userDetails.getUsername(),
        userDetails.getEmail(),
        roles));
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
        signUpRequest.getEmail(),
        "active",
        encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role studentRole = roleRepository.findByName(ERole.STUDENT)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(studentRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleRepository.findByName(ERole.ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);
            break;

          case "parent":
            Role parentRole = roleRepository.findByName(ERole.PARENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(parentRole);
            break;

          case "teacher":
            Role teacherRole = roleRepository.findByName(ERole.TEACHER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(teacherRole);
            break;

          case "school":
            Role schoolRole = roleRepository.findByName(ERole.SCHOOl)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(schoolRole);
            break;

          default:
            Role studentRole = roleRepository.findByName(ERole.STUDENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(studentRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);
    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }
}
