package com.example.Test.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Test.model.dto.ProductDto;
import com.example.Test.model.po.Product;
import com.example.Test.model.po.Sale;
import com.example.Test.repository.ProductRepository;
import com.example.Test.repository.SaleRepository;

@Service // 標註此類別為 Spring 服務層組件
public class ProductService {

    @Autowired // 自動注入 ProductRepository
    private ProductRepository productRepository;

    @Autowired // 自動注入 SaleRepository
    private SaleRepository saleRepository;

    // 保存產品
    public Product saveProduct(Product product) {
        // 生成唯一商品代號
        String code = generateProductCode(product.getCategory());
        product.setCode(code);

        return productRepository.save(product); // 保存產品
    }

    // 生成唯一的商品代號
    private String generateProductCode(String category) {
        long count = productRepository.countByCategory(category);
        return category.toLowerCase() + (count + 1); // 生成代號
    }

    // 獲取所有產品，並按 ID 逆序排序
    public List<Product> findAllProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.DESC, "id")); // 獲取所有產品並排序
    }

    // 根據類別獲取產品
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category); // 根據類別查詢產品
    }

    // 模擬的查詢所有產品的方法
    public List<Product> findAll() {
        // 假設這裡是從資料庫查詢的所有產品
        return new ArrayList<>();
    }

    // 根據 ID 查找產品
    public Product findProductById(int id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid product ID:" + id)); // 根據 ID 查找產品
    }

    // 創建新產品
    public void createProduct(ProductDto productDto) {
        MultipartFile image = productDto.getImageFile();
        String base64Image = "";
        try {
            base64Image = Base64.getEncoder().encodeToString(image.getBytes()); // 將圖片轉成 Base64 編碼
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String uploadDir = "public/photo/";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, uploadPath.resolve(System.currentTimeMillis() + "_" + image.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        productDto.setBase64Image(base64Image);

        Product product = new Product();
        product.setName(productDto.getName());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setCreatedAt(LocalDateTime.now());
        product.setBase64Image(base64Image);

        String code = generateProductCode(productDto.getCategory());
        product.setCode(code);

        productRepository.save(product); // 保存新產品
    }

    // 更新產品
 // 更新產品
    public void updateProduct(int id, ProductDto productDto) {
        try {
            Product product = findProductById(id);

            if (!productDto.getImageFile().isEmpty()) {
                String uploadDir = "public/photo/";
                Path oldImagePath = Paths.get(uploadDir + product.getBase64Image());
                try {
                    Files.deleteIfExists(oldImagePath); // 刪除舊圖片文件
                } catch (Exception ex) {
                    System.out.println("Exception：" + ex.getMessage());
                }

                MultipartFile image = productDto.getImageFile();
                String storageFileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();

                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
                }

                String base64Image = "";
                try {
                    base64Image = Base64.getEncoder().encodeToString(image.getBytes()); // 將圖片轉成 Base64 編碼
                } catch (IOException e) {
                    e.printStackTrace();
                }
                product.setBase64Image(base64Image); // 更新圖片的 Base64 編碼
            }

            product.setName(productDto.getName());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setCreatedAt(LocalDateTime.now()); // 確保創建時間被更新

            productRepository.save(product); // 保存更新後的產品
        } catch (Exception ex) {
            System.out.println("異常：" + ex.getMessage());
        }
    }


    // 刪除產品
    public void deleteProduct(int id) {
        try {
            Product product = findProductById(id);
            Path imagePath = Paths.get("public/photo/" + product.getBase64Image());
            try {
                Files.delete(imagePath); // 刪除圖片文件
            } catch (Exception ex) {
                System.out.println("Exception:" + ex.getMessage());
            }
            productRepository.delete(product); // 從資料庫中刪除產品
        } catch (Exception ex) {
            System.out.println("異常：" + ex.getMessage());
        }
    }

    // 獲取產品類別統計數據
    public List<Object[]> getProductStatisticsFromSales() {
        List<Sale> sales = saleRepository.findAll();
        Map<String, Integer> productStatistics = new HashMap<>();

        for (Sale sale : sales) {
            String[] items = sale.getName().split(" ");
            for (String item : items) {
                String[] details = item.split("\\*");
                if (details.length == 2) { // 確保 details 長度為2
                    String productName = details[0];
                    int quantity = Integer.parseInt(details[1]);
                    productStatistics.put(productName, productStatistics.getOrDefault(productName, 0) + quantity);
                }
            }
        }

        List<Object[]> statistics = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : productStatistics.entrySet()) {
            statistics.add(new Object[] { entry.getKey(), entry.getValue() });
        }

        return statistics; // 獲取產品銷售統計數據
    }

    // 獲取產品統計數據
    public Map<String, Integer> getProductStatistics() {
        List<Sale> sales = saleRepository.findAll();
        Map<String, Integer> productStatistics = new HashMap<>();

        for (Sale sale : sales) {
            String[] items = sale.getName().split(" ");
            for (String item : items) {
                String[] details = item.split("\\*");
                String productName = details[0];
                int quantity = Integer.parseInt(details[1]);
                productStatistics.put(productName, productStatistics.getOrDefault(productName, 0) + quantity);
            }
        }

        return productStatistics;
    }

    // 獲取每日總結
    public List<Object[]> getDailySummary() {
        LocalDate today = LocalDate.now();
        List<Sale> sales = saleRepository.findByOrderTimeBetween(today.atStartOfDay(), today.plusDays(1).atStartOfDay());
        Map<String, Object[]> summary = new HashMap<>();

        for (Sale sale : sales) {
            String[] items = sale.getName().split(" ");
            for (String item : items) {
                String[] details = item.split("\\*");
                if (details.length == 2) {
                    String productName = details[0];
                    int quantity = Integer.parseInt(details[1]);
                    int totalAmount = (int) sale.getTotalAmount(); // 確保總金額為整數

                    Object[] summaryData = summary.getOrDefault(productName, new Object[]{productName, 0, 0});
                    summaryData[1] = ((Integer) summaryData[1]) + quantity;
                    summaryData[2] = ((Integer) summaryData[2]) + totalAmount;
                    summary.put(productName, summaryData);
                }
            }
        }

        return new ArrayList<>(summary.values());
    }
    
 // 獲取指定日期的每日總結
    public List<Object[]> getDailySummaryByDate(LocalDate date) {
        List<Sale> sales = saleRepository.findByOrderTimeBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        Map<String, Object[]> summary = new HashMap<>();

        for (Sale sale : sales) {
            String[] items = sale.getName().split(" ");
            for (String item : items) {
                String[] details = item.split("\\*");
                if (details.length == 2) {
                    String productName = details[0];
                    int quantity = Integer.parseInt(details[1]);
                    int totalAmount = (int) sale.getTotalAmount(); // 確保總金額為整數

                    Object[] summaryData = summary.getOrDefault(productName, new Object[]{productName, 0, 0});
                    summaryData[1] = ((Integer) summaryData[1]) + quantity;
                    summaryData[2] = ((Integer) summaryData[2]) + totalAmount;
                    summary.put(productName, summaryData);
                }
            }
        }

        return new ArrayList<>(summary.values());
    }

    
 // 獲取指定日期的前五名銷售產品
    public List<Object[]> getTop5ProductsByDate(LocalDate date) {
        List<Sale> sales = saleRepository.findByOrderTimeBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        Map<String, Integer> productStatistics = new HashMap<>();

        for (Sale sale : sales) {
            String[] items = sale.getName().split(" ");
            for (String item : items) {
                String[] details = item.split("\\*");
                if (details.length == 2) {
                    String productName = details[0];
                    int quantity = Integer.parseInt(details[1]);
                    productStatistics.put(productName, productStatistics.getOrDefault(productName, 0) + quantity);
                }
            }
        }

        return productStatistics.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(5)
            .map(e -> new Object[]{e.getKey(), e.getValue()})
            .collect(Collectors.toList());
	}
}
