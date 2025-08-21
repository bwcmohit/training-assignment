package org.stark.services;

import org.stark.dtos.*;
import org.stark.entities.User;

public interface IUserService {

    UserResponseDTO register(RegisterUserDTO dto);

    void uploadProfileImage(Long userId, ProfileImageUploadDTO dto);

    LoginResponseDTO login(LoginRequestDTO dto);

    UserResponseDTO getUserById(Long id);

    UserResponseDTO getUserByUsername(String username);

    void deleteUser(Long id);

    User getUserEntityByUsernameOrEmail(String usernameOrEmail);
}
