package com.mit.fmp.service;


import java.util.List;

import com.mit.fmp.dto.UserRequest;
import com.mit.fmp.dto.UserResponse;

public interface UserService {

    UserResponse registerUser(UserRequest request);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);
}
