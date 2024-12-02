package com.example.Test.model.dto;

public class UserLogoutDto {
	
	// 前端取得 登出的資訊
	private String username; // 使用者名稱
    private String password; // 密碼
    private int errorCount; // 錯誤次數欄位

    // Getter 和 Setter 方法

    // 獲取使用者名稱
    public String getUsername() {
        return username;
    }

    // 設置使用者名稱
    public void setUsername(String username) {
        this.username = username;
    }

    // 獲取密碼
    public String getPassword() {
        return password;
    }

    // 設置密碼
    public void setPassword(String password) {
        this.password = password;
    }

    // 獲取錯誤次數
    public int getErrorCount() {
        return errorCount;
    }

    // 設置錯誤次數
    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
}
