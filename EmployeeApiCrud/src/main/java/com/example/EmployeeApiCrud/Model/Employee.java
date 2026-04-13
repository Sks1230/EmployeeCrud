package com.example.EmployeeApiCrud.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "emp")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long empid;

    @Column(name = "emp_name")
    private String emp_name;

    @Column(name = "emp_salary")
    private Float emp_salary;

    @Column(name = "emp_age")
    private int emp_age;

    @Column(name = "emp_city")
    private String emp_city;

    @Column(name = "emp_code", unique = true, length = 4)
    private String empCode;

    @Column(name = "password")
    private String password;

    public Long getEmpid() {
        return empid;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public Float getEmp_salary() {
        return emp_salary;
    }

    public int getEmp_age() {
        return emp_age;
    }

    public String getEmp_city() {
        return emp_city;
    }

    public void setEmpid(Long empid) {
        this.empid = empid;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public void setEmp_salary(Float emp_salary) {
        this.emp_salary = emp_salary;
    }

    public void setEmp_age(int emp_age) {
        this.emp_age = emp_age;
    }

    public void setEmp_city(String emp_city) {
        this.emp_city = emp_city;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
