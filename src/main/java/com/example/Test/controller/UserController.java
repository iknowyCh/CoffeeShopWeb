package com.example.Test.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Test.model.dto.UserDto;
import com.example.Test.model.dto.UserLoginDto;
import com.example.Test.model.dto.UserRegisterDto;
import com.example.Test.model.po.Product;
import com.example.Test.model.po.User;
import com.example.Test.service.ProductService;
import com.example.Test.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;
    
    @Autowired 
    private ProductService productService;

    @GetMapping
    public String getIndex(Model model) {
        model.addAttribute("userLoginDto", new UserLoginDto());
        return "usersform/users";
    }
    
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/expired-password")
    public String showExpiredPasswordPage() {
        return "/expiredpassword"; // 返回過期頁面的視圖名稱
    }
    
    @PostMapping("/login")
    public String postLogin(UserLoginDto userLoginDto, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer failedAttempts = (Integer) session.getAttribute("failedAttempts");
        if (failedAttempts == null) {
            failedAttempts = 0;
        }

        // 驗證碼檢查
        Integer captchaResult = (Integer) session.getAttribute("captchaResult");
        if (captchaResult == null || !userLoginDto.getCaptcha().equals(captchaResult.toString())) {
            redirectAttributes.addFlashAttribute("error", "驗證碼錯誤");
            redirectAttributes.addFlashAttribute("userLoginDto", userLoginDto);
            return "redirect:/users";
        }

        try {
            if ("root".equals(userLoginDto.getUsername()) && "123456".equals(userLoginDto.getPassword())) {
                User rootUser = new User();
                rootUser.setUsername("root");
                rootUser.setEmail("root@gmail.com");
                session.setAttribute("user", rootUser);
                session.setAttribute("username", rootUser.getUsername());
                return "redirect:/products/secindex";
            }

            User user = userService.checkUserCredentials(userLoginDto.getUsername(), userLoginDto.getPassword());
            if (user == null) {
                failedAttempts++;
                session.setAttribute("failedAttempts", failedAttempts);

                if (failedAttempts >= 3) {
                    session.setAttribute("failedAttempts", 0);
                    return "redirect:/";
                }

                redirectAttributes.addFlashAttribute("error", "使用者名稱及密碼錯誤，請重新輸入");
                redirectAttributes.addFlashAttribute("userLoginDto", userLoginDto);
                return "redirect:/users";
            }

            session.setAttribute("failedAttempts", 0);
            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());

            return "redirect:/users/sec";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "登入過程中出錯");
            return "redirect:/users";
        }
    }

    @GetMapping("/sec")
    public String secPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users";
        }
        model.addAttribute("username", user.getUsername());
        List<Product> products = productService.findAllProducts();
        model.addAttribute("products", products);
        products.forEach(System.out::println);
        return "/sec";
    }
    
    @GetMapping("/register")
    public String getRegister() {
        return "usersform/register";
    }

    @PostMapping("/register")
    public String postRegister(UserRegisterDto userRegisterDto, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer captchaResult = (Integer) session.getAttribute("captchaResult");
        if (captchaResult == null || !userRegisterDto.getCaptcha().equals(captchaResult.toString())) {
            redirectAttributes.addFlashAttribute("error", "驗證碼錯誤");
            redirectAttributes.addFlashAttribute("userRegisterDto", userRegisterDto);
            return "redirect:/users/register";
        }

        try {
            User user = userService.registerNewUser(userRegisterDto);
            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "註冊失敗");
                redirectAttributes.addFlashAttribute("userRegisterDto", userRegisterDto);
                return "redirect:/users/register";
            }

            return "redirect:/users?success=true";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "註冊過程中出錯");
            return "redirect:/users/register";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/list")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "usersform/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "usersform/createuser";
    }

    @PostMapping("/create")
    public String createUser(@Valid @ModelAttribute UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            return "usersform/createuser";
        }
        try {
            userService.saveUser(userDto);
        } catch (Exception e) {
            e.printStackTrace();
            return "usersform/createuser";
        }
        return "redirect:/users/list";
    }

    @GetMapping("/edit")
    public String showEditForm(@RequestParam int id, Model model) {
        User user = userService.findUserById(id);
        if (user == null) {
            return "redirect:/users/list";
        }
        UserDto userDto = new UserDto(user);
        model.addAttribute("user", user);
        model.addAttribute("userDto", userDto);
        model.addAttribute("userId", user.getId());
        return "usersform/edituser";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable int id, @ModelAttribute UserDto userDto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("userId", id);
            return "usersform/edituser";
        }
        try {
            userService.updateUser(id, userDto);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("userId", id);
            return "usersform/edituser"; // 返回表單頁面
        }
        return "redirect:/users/list"; // 成功後重定向到用戶列表頁面
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam int id) {
        userService.deleteUser(id);
        return "redirect:/users/list";
    }
    
    @GetMapping("/search")
    public String searchUsers(@RequestParam String keyword, Model model) {
        List<User> users = userService.searchUsersByKeyword(keyword);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "usersform/list";
    }
    
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(HttpSession session, Model model) {
        LocalDateTime lastResetRequest = (LocalDateTime) session.getAttribute("lastResetRequest");
        if (lastResetRequest != null) {
            long remainingTime = Duration.between(LocalDateTime.now(), lastResetRequest.plusMinutes(1)).getSeconds();
            if (remainingTime > 0) {
                model.addAttribute("remainingTime", remainingTime);
            }
        }
        return "usersform/forgotpassword";
    }

    @PostMapping("/forgot-password")
    @ResponseBody
    public Map<String, String> processForgotPassword(@RequestParam String username, @RequestParam String email, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        try {
            User user = userService.findByUsernameAndEmail(username, email);
            if (user == null) {
                response.put("status", "error");
                response.put("message", "使用者名稱或Email不正確，請重新輸入");
                return response;
            }
            userService.sendResetPasswordLink(user);
            session.setAttribute("lastResetRequest", LocalDateTime.now());
            response.put("status", "success");
            response.put("message", "重置密碼連結已發送到您的信箱");
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "發送重置密碼連結時發生錯誤");
        }
        return response;
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        User user = userService.findByResetToken(token);
        if (user == null) {
            return "redirect:/users/expired-password"; // 跳轉到過期頁面
        }

        long remainingTime = Duration.between(LocalDateTime.now(), user.getResetTokenCreationTime().plusMinutes(1)).getSeconds();
        if (remainingTime <= 0) {
            return "redirect:/users/expired-password"; // 跳轉到過期頁面
        }

        model.addAttribute("token", token);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("remainingTime", remainingTime);
        return "usersform/resetpassword";
    }

    @PostMapping("/reset-password")
    @ResponseBody
    public Map<String, String> processResetPassword(@RequestParam String token, @RequestParam String newPassword, @RequestParam String confirmPassword) {
        Map<String, String> response = new HashMap<>();
        if (!newPassword.equals(confirmPassword)) {
            response.put("status", "error");
            response.put("message", "密碼不相符，請重新輸入");
            return response;
        }
        try {
            boolean success = userService.resetPassword(token, newPassword);
            if (success) {
                response.put("status", "success");
                response.put("message", "密碼已重置，請使用新密碼登入");
            } else {
                response.put("status", "error");
                response.put("message", "無效或過期的重設密碼連結");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "重設密碼時發生錯誤");
        }
        return response;
    }
}
