package com.dev.boxpoint.service.user;

import com.dev.boxpoint.dtos.UserDto;
import com.dev.boxpoint.model.User;
import com.dev.boxpoint.request.CreateUserRequest;
import com.dev.boxpoint.request.UserUpdateRequest;

public interface IUserService {

    User createUser(CreateUserRequest request);

    User updateUser(UserUpdateRequest request, Long userId);

    User getUserById(Long userId);

    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);

}
