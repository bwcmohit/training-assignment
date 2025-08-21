package org.stark.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.stark.configs.FileStorageConfig;
import org.stark.configs.JwtUtil;
import org.stark.dtos.*;
import org.stark.entities.User;
import org.stark.enums.Roles;
import org.stark.exceptions.AuthException;
import org.stark.exceptions.CryptoException;
import org.stark.repositories.UserRepository;
import org.stark.utils.FileTypeUtil;
import org.stark.utils.ImageUtil;
import org.stark.utils.PathUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;  // use BCrypt
    private final FileStorageConfig storageConfig;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder encoder, JwtUtil jwtUtil, FileStorageConfig storageConfig) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.storageConfig = storageConfig;
    }

    @Override
    public UserResponseDTO register(RegisterUserDTO dto) {
        if (userRepository.existsByUsername(dto.username()) || userRepository.existsByEmail(dto.email())) {
            throw new AuthException("Username or Email already exists");
        }

        try {

            User user = new User();
            user.setFirstName(dto.firstName());
            user.setLastName(dto.lastName());
            user.setUsername(dto.username());
            user.setEmail(dto.email());
            user.setMobile(dto.mobile());
            user.setPassword(encoder.encode(dto.password()));
            user.setRole(Roles.USER);

            User saved = userRepository.save(user);

            return mapToDto(saved);

        } catch (Exception e) {
            throw new CryptoException("Password encryption failed", e);
        }
    }

    @Override
    public void uploadProfileImage(Long userId, ProfileImageUploadDTO dto) {
        // 1️⃣ Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found"));

        // 2️⃣ Validate file (size, type)
        dto.validate();

        // 3️⃣ Process image
        try {
            processProfileImage(user, dto.file());
        } catch (Exception e) {
            // Log full stack trace
            log.error("Failed to process profile image for userId {}: {}", userId, e.getMessage(), e);


            // Throw custom exception for API response
            throw new RuntimeException("Failed to upload profile image: " + e.getMessage(), e);
        }
    }

    // --- Synchronous processing for immediate feedback ---
    private void processProfileImage(User user, MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("No file provided");
        }

        // 1️⃣ Generate safe filename
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String ext = FileTypeUtil.getImageExtension(image.getContentType());
        String baseFileName = user.getId() + "_" + timestamp + "_" + UUID.randomUUID();
        String safeFileName = Paths.get(baseFileName + ext).getFileName().toString();

        // 2️⃣ Resolve paths
        Path originalPath = PathUtil.resolvePath(storageConfig.getImages(), safeFileName);
        Path optimizedPath = PathUtil.resolvePath(storageConfig.getImagesOptimized(), safeFileName);

        // 3️⃣ Save to disk
        Files.copy(image.getInputStream(), originalPath, StandardCopyOption.REPLACE_EXISTING);

        // 4️⃣ Resize & compress
        ImageUtil.resizeAndCompress(originalPath.toFile(), optimizedPath.toFile());

        // 5️⃣ Update user entity
        user.setProfileImageFilename(safeFileName);
        userRepository.save(user);

        log.info("Profile image saved for userId {} as {}", user.getId(), safeFileName);
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByUsernameOrEmail(dto.usernameOrEmail(), dto.usernameOrEmail())
                .orElseThrow(() -> new AuthException("Invalid credentials"));

        try {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
                throw new AuthException("Invalid credentials");
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

            // Expiry (15 mins from now)
            long expiryMillis = System.currentTimeMillis() + (15 * 60 * 1000);
            Instant expiryInstant = Instant.ofEpochMilli(expiryMillis);
            String expiryMessage = "Token will expire in 15 minutes at " + expiryInstant.toString();

            // Return userId along with token info
            long userId = user.getId();
            return new LoginResponseDTO(userId, token, expiryMessage);

        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new CryptoException("Password verification failed", e);
        }
    }



    @Override
    public UserResponseDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new AuthException("User not found"));
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapToDto)
                .orElseThrow(() -> new AuthException("User not found"));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new AuthException("User not found");
        userRepository.deleteById(id);
    }

    @Override
    public User getUserEntityByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new AuthException("User not found"));
    }

    private UserResponseDTO mapToDto(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUsername(),
                user.getMobile(),
                user.getRole()
        );
    }
}
