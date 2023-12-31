package com.school.management.utils;

import static com.school.management.config.SecurityConstants.SECRET;
import static com.school.management.config.SecurityConstants.TOKEN_EXPIRATION_TIME;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.school.management.dto.AccessTokenDto;
import com.school.management.model.Student;
import com.school.management.model.Teacher;
import com.school.management.model.User;
import com.school.management.repository.StudentRepository;
import com.school.management.repository.TeacherRepository;
import com.school.management.repository.UserRepository;
import com.school.management.service.RefreshTokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {

    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    public AccessTokenDto generateToken(Authentication auth) {

        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) auth
                .getPrincipal();
        String login = user.getUsername();

        Claims claims = Jwts.claims().setSubject(login);
        List<String> roles = new ArrayList<>();
        user.getAuthorities().stream().forEach(authority -> roles.add(authority.getAuthority()));
        claims.put("roles", roles);
        String token = Jwts.builder().setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();
        User userlogin = userRepository.findByEmail(login).get();
        Long uid = userlogin.getId();
        Long id = null;
        Student student = studentRepository.findByUser(userlogin);
        Teacher teacher = teacherRepository.findByUser(userlogin);

        AccessTokenDto accessToken = new AccessTokenDto();
        accessToken.setRoles(roles);
        accessToken.setToken(token);
        accessToken.setUid(uid);
         if (student != null) {
            id = student.getId();
        }
        if (teacher != null) {
            id = teacher.getId();
        }
        accessToken.setId(id);
        accessToken.setUsername(user.getUsername());
        accessToken.setRefreshToken(refreshTokenService.createRefreshToken(login).getToken());
        return accessToken;

    }

    public AccessTokenDto generateTokenFromEmail(String email) {

        User user = userRepository.findByEmail(email).get();
        Claims claims = Jwts.claims().setSubject(email);
        List<String> roles = new ArrayList<>();
        roles.add(user.getRole().getRole().toString());
        claims.put("roles", roles);
        String token = Jwts.builder().setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET).compact();

        AccessTokenDto accessToken = new AccessTokenDto();
        accessToken.setRoles(roles);
        accessToken.setToken(token);
        accessToken.setUsername(user.getEmail());
        accessToken.setRefreshToken(refreshTokenService.createRefreshToken(email).getToken());
        return accessToken;

    }
}