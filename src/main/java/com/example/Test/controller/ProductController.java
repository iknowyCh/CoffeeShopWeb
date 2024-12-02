package com.example.Test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.example.Test.model.dto.ProductDto;
import com.example.Test.model.po.Product;
import com.example.Test.service.ProductService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;


@Controller
@RequestMapping("/products") // 定義基礎 URL 路徑為 /products
public class ProductController {

    @Autowired 
    private ProductService productService; // 自動注入 ProductService
    
    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.findAllProducts();
        products.forEach(product -> product.setPrice((int) product.getPrice())); // 將價格轉換為整數
        model.addAttribute("products", products);
        return "products/addproducts"; // 使用 addproducts.html 顯示產品列表
    }

    
    @GetMapping("/addproducts")
    public String showAddProductsPage() {
        return "products/addproducts"; // 返回 products/addproducts.html 頁面
    }
    
    @GetMapping("/shopcart")
    public String shopCart() {
        return "products/shopcart"; // 返回模板名稱
    }

    @GetMapping("/secindex")
    public String showSecIndex() {
        return "products/secindex"; // 返回 products/secindex.html 頁面
    }

    @GetMapping("/cookies")
    public String showCookies(Model model) {
        List<Product> cookies = productService.getProductsByCategory("軟餅乾");
        model.addAttribute("cookies", cookies);
        return "sec"; // 返回 sec.html 頁面
    }
    
    @GetMapping("/puddings")
    public String showPuddings(Model model) {
        List<Product> puddings = productService.getProductsByCategory("手做布丁");
        model.addAttribute("puddings", puddings);
        return "sec"; // 返回 sec.html 頁面
    }
    
    @GetMapping("/cakes")
    public String showCakes(Model model) {
        List<Product> cakes = productService.getProductsByCategory("蛋糕");
        model.addAttribute("cakes", cakes);
        return "sec"; // 返回 sec.html 頁面
    }
    
    @GetMapping("/ices")
    public String showIces(Model model) {
        List<Product> ices = productService.getProductsByCategory("冰淇淋聖代");
        model.addAttribute("ices", ices);
        return "sec"; // 返回 sec.html 頁面
    }
    
    @GetMapping("/shakes")
    public String showShakes(Model model) {
        List<Product> shakes = productService.getProductsByCategory("奶昔");
        model.addAttribute("shakes", shakes);
        return "sec"; // 返回 sec.html 頁面
    }
    
    @GetMapping("/teas")
    public String showTeas(Model model) {
        List<Product> teas = productService.getProductsByCategory("花果茶");
        model.addAttribute("teas", teas);
        return "sec"; // 返回 sec.html 頁面
    }
    
    @GetMapping("/coffees")
    public String showCoffees(Model model) {
        List<Product> coffees = productService.getProductsByCategory("咖啡");
        model.addAttribute("coffees", coffees);
        return "sec"; // 返回 sec.html 頁面
    }   
    
    @GetMapping("/sec")
    public String handleSecRequest(@RequestParam(required = false) String category, Model model, HttpSession session) {
        List<Product> products = productService.findAll();
        if (category != null) {
            products = products.stream()
                    .filter(product -> category.equals(product.getCategory()))
                    .collect(Collectors.toList());
            model.addAttribute("products", products);
            return "sec";
        }
        // 處理其他業務邏輯
        return "sec";
    }

    /**
     * [後台][查詢] 處理 GET 請求，顯示產品列表
     * 
     * @param model
     * @return
     */
    @GetMapping("/list")
    public String showProductList(Model model) {
        List<Product> products = productService.findAllProducts(); // 獲取所有產品
        model.addAttribute("products", products); // 添加產品列表到模型
        model.addAttribute("productDto", new ProductDto()); // 添加空的 productDto 到模型
        return "products/addproducts"; // 返回視圖名稱
    }


    /**
     * [後台]顯示創建產品頁面
     * 
     * @param model
     * @return
     */
    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDto productDto = new ProductDto(); // 創建新的 ProductDto
        model.addAttribute("productDto", productDto); // 將 ProductDto 添加到模型中
        return "products/CreateProduct"; // 返回視圖名稱
    }

    /**
     * [後台][新增] 處理創建產品的 POST 請求
     * 
     * @param productDto
     * @param result
     * @return
     */
    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute ProductDto productDto, // 驗證並綁定表單數據到 ProductDto
            BindingResult result) { // 綁定結果

        if (productDto.getImageFile().isEmpty()) { // 檢查圖片文件是否上傳
            result.addError(new FieldError("productDto", "imageFile", "請上傳商品圖片")); // 添加錯誤信息
        }

        if (result.hasErrors()) { // 如果有驗證錯誤
            return "products/CreateProduct"; // 返回創建產品頁面
        }

        productService.createProduct(productDto); // 調用服務層創建產品

        return "redirect:/products"; // 重定向到產品列表頁面
    }

    /**
     * [後台] 顯示編輯產品頁面
     * 
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam int id) {
        try {
            Product product = productService.findProductById(id); // 根據 ID 查找產品
            model.addAttribute("product", product); // 將產品添加到模型中

            ProductDto productDto = new ProductDto(); // 創建新的 ProductDto
            productDto.setName(product.getName()); // 設置產品名稱
            productDto.setCategory(product.getCategory()); // 設置產品類別
            productDto.setPrice(product.getPrice()); // 設置產品價格
            productDto.setCreatedAt(product.getCreatedAt()); // 設置產品創建日期
            productDto.setBase64Image(product.getBase64Image());

            model.addAttribute("productDtoId", product.getId()); // 將產品 ID 添加到模型中
            model.addAttribute("productDto", productDto); // 將 ProductDto 添加到模型中
            System.out.println(productDto);
        } catch (Exception ex) {
            System.out.println("Exception:" + ex.getMessage()); // 捕捉並打印異常
            return "redirect:/products/list"; // 發生錯誤時重定向到產品列表頁面
        }
        return "products/EditProduct"; // 返回視圖名稱
    }
    
    
    /**
     * [後台][修改] 處理編輯產品的 POST 請求
     * 
     * @param id
     * @param productDto
     * @param result
     * @param model
     * @return
     */
    @PostMapping("/edit/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable int id, 
            @Valid @ModelAttribute ProductDto productDto, 
            BindingResult result, 
            Model model) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        
        try {
            productService.updateProduct(id, productDto);
            return ResponseEntity.ok().body("Product updated successfully");
        } catch (Exception e) {
            e.printStackTrace(); // 輸出異常詳情到控制台
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update product");
        }
    }



    /**
     * [後台][刪除] 處理刪除產品的 GET 請求
     * 
     * @param id
     * @return
     */
    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {
        productService.deleteProduct(id); // 調用服務層刪除產品
        return "redirect:/products/list"; // 重定向到產品列表頁面
    }
    
    @GetMapping("/dailySummary")
    public String showDailySummary(
            @RequestParam(required = false) String date, 
            Model model) {
        LocalDate selectedDate = date != null ? LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")) : LocalDate.now();
        List<Object[]> dailySummary = productService.getDailySummaryByDate(selectedDate);

        if (dailySummary.isEmpty()) {
            model.addAttribute("noDataMessage", "查無銷售報告");
        }

        int totalQuantity = dailySummary.stream().mapToInt(summary -> (int) summary[1]).sum();
        int totalAmount = dailySummary.stream().mapToInt(summary -> ((Number) summary[2]).intValue()).sum();

        model.addAttribute("dailySummary", dailySummary);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("selectedDate", selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        return "products/dailySummary"; // 返回日結頁面模板名稱
    }


    @GetMapping("/exportDailySummary")
    public StreamingResponseBody exportDailySummaryToExcel(@RequestParam String date, HttpServletResponse response) {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=daily_summary_" + date + ".xlsx");

        LocalDate selectedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return outputStream -> {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Daily Summary");
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("產品名稱");
                headerRow.createCell(1).setCellValue("數量");
                headerRow.createCell(2).setCellValue("總金額");

                List<Object[]> dailySummary = productService.getDailySummaryByDate(selectedDate);
                int rowNum = 1;
                int totalQuantity = 0;
                int totalAmount = 0;

                for (Object[] summary : dailySummary) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue((String) summary[0]);
                    row.createCell(1).setCellValue((int) summary[1]);
                    row.createCell(2).setCellValue(((Number) summary[2]).intValue());
                    totalQuantity += (int) summary[1];
                    totalAmount += ((Number) summary[2]).intValue();
                }

                // 添加總計行
                Row totalRow = sheet.createRow(rowNum);
                totalRow.createCell(0).setCellValue("總計");
                totalRow.createCell(1).setCellValue(totalQuantity);
                totalRow.createCell(2).setCellValue(totalAmount);

                workbook.write(outputStream); // 將 workbook 寫入輸出流
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    
    @GetMapping("/top5Products")
    @ResponseBody
    public Map<String, Object> getTop5Products(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Object[]> top5Products = productService.getTop5ProductsByDate(date);
        Map<String, Object> response = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Integer> quantities = new ArrayList<>();

        for (Object[] product : top5Products) {
            labels.add((String) product[0]);
            quantities.add((Integer) product[1]);
        }

        response.put("labels", labels);
        response.put("quantities", quantities);
        return response;
    }

}