package com.example.Test.model.dto;

import com.example.Test.model.po.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {

    private long id; // 使用者ID

    @NotBlank(message = "使用者名稱不可為空") // 使用者名稱不可為空
    @Size(min = 3, max = 50, message = "使用者名稱長度應在3到50個字之間") // 使用者名稱長度限制
    private String username; // 使用者名稱

    @NotBlank(message = "密碼不可為空") // 密碼不可為空
    @Size(min = 6, max = 100, message = "密碼長度應在6到100個字之間") // 密碼長度限制
    private String password; // 密碼

    @NotBlank(message = "Email不可為空") // Email不可為空
    @Email(message = "Email 格式不正確") // Email格式驗證
    private String email; // Email地址

    // 預設建構子
    public UserDto() {}

    // 用於更新的建構子
    public UserDto(User user) {
        this.id = user.getId(); // 設置使用者ID
        this.username = user.getUsername(); // 設置使用者名稱
        this.password = user.getPassword(); // 設置密碼
        this.email = user.getEmail(); // 設置Email
    }

    // Getters 和 Setters

    public long getId() {
        return id; // 獲取使用者ID
    }

    public void setId(long id) {
        this.id = id; // 設置使用者ID
    }

    public String getUsername() {
        return username; // 獲取使用者名稱
    }

    public void setUsername(String username) {
        this.username = username; // 設置使用者名稱
    }

    public String getPassword() {
        return password; // 獲取密碼
    }

    public void setPassword(String password) {
        this.password = password; // 設置密碼
    }

    public String getEmail() {
        return email; // 獲取Email
    }

    public void setEmail(String email) {
        this.email = email; // 設置Email
    }
}
