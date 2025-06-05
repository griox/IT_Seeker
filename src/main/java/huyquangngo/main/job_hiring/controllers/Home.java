package huyquangngo.main.job_hiring.controllers;

import huyquangngo.main.job_hiring.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Home {
    @GetMapping("/index")
    public String home(){
        return "index";
    }
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "success", required = false) String success,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Sai email hoặc mật khẩu.");
        }
        if (success != null) {
            model.addAttribute("success", success);
        }
        return "login"; // Trả về tên template: login.html
    }

    // Trang đăng ký
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register"; // register.html
    }
}
