package com.napier.sem;

import com.napier.sem.App;
import com.napier.sem.Employee;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class AppTest {
static App app;

    @BeforeAll
    static void init(){
        app=new App();
    }

    @Test
    void printSalariesTestNull(){ // passing just null
        app.printSalaries(null);
    }

    @Test
    void printSalariesTestEmpty() // passing a null ArrayList
    {
        ArrayList<Employee> employees = new ArrayList<>();
        app.printSalaries(employees);
    }
    @Test
    void printSalariesTestContainsNull() // passing an ArrayList with a null object in it
    {
        ArrayList<Employee> employees = new ArrayList<>();
        employees.add(null);
        app.printSalaries(employees);
    }
    @Test
    void printSalaries() // normal condition
    {
        ArrayList<Employee> employees = new ArrayList<>();
        Employee emp = new Employee();
        emp.emp_no = 1;
        emp.first_name = "Kevin";
        emp.last_name = "Chalmers";
        emp.title = "Engineer";
        emp.salary = 55000;
        employees.add(emp);
        app.printSalaries(employees);
    }
}
