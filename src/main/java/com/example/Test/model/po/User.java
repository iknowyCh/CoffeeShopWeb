package com.example.Test.model.po;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity // 標記這個類別是一個 JPA 實體
public class User {

    @Id // 標記這個欄位是主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動生成 ID
    private Long id;

    @Column(unique = true) // 將電子郵件設置為唯一
    private String username;

    @Column(unique = true) // 將電子郵件設置為唯一
    private String email;

    @Column // 對應資料表的欄位
    private String password;

    @Column // 對應資料表的欄位
    private String salt;

    @Column // 新增的欄位，存儲重置密碼的 token
    private String resetToken;

    @Column // 新增的欄位，存儲重置密碼 token 的生成時間
    private LocalDateTime resetTokenCreationTime;

    @Column // 新增的欄位，存儲最後一次請求重置密碼的時間
    private LocalDateTime lastResetRequest;

    // Getter 和 Setter 方法

    // 獲取使用者ID
    public Long getId() {
        return id;
    }

    // 設置使用者ID
    public void setId(Long id) {
        this.id = id;
    }

    // 獲取使用者名稱
    public String getUsername() {
        return username;
    }

    // 設置使用者名稱
    public void setUsername(String username) {
        this.username = username;
    }

    // 獲取Email
    public String getEmail() {
        return email;
    }

    // 設置Email
    public void setEmail(String email) {
        this.email = email;
    }

    // 獲取密碼
    public String getPassword() {
        return password;
    }

    // 設置密碼
    public void setPassword(String password) {
        this.password = password;
    }

    // 獲取Salt
    public String getSalt() {
        return salt;
    }

    // 設置Salt
    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public LocalDateTime getResetTokenCreationTime() {
        return resetTokenCreationTime;
    }

    public void setResetTokenCreationTime(LocalDateTime resetTokenCreationTime) {
        this.resetTokenCreationTime = resetTokenCreationTime;
    }

    public LocalDateTime getLastResetRequest() {
        return lastResetRequest;
    }

    public void setLastResetRequest(LocalDateTime lastResetRequest) {
        this.lastResetRequest = lastResetRequest;
    }
}
