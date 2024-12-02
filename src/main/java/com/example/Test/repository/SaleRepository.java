package com.example.Test.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Test.model.po.Sale;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findByUsername(String username);

    Sale findByIdAndUsername(Long id, String username);

    @Query("SELECT s.name, SUM(s.quantity) FROM Sale s GROUP BY s.name")
    List<Object[]> findProductStatisticsFromSales(); // 查詢產品銷售統計數據

	List<Sale> findByOrderTimeBetween(LocalDateTime atStartOfDay, LocalDateTime atStartOfDay2);

	

	
    

}
