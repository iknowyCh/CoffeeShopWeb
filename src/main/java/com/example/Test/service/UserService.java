package com.example.Test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.Test.model.dto.UserDto;
import com.example.Test.model.dto.UserRegisterDto;
import com.example.Test.model.po.User;
import com.example.Test.repository.UserRepository;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final int TOKEN_EXPIRATION_MINUTES = 1;

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username) != null;
    }

    public boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public User saveUser(UserDto userDto) throws Exception {
        boolean usernameTaken = isUsernameTaken(userDto.getUsername());
        boolean emailTaken = isEmailTaken(userDto.getEmail());

        if (usernameTaken || emailTaken) {
            throw new Exception("使用者名稱及 Email 已存在");
        }

        User user = new User();
        user.setUsername(userDto.getUsername());

        byte[] salt = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        String saltHex = bytesToHex(salt);

        String hashedPassword = hashPassword(userDto.getPassword(), salt);
        user.setPassword(hashedPassword);
        user.setSalt(saltHex);
        user.setEmail(userDto.getEmail());

        System.out.println("Saving user: " + user);

        return userRepository.save(user);
    }

    public User registerNewUser(UserRegisterDto userRegisterDto) throws Exception {
        boolean usernameTaken = isUsernameTaken(userRegisterDto.getUsername());
        boolean emailTaken = isEmailTaken(userRegisterDto.getEmail());

        if (usernameTaken || emailTaken) {
            throw new Exception("使用者名稱及 Email 已存在");
        }

        User newUser = new User();
        newUser.setUsername(userRegisterDto.getUsername());

        byte[] salt = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);
        String saltHex = bytesToHex(salt);

        String hashedPassword = hashPassword(userRegisterDto.getPassword(), salt);
        newUser.setPassword(hashedPassword);
        newUser.setSalt(saltHex);
        newUser.setEmail(userRegisterDto.getEmail());

        return userRepository.save(newUser);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User checkUserCredentials(String username, String password) throws Exception {
        User user = userRepository.findByUsername(username);
        if (user != null && checkPassword(password, user.getPassword(), user.getSalt())) {
            return user;
        }
        return null;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserById(int id) {
        return userRepository.findById((long) id).orElse(null);
    }

    public void updateUser(long id, UserDto userDto) throws Exception {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setUsername(userDto.getUsername());

            // 只在新密碼不為空的情況下更新密碼
            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                // 生成新的鹽值
                byte[] salt = new byte[16];
                SecureRandom secureRandom = new SecureRandom();
                secureRandom.nextBytes(salt);
                String saltHex = bytesToHex(salt);

                // 使用加鹽哈希密碼
                String hashedPassword = hashPassword(userDto.getPassword(), salt);
                user.setPassword(hashedPassword);
                user.setSalt(saltHex);
            }

            user.setEmail(userDto.getEmail());

            userRepository.save(user);
        }
    }








    public void deleteUser(int id) {
        userRepository.deleteById((long) id);
    }

    private String hashPassword(String password, byte[] salt) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(salt);
        byte[] hashedBytes = messageDigest.digest(password.getBytes());
        return bytesToHex(hashedBytes);
    }

    private boolean checkPassword(String password, String storedPassword, String saltHex) throws Exception {
        byte[] salt = hexToBytes(saltHex);
        String hashedPassword = hashPassword(password, salt);
        return hashedPassword.equals(storedPassword);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public List<User> searchUsersByKeyword(String keyword) {
        return userRepository.findAll().stream()
                .filter(user -> user.getUsername().contains(keyword) || user.getEmail().contains(keyword))
                .collect(Collectors.toList());
    }

    public User findByUsernameAndEmail(String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email);
    }

    public void sendResetPasswordLink(User user) throws Exception {
        if (user.getLastResetRequest() != null && user.getLastResetRequest().isAfter(LocalDateTime.now().minusSeconds(30))) {
            throw new Exception("請稍後再試");
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setLastResetRequest(LocalDateTime.now());
        userRepository.save(user);

        String resetLink = "http://localhost:8080/users/reset-password?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("重置密碼連結");
        message.setText("請點擊以下連結在1分鐘內重置您的密碼: " + resetLink);

        mailSender.send(message);

        user.setResetTokenCreationTime(LocalDateTime.now());
        userRepository.save(user);
    }

    public User findByResetToken(String token) {
        User user = userRepository.findByResetToken(token);
        if (user == null || isTokenExpired(user.getResetTokenCreationTime())) {
            return null;
        }
        return user;
    }

    private boolean isTokenExpired(LocalDateTime tokenCreationTime) {
        return Duration.between(tokenCreationTime, LocalDateTime.now()).toMinutes() >= TOKEN_EXPIRATION_MINUTES;
    }

    public boolean resetPassword(String token, String newPassword) throws Exception {
        User user = findByResetToken(token);
        if (user != null) {
            byte[] salt = hexToBytes(user.getSalt());
            String hashedPassword = hashPassword(newPassword, salt);
            user.setPassword(hashedPassword);
            user.setResetToken(null);
            user.setResetTokenCreationTime(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
