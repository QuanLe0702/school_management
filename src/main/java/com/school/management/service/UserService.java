package com.school.management.service;

import java.util.List;

import com.school.management.dto.LoginRequest;
import com.school.management.dto.UserDto;
import com.school.management.model.User;

public interface UserService {
    UserDto signup(UserDto user);

    UserDto login(LoginRequest loginDto);

    UserDto findUserByEmail(String email);

    Boolean checkUserExistByEmail(String email);

    List<User> getAllUser();

    UserDto updateResetPasswordToken(String token, String password);

    User getByResetPasswordToken(String token);
    // Long generateAccount();

    String deleteAccount(String email);

    String changePassword(Long uid,String oldPassword,String newPassword);
}
