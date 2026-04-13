package com.example.EmployeeApiCrud.mvc;

import com.example.EmployeeApiCrud.Model.Employee;
import com.example.EmployeeApiCrud.dto.EmpRequest;
import com.example.EmployeeApiCrud.service.EmpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final EmpService empService;

    public LoginController(EmpService empService) {
        this.empService = empService;
    }

    // SETUP — only accessible when no employees exist
    @GetMapping("/setup")
    public String setupPage(HttpSession session) {
        if (session.getAttribute("loggedInEmployee") != null) {
            return "redirect:/";
        }
        return "setup";
    }

    @PostMapping("/setup")
    public String setup(@RequestParam String emp_name,
                        @RequestParam String empCode,
                        @RequestParam String password,
                        @RequestParam(required = false) String emp_city,
                        HttpSession session,
                        Model model) {
        try {
            EmpRequest req = new EmpRequest(emp_name, null, 0, emp_city);
            req.setEmpCode(empCode);
            req.setPassword(password);
            Employee employee = empService.createEmployee(req);
            session.setAttribute("loggedInEmployee", employee);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "setup";
        }
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("loggedInEmployee") != null) {
            return "redirect:/";
        }
        if (empService.getAllEmployees().isEmpty()) {
            return "redirect:/setup";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String empCode,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            Employee employee = empService.login(empCode, password);
            session.setAttribute("loggedInEmployee", employee);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/ui/profile")
    public String profilePage(HttpSession session, Model model) {
        Employee loggedIn = (Employee) session.getAttribute("loggedInEmployee");
        model.addAttribute("employee", loggedIn);
        model.addAttribute("currentPage", "profile");
        return "profile";
    }

    @PostMapping("/ui/profile")
    public String updateProfile(@RequestParam(required = false) String emp_name,
                                @RequestParam(required = false) Float emp_salary,
                                @RequestParam(required = false, defaultValue = "0") int emp_age,
                                @RequestParam(required = false) String emp_city,
                                HttpSession session,
                                RedirectAttributes ra) {
        try {
            Employee loggedIn = (Employee) session.getAttribute("loggedInEmployee");
            EmpRequest req = new EmpRequest(emp_name, emp_salary, emp_age, emp_city);
            Employee updated = empService.updateProfile(loggedIn.getEmpid(), req);
            session.setAttribute("loggedInEmployee", updated);
            ra.addFlashAttribute("success", "Profile updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/profile";
    }

    @GetMapping("/ui/change-password")
    public String changePasswordPage(Model model) {
        model.addAttribute("currentPage", "change-password");
        return "change-password";
    }

    @PostMapping("/ui/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session,
                                 RedirectAttributes ra) {
        try {
            if (!newPassword.equals(confirmPassword)) {
                ra.addFlashAttribute("error", "New password and confirm password do not match");
                return "redirect:/ui/change-password";
            }
            Employee loggedIn = (Employee) session.getAttribute("loggedInEmployee");
            empService.updatePassword(loggedIn.getEmpid(), currentPassword, newPassword);
            session.invalidate();
            ra.addFlashAttribute("success", "Password changed successfully. Please login again.");
            return "redirect:/login";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/ui/change-password";
        }
    }
}
