package com.example.Test;

import org.springframework.boot.SpringApplication; // 導入 SpringApplication 類，用於啟動 Spring Boot 應用程式
import org.springframework.boot.autoconfigure.SpringBootApplication; // 導入 SpringBootApplication 註解，用於標註主應用程式類別
import org.springframework.boot.autoconfigure.domain.EntityScan; // 導入 EntityScan 註解，用於掃描實體類別

@SpringBootApplication // 標註這是一個 Spring Boot 應用程式的主類別
@EntityScan(basePackages = "com.example.Test.model") // 指定要掃描的實體類別所在的包
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args); // 啟動 Spring Boot 應用程式
    }
    
    
}
