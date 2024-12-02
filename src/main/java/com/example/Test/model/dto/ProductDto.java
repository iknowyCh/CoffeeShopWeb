package com.example.Test.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.Test.model.po.Product;

import jakarta.validation.constraints.*;

public class ProductDto {

	// @NotEmpty(message = "產品名稱不得為空")
	private String name; // 產品名稱

	// @NotEmpty(message = "商品類別不得為空白")
	private String category; // 商品類別

	@Min(0)
	private double price; // 商品價格，必須大於等於 0

	private MultipartFile imageFile; // 商品圖片檔案

	private LocalDateTime createdAt; // 商品創建時間
	
	private String base64Image; // 此屬性將存儲 Base64 編碼

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public MultipartFile getImageFile() {
		return imageFile;
	}

	public void setImageFile(MultipartFile imageFile) {
		this.imageFile = imageFile;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getBase64Image() {
		return base64Image;
	}

	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}

	public static List<Product> getProductsByCategory(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	
}