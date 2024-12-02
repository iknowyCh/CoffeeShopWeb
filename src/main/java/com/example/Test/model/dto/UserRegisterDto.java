package com.example.Test.model.dto;

public class UserRegisterDto {

    // 使用者名稱
    private String username;

    // 使用者密碼
    private String password;

    // 使用者電子郵件
    private String email;
    
    private String captcha;

    // 獲取使用者名稱
    public String getUsername() {
        return username;
    }
    

    // 設定使用者名稱
    public void setUsername(String username) {
        this.username = username;
    }

    // 獲取使用者密碼
    public String getPassword() {
        return password;
    }

    // 設定使用者密碼
    public void setPassword(String password) {
        this.password = password;
    }

    // 獲取使用者電子郵件
    public String getEmail() {
        return email;
    }

    // 設定使用者電子郵件
    public void setEmail(String email) {
        this.email = email;
    }
    

    public String getCaptcha() {
		return captcha;
	}

	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}

	// 重寫 toString 方法，方便日誌輸出
    @Override
    public String toString() {
        return "UserRegisterDto [username=" + username + ", password=" + password + ", email=" + email + "]";
    }
}
