package com.example.ems.service;

import com.example.ems.payload.UserDto;

import java.util.List;

public interface UserService {
    UserDto registerNewUser(UserDto userDto);
    UserDto createUser(UserDto userDto);
    UserDto updateUser(UserDto userDto, long userId);
    UserDto getUserById(long userId);
    List<UserDto> getAllUsers();
    void deleteUser(long userId);
    UserDto assignManager(long userId, long managerId);
    List<UserDto> getAllUnassignedEmployees();

}
