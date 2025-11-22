package com.dev.boxpoint.controller;

import com.dev.boxpoint.dtos.UserDto;
import com.dev.boxpoint.model.User;
import com.dev.boxpoint.request.CreateUserRequest;
import com.dev.boxpoint.request.UserUpdateRequest;
import com.dev.boxpoint.response.ApiResponse;
import com.dev.boxpoint.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/users")
public class UserController {

    private final IUserService userService;

    @GetMapping("/user/{userId}/user")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        UserDto userDto = userService.convertUserToDto(user);

        return ResponseEntity.ok(new ApiResponse("Found!", userDto));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        UserDto userDto = userService.convertUserToDto(user);

        return ResponseEntity.ok(new ApiResponse("Create user successfully!", userDto));
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<ApiResponse> updateUser(
            @RequestBody UserUpdateRequest request,
            @PathVariable Long userId) {
        User user = userService.updateUser(request, userId);
        UserDto userDto = userService.convertUserToDto(user);

        return ResponseEntity.ok(new ApiResponse("Update user successfully!", userDto));
    }

    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);

        return ResponseEntity.ok(new ApiResponse("Delete user successfully!", null));
    }
}
