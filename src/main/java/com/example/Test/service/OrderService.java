package com.example.Test.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Test.model.dto.OrderDto;
import com.example.Test.model.po.Order;
import com.example.Test.model.po.Sale;
import com.example.Test.repository.OrderRepository;
import com.example.Test.repository.SaleRepository;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SaleRepository saleRepository;

    // 格式化訂單時間
    public String formatOrderTime(LocalDateTime orderTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return orderTime.format(formatter);
    }

    // 保存訂單
    public void addOrUpdateOrders(List<OrderDto> orders, String username) {
        for (OrderDto orderDTO : orders) {
            Order order = new Order();
            order.setName(orderDTO.getName());
            order.setPrice(orderDTO.getPrice());
            order.setQuantity(orderDTO.getQuantity());
            order.setTotalAmount(orderDTO.getPrice() * orderDTO.getQuantity());
            order.setUsername(username);
            order.setOrderTime(LocalDateTime.now());
            orderRepository.save(order);
        }
    }
    
    public Sale getSaleByIdAndUsername(Long id, String username) {
        return saleRepository.findByIdAndUsername(id, username);
    }


    // 獲取所有訂單
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 根據用戶名稱獲取訂單
    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findByUsername(username);
    }

    // 計算所有訂單的總金額
    public double getTotalAmount() {
        List<Order> orders = getAllOrders();
        return orders.stream().mapToDouble(Order::getTotalAmount).sum(); // 計算總計
    }

    // 更新訂單數量
    public void updateOrderQuantity(Long id, Integer quantity) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setQuantity(quantity); // 更新數量
            order.setTotalAmount(order.getPrice() * quantity); // 更新總金額
            orderRepository.save(order); // 保存更新
        }
    }

    // 刪除訂單
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id); // 刪除訂單
    }

 // 將訂單轉移至銷售記錄，並清空訂單
    public List<Sale> transferOrdersToSales(String username) {
        List<Order> orders = getOrdersByUsername(username);
        List<Sale> sales = new ArrayList<>();
        if (!orders.isEmpty()) {
            Sale sale = new Sale();
            StringBuilder nameBuilder = new StringBuilder();
            double totalAmount = 0.0;
            for (Order order : orders) {
                nameBuilder.append(order.getName())
                           .append("*")
                           .append(order.getQuantity())
                           .append(" ");
                totalAmount += order.getTotalAmount();
            }
            sale.setName(nameBuilder.toString().trim()); // 設置格式化名稱
            sale.setPrice(0); // 設置價格為 0.0，因為這裡不需要單價
            sale.setQuantity(0); // 設置數量為 0，因為這裡不需要數量
            sale.setTotalAmount(totalAmount); // 設置總金額
            sale.setOrderTime(LocalDateTime.now());
            sale.setUsername(username);
            saleRepository.save(sale);
            sales.add(sale); // 添加到銷售列表
            orderRepository.deleteAll(orders); // 清空訂單
        }
        return sales; // 返回銷售列表
    }

    
    // 獲取所有銷售記錄
    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    // 根據ID獲取銷售記錄
    public Sale getSaleById(Long id) {
        return saleRepository.findById(id).orElse(null);
    }

    // 更新訂單狀態
    public void updateOrderStatus(Long id, String status) {
        Sale sale = saleRepository.findById(id).orElse(null);
        if (sale != null) {
            sale.setStatus(status);
            saleRepository.save(sale);
        }
    }

    // 刪除銷售記錄
    public void deleteSale(Long id) {
        saleRepository.deleteById(id);
    }

    // 根據使用者名稱獲取所有銷售記錄
    public List<Sale> getAllSalesByUsername(String username) {
        return saleRepository.findByUsername(username);
    }
    
 
}
