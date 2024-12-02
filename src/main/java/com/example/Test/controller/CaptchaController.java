package com.example.Test.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    private static final Logger logger = LoggerFactory.getLogger(CaptchaController.class);

    @GetMapping("/generate")
    public void generateCaptcha(HttpServletRequest request, HttpServletResponse response) {
        int width = 100;
        int height = 50;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        Random random = new Random();

        try {
            // 設置背景色
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);

            // 設置干擾線
            graphics.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i < 10; i++) {
                int x1 = random.nextInt(width);
                int y1 = random.nextInt(height);
                int x2 = random.nextInt(width);
                int y2 = random.nextInt(height);
                graphics.drawLine(x1, y1, x2, y2);
            }

            // 設置字體
            graphics.setFont(new Font("Arial", Font.BOLD, 20));

            // 生成驗證碼
            int num1 = random.nextInt(10);
            int num2 = random.nextInt(10);
            String captcha = num1 + " + " + num2 + " = ?";
            int result = num1 + num2;

            // 將結果存儲在 session 中
            request.getSession().setAttribute("captchaResult", result);

            // 繪製驗證碼
            graphics.setColor(Color.BLACK);
            graphics.drawString(captcha, 10, 25);

            // 釋放資源
            graphics.dispose();

            // 將圖像寫入響應
            response.setContentType("image/png");
            ImageIO.write(bufferedImage, "png", response.getOutputStream());
        } catch (IOException e) {
            logger.error("生成驗證碼時出錯", e);
        } catch (Exception e) {
            logger.error("未知錯誤", e);
        }
    }
}
