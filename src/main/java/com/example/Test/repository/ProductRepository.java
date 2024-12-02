package com.example.Test.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.Test.model.po.Product;

@Repository // 標記這個介面是一個 Spring Data JPA 的倉儲類別
public interface ProductRepository extends JpaRepository<Product, Integer> {

	List<Product> findByCategory(String category);
    // 繼承 JpaRepository，泛型參數分別是實體類型 Product 和主鍵類型 Integer
	
	 long countByCategory(String category);

	 	/**
	     * 獲取產品類別統計數據
	     * @return 統計數據列表
	     */
	 @Query("SELECT s.name, SUM(s.quantity) FROM Sale s GROUP BY s.name")
	    List<Object[]> findProductStatisticsFromSales();
}


