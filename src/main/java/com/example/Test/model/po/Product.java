package com.example.Test.model.po;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.*;

@Entity // 表示這是一個 JPA 實體
@Table(name = "product") // 對應的資料表名稱為 product
public class Product {

    @Id // 主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自動生成主鍵
    private int id;

    @Column // 對應資料表的欄位
    private String name;

    @Column // 對應資料表的欄位
    private String category;

    @Column // 對應資料表的欄位
    private double price;

    @Column // 對應資料表的欄位
    private LocalDateTime createdAt; // 使用 LocalDateTime 型別儲存時間

    @Column(columnDefinition = "LONGTEXT") // 將 base64Image 欄位設置為 LONGTEXT
	private String base64Image;
    
    @Column(unique = false)
    private String code; // 新增商品代號字段
    
    private LocalDateTime orderTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public LocalDateTime getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(LocalDateTime orderTime) {
		this.orderTime = orderTime;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", category=" + category + ", price=" + price + ", createdAt="
				+ createdAt + ", base64Image=" + base64Image + "]";
	}
}
