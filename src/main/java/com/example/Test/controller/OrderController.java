package com.example.Test.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.Test.model.dto.OrderDto;
import com.example.Test.model.po.Order;
import com.example.Test.model.po.Sale;
import com.example.Test.service.OrderService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 訪問訂單管理頁面
    @GetMapping("/order")
    public String order() {
        return "products/order"; // 返回訂單管理頁面模板名稱
    }

    // 獲取所有銷售記錄
    @GetMapping("/products/order")
    public String getAllSales(Model model) {
        List<Sale> sales = orderService.getAllSales();
        DecimalFormat df = new DecimalFormat("#");
        sales.forEach(sale -> sale.setTotalAmount(Double.valueOf(df.format(sale.getTotalAmount()))));
        model.addAttribute("sales", sales);
        return "products/order";
    }

    // 獲取訂單詳細信息
    @GetMapping("/products/orderDetail/{id}")
    public String getOrderDetail(@PathVariable("id") Long id, Model model) {
        Sale sale = orderService.getSaleById(id); // 根據ID獲取銷售記錄
        model.addAttribute("sale", sale); // 將銷售記錄添加到模型中
        return "products/orderdetail"; // 返回訂單詳細信息頁面模板名稱
    }

 // 獲取訂單詳細信息（後台用，包含操作按鈕）
    @GetMapping("/products/orderDetail/{id}/modalWithActions")
    @ResponseBody
    public String getOrderDetailForModalWithActions(@PathVariable("id") Long id) {
        Sale sale = orderService.getSaleById(id); // 根據ID獲取銷售記錄
        String formattedOrderTime = orderService.formatOrderTime(sale.getOrderTime()); // 格式化訂單時間

        // 使用 DecimalFormat 來格式化總價為整數
        DecimalFormat df = new DecimalFormat("#");
        String formattedTotalAmount = df.format(sale.getTotalAmount());

        return "<div>" +
               "<p>訂單編號: " + sale.getId() + "</p>" +
               "<p>總價: " + formattedTotalAmount + "</p>" +
               "<p>商品明細: " + sale.getName() + "</p>" +
               "<p>狀態: " + sale.getStatus() + "</p>" +
               "<p>建立時間: " + formattedOrderTime + "</p>" +
               "<div class='row'>" +
               "<div class='col'><form action='/products/orderDetail/" + sale.getId() + "/updateStatus' method='post'>" +
               "<input type='hidden' name='status' value='已付款'/>" +
               "<button type='submit' class='btn btn-success'>已付款</button>" +
               "</form></div>" +
               "<div class='col'><form action='/products/orderDetail/" + sale.getId() + "/updateStatus' method='post'>" +
               "<input type='hidden' name='status' value='未付款'/>" +
               "<button type='submit' class='btn btn-warning'>未付款</button>" +
               "</form></div>" +
               "<div class='col'><form id='deleteForm' action='/products/orderDetail/" + sale.getId() + "/deleteSale' method='post'>" +
               "<button type='button' class='btn btn-danger' onclick='confirmDelete()'>刪除訂單</button>" +
               "</form></div>" +
               "</div></div>";
    }

    // 獲取訂單詳細信息（後台用，不包含操作按鈕）
    @GetMapping("/products/orderDetail/{id}/modal")
    @ResponseBody
    public String getOrderDetailForModal(@PathVariable("id") Long id) {
        Sale sale = orderService.getSaleById(id); // 根據ID獲取銷售記錄
        String formattedOrderTime = orderService.formatOrderTime(sale.getOrderTime()); // 格式化訂單時間

        // 使用 DecimalFormat 來格式化總價為整數
        DecimalFormat df = new DecimalFormat("#");
        String formattedTotalAmount = df.format(sale.getTotalAmount());

        return "<div>" +
               "<p>訂單編號: " + sale.getId() + "</p>" +
               "<p>總價: " + formattedTotalAmount + "</p>" +
               "<p>商品明細: " + sale.getName() + "</p>" +
               "<p>狀態: " + sale.getStatus() + "</p>" +
               "<p>建立時間: " + formattedOrderTime + "</p>" +
               "</div>";
    }

    // 更新訂單狀態
    @PostMapping("/products/orderDetail/{id}/updateStatus")
    public String updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        orderService.updateOrderStatus(id, status); // 更新訂單狀態
        return "redirect:/products/order"; // 重定向到訂單頁面
    }

    // 刪除銷售記錄
    @PostMapping("/products/orderDetail/{id}/deleteSale")
    public String deleteSale(@PathVariable("id") Long id) {
        orderService.deleteSale(id); // 刪除銷售記錄
        return "redirect:/products/order"; // 重定向到訂單頁面
    }

    // 獲取當前用戶的檢查記錄
    @GetMapping("/products/check")
    public String check(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username"); // 獲取當前用戶的使用者名稱
        List<Sale> sales = orderService.getAllSalesByUsername(username); // 根據使用者名稱獲取銷售記錄
        for (Sale sale : sales) {
            String formattedOrderTime = orderService.formatOrderTime(sale.getOrderTime()); // 格式化訂單時間
            sale.setOrderTimeFormatted(formattedOrderTime); // 假設我們在 Sale 類別中增加了一個臨時屬性 orderTimeFormatted
        }
        model.addAttribute("sales", sales); // 將銷售記錄添加到模型中
        return "products/check"; // 返回檢查頁面模板名稱
    }

    @ResponseBody
    public String getCheckDetail(@PathVariable("id") Long id, Model model) {
        Sale sale = orderService.getSaleById(id); // 根據ID獲取銷售記錄
        String formattedOrderTime = orderService.formatOrderTime(sale.getOrderTime()); // 格式化訂單時間

        // 使用 DecimalFormat 來格式化總價為整數
        DecimalFormat df = new DecimalFormat("#");
        String formattedTotalAmount = df.format(sale.getTotalAmount());

        model.addAttribute("sale", sale); // 將銷售記錄添加到模型中
        model.addAttribute("formattedOrderTime", formattedOrderTime); // 傳遞格式化的日期字符串

        return "<div>" +
               "<p>訂單編號: " + sale.getId() + "</p>" +
               "<p>總價: " + formattedTotalAmount + "</p>" +
               "<p>商品明細: " + sale.getName() + "</p>" +
               "<p>狀態: " + sale.getStatus() + "</p>" +
               "<p>建立時間: " + formattedOrderTime + "</p>" +
               "<div class='row'>" +
               "<div class='col'><form action='/products/orderDetail/" + sale.getId() + "/updateStatus' method='post'>" +
               "<input type='hidden' name='status' value='已付款'/>" +
               "<button type='submit' class='btn btn-success'>已付款</button>" +
               "</form></div>" +
               "<div class='col'><form action='/products/orderDetail/" + sale.getId() + "/updateStatus' method='post'>" +
               "<input type='hidden' name='status' value='未付款'/>" +
               "<button type='submit' class='btn btn-warning'>未付款</button>" +
               "</form></div>" +
               "<div class='col'><form id='deleteForm' action='/products/orderDetail/" + sale.getId() + "/deleteSale' method='post'>" +
               "<button type='button' class='btn btn-danger' onclick='confirmDelete()'>刪除訂單</button>" +
               "</form></div>" +
               "</div></div>";
    }

    // 新增訂單
    @PostMapping("/orders/add")
    public String addOrder(HttpServletRequest request, HttpSession session) {
        String username = (String) session.getAttribute("username"); // 獲取當前用戶的使用者名稱
        List<OrderDto> orders = new ArrayList<>();

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            if (paramName.equals("name")) {
                String[] names = request.getParameterValues("name");
                String[] prices = request.getParameterValues("price");
                String[] quantities = request.getParameterValues("quantity");

                for (int i = 0; i < names.length; i++) {
                    int quantity = Integer.parseInt(quantities[i]);
                    if (quantity > 0) {
                        OrderDto orderDTO = new OrderDto();
                        orderDTO.setName(names[i]);
                        orderDTO.setPrice(Double.parseDouble(prices[i]));
                        orderDTO.setQuantity(quantity);
                        orders.add(orderDTO);
                    }
                }
            }
        }

        orderService.addOrUpdateOrders(orders, username); // 保存訂單並將使用者名稱傳遞
        return "redirect:/cart";
    }

    // 獲取購物車內容
    @GetMapping("/cart")
    public String getCart(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username"); // 獲取當前用戶的使用者名稱
        List<Order> cartItems = orderService.getOrdersByUsername(username); // 獲取當前用戶的訂單
        int totalAmount = 0;
        for (Order order : cartItems) {
            order.setTotalAmount(order.getPrice() * order.getQuantity()); // 計算訂單總金額
            totalAmount += order.getTotalAmount();
        }
        model.addAttribute("cart", cartItems); // 將購物車內容添加到模型中
        model.addAttribute("totalAmount", totalAmount); // 將總金額添加到模型中
        return "/products/shopcart"; // 返回購物車頁面模板名稱
    }

    // 更新訂單數量
    @PostMapping("/orders/update/{id}")
    public String updateOrderQuantity(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity) {
        orderService.updateOrderQuantity(id, quantity); // 更新訂單數量
        return "redirect:/cart"; // 重定向到購物車頁面
    }

    // 刪除訂單
    @PostMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") Long id) {
        orderService.deleteOrder(id); // 刪除訂單
        return "redirect:/cart"; // 重定向到購物車頁面
    }

    @PostMapping("/orders/checkout")
    public String checkout(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        List<Sale> sales = orderService.transferOrdersToSales(username); // 獲取轉移後的銷售列表
        if (sales == null || sales.isEmpty()) {
            System.out.println("No orders found for user: " + username);
            return "redirect:/cart"; // 如果沒有訂單，重定向到購物車頁面
        }
        DecimalFormat df = new DecimalFormat("#"); // 創建格式化器，確保輸出為整數
        double totalAmount = sales.stream().mapToDouble(Sale::getTotalAmount).sum(); // 計算總金額
        List<OrderDto> orderDetails = new ArrayList<>();
        for (Sale sale : sales) {
            String[] items = sale.getName().split(" ");
            for (String item : items) {
                String[] details = item.split("\\*");
                OrderDto orderDto = new OrderDto();
                orderDto.setName(details[0]);
                int quantity = Integer.parseInt(details[1]);
                orderDto.setQuantity(quantity);
                double price = sale.getTotalAmount() / quantity; // 計算單價
                orderDto.setPrice(Double.parseDouble(df.format(price))); // 設置單價，格式化為整數
                orderDto.setTotalAmount(Double.parseDouble(df.format(price * quantity))); // 計算小計，格式化為整數
                orderDetails.add(orderDto);
            }
        }
        sales.forEach(sale -> System.out.println(sale)); // 打印每個銷售的詳細信息
        model.addAttribute("orders", orderDetails); // 將銷售列表添加到模型中
        model.addAttribute("totalAmount", Double.parseDouble(df.format(totalAmount))); // 將總金額添加到模型中，格式化為整數
        return "products/orderdetails"; // 返回訂單明細頁面
    }

}
