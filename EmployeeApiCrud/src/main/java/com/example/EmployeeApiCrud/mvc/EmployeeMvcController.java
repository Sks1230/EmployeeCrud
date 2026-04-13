package com.example.EmployeeApiCrud.mvc;

import com.example.EmployeeApiCrud.Model.Employee;
import com.example.EmployeeApiCrud.dto.EmpRequest;
import com.example.EmployeeApiCrud.service.EmpService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ui/employees")
public class EmployeeMvcController {

    private final EmpService empService;

    public EmployeeMvcController(EmpService empService) {
        this.empService = empService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("currentPage", "employees");
        model.addAttribute("employees", empService.getAllEmployees());
        return "employee/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("currentPage", "employees");
        model.addAttribute("employee", new Employee());
        return "employee/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("currentPage", "employees");
            model.addAttribute("employee", empService.getEmployeeById(id));
            return "employee/form";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/ui/employees";
        }
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) String emp_name,
                       @RequestParam(required = false) Float emp_salary,
                       @RequestParam(required = false, defaultValue = "0") int emp_age,
                       @RequestParam(required = false) String emp_city,
                       @RequestParam(required = false) String empCode,
                       @RequestParam(required = false) String password,
                       RedirectAttributes ra) {
        try {
            EmpRequest req = new EmpRequest(emp_name, emp_salary, emp_age, emp_city);
            req.setEmpCode(empCode);
            req.setPassword(password);
            empService.createEmployee(req);
            ra.addFlashAttribute("success", "Employee created successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/employees";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @RequestParam(required = false) String emp_name,
                         @RequestParam(required = false) Float emp_salary,
                         @RequestParam(required = false, defaultValue = "0") int emp_age,
                         @RequestParam(required = false) String emp_city,
                         @RequestParam(required = false) String empCode,
                         RedirectAttributes ra) {
        try {
            EmpRequest req = new EmpRequest(emp_name, emp_salary, emp_age, emp_city);
            req.setEmpCode(empCode);
            empService.updateEmployee(id, req);
            ra.addFlashAttribute("success", "Employee updated successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/employees";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            empService.deleteEmployee(id);
            ra.addFlashAttribute("success", "Employee deleted successfully");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/employees";
    }
}
