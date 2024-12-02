package com.example.Test;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptchaGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int WIDTH = 200;
    private static final int HEIGHT = 50;
    private static final Font FONT = new Font("Arial", Font.BOLD, 40);

    public static BufferedImage generateCaptchaImage(String captchaText) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // 設置背景顏色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 設置字體
        g.setFont(FONT);
        FontMetrics fm = g.getFontMetrics();

        // 設置文字顏色
        g.setColor(Color.BLACK);
        int x = 10;
        int y = ((HEIGHT - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(captchaText, x, y);

        // 添加一些噪點和線條
        Random random = new Random();
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < 50; i++) {
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        g.dispose();
        return image;
    }

    public static String generateCaptchaText() {
        Random random = new Random();
        StringBuilder captchaText = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            captchaText.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return captchaText.toString();
    }

	
}
