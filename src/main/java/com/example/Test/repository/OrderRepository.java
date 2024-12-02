package com.example.Test.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Test.model.po.Order;

@Repository // 將這個介面標註為Spring的Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	Order findByName(String name);

	List<Order> findByUsername(String username);
	
}
