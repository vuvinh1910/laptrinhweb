package com.example.web_nhom_5.service.implement;


import com.example.web_nhom_5.conventer.UserMapper;
import com.example.web_nhom_5.dto.request.*;
import com.example.web_nhom_5.dto.response.UserResponse;
import com.example.web_nhom_5.entity.ForgotPassword;
import com.example.web_nhom_5.entity.Role;
import com.example.web_nhom_5.entity.UserEntity;
import com.example.web_nhom_5.exception.ErrorCode;
import com.example.web_nhom_5.exception.WebException;
import com.example.web_nhom_5.repository.ForgotPasswordRepository;
import com.example.web_nhom_5.repository.RoleRepository;
import com.example.web_nhom_5.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Transactional
public class UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    UserMapper userMapper;
    ForgotPasswordRepository forgotPasswordRepository;

    public UserResponse creatUser(UserCreationRequest request) {
        if(userRepository.existsByUserName(request.getUserName())) {
            throw new WebException(ErrorCode.USER_EXISTED);
        }

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new WebException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        UserEntity user = userMapper.toUser(request);


        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // tự động set role cho tài khoản mới tạo là USER
        var userRole = roleRepository.findById("USER")
                .orElseThrow(() -> new WebException(ErrorCode.SERVICE_EXISTED));

        // Gán role cho user
        user.setRoles(Set.of(userRole));

        return userMapper.toUserResponse(userRepository.save(user));
    }


    public UserResponse getMyInfo(){
        var contex = SecurityContextHolder.getContext();
        String name = contex.getAuthentication().getName();
        UserEntity user = userRepository.findByUserName(name).orElseThrow(()
                -> new WebException(ErrorCode.USER_NOT_EXISTED) );
        return userMapper.toUserResponse(user);
    }


    public List<UserResponse> getAllUser() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }



    public UserResponse getUser(Long userid) {
        return userMapper.toUserResponse(userRepository.findById(userid).orElseThrow(() -> new RuntimeException("User not found")));
    }


    public UserResponse updateUserProfile(UserProfileUpdateRequest request) {

        var contex = SecurityContextHolder.getContext();
        String name = contex.getAuthentication().getName();
        UserEntity user = userRepository.findByUserName(name).orElseThrow(()
                -> new WebException(ErrorCode.USER_NOT_EXISTED) );
        userMapper.updateProfile(user ,request);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse updateUserByAdmin(Long userid, UserUpdateRequest request) {
        UserEntity user = userRepository.findById(userid).orElseThrow(() -> new RuntimeException("User not found"));

        userMapper.updateUser(user ,request);
        Set<Role> sett = new HashSet<>();
        for(String i : request.getRoles()) {
            sett.add(roleRepository.findById(i).orElseThrow(()->new RuntimeException("KO Tim Dc ROLE")));
        }
        user.setRoles(sett);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userMapper.toUserResponse(userRepository.save(user));
    }

    public String updatePassword(UpdatePasswordRequest request) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUserName(name).orElseThrow(()
                -> new WebException(ErrorCode.USER_NOT_EXISTED) );
        if(passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            if(request.getNewPassword().equals(request.getConfirmPassword())) {
                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                userRepository.save(user);
                return "Password changed successfully";
            } else {
                return "Confirm password does not match";
            }
        }
        return "Wrong old password";
    }

    public String changePassword(String email, ChangePassword changePassword) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new WebException(ErrorCode.USER_NOT_EXISTED));
        ForgotPassword fp = forgotPasswordRepository.findByUserEntityAndVerified(userEntity,true)
                .orElseThrow(() -> new RuntimeException("OTP khong hop le, ban khong co quyen doi mat khau"));
        if(!Objects.equals(changePassword.getPassword(), changePassword.getRepeatPassword())) {
            return "Please Enter Password Again";
        }
        userEntity.setPassword(passwordEncoder.encode(changePassword.getPassword()));
        forgotPasswordRepository.delete(fp);
        userRepository.save(userEntity);
        return "Password changed successfully";
    }

    public void deleteUser(Long userid) {
        userRepository.deleteById(userid);
    }
    public long countUser() {
        return userRepository.countUser();
    }
}
