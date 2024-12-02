package com.example.Test.model.dto;

public class UserLoginDto {
    private String username; // 使用者名稱
    private String password; // 密碼
    private int errorCount; // 錯誤次數欄位
    private String captcha;

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

	public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
    
    
}
