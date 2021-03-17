package com.napier.sem;

import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;

public class App
{
    /**
     * Connection to MySQL database
     */
    private static Connection con = null;

    /**
     * Connects to the MySQL database.
     */
    public void connect()
    {
        try
        {
            // Load Database driver
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Could not load SQL driver");
            System.exit(-1);
        }

        int retries = 10;
        for (int i = 0; i < retries; ++i)
        {
            System.out.println("Connecting to database...");
            try
            {
                // Wait a bit for db to start
                Thread.sleep(30000);
                // Connect to database
                con = DriverManager.getConnection("jdbc:mysql://db:3306/employees?useSSL=false", "root", "example");
                System.out.println("Successfully connected");
                break;
            }
            catch (SQLException sqle)
            {
                System.out.println("Failed to connect to database attempt " + i);
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /**
     * Disconnects from the mySQL database
     */
    public void disconnect(){
        if (con != null)
        {
            try
            {
                // Close connection
                con.close();
            }
            catch (Exception e)
            {
                System.out.println("Error closing connection to database");
            }
        }
    }

    /**
     * Retrieves an employee of a given ID number from the database.
     * @param ID ID of an employee to retrieve
     * @return an Employee or null if no such employee exists/connection was unsuccessful
     */
    public Employee getEmployee(int ID)
    {
        try
        {
            // Create an SQL statement
            Statement stmt = con.createStatement();
            // Create string for SQL statement
            String strSelect =
                    "SELECT E.emp_no, E.first_name, E.last_name, T.title, T.to_date, S.salary, S.to_date, "
                            + "D.dept_name, DM.emp_no, DM.to_date, E2.first_name, E2.last_name "
                            + "FROM employees E "
                            + "JOIN titles T ON (E.emp_no=T.emp_no)"
                            + "JOIN salaries S ON (E.emp_no=S.emp_no)"
                            + "JOIN dept_emp DE ON (E.emp_no=DE.emp_no)"
                            + "JOIN departments D ON (DE.dept_no=D.dept_no)"
                            + "JOIN dept_manager DM ON (D.dept_no=DM.dept_no)"
                            + "JOIN employees E2 On (DM.emp_no=E2.emp_no)"
                            + "WHERE E.emp_no = " + ID
                            + " ORDER BY T.to_date DESC, S.to_date DESC, DM.to_date DESC"
                            + " LIMIT 1";
            // Execute SQL statement
            ResultSet rset = stmt.executeQuery(strSelect);

            // Return new employee if valid.
            // Check one is returned
            if (rset.next())
            {
                Employee emp = new Employee();
                emp.emp_no = rset.getInt("E.emp_no");
                emp.first_name = rset.getString("E.first_name");
                emp.last_name = rset.getString("E.last_name");
                emp.title=rset.getString("T.title");
                emp.salary=rset.getInt("S.salary");
                emp.dept_name=rset.getString("D.dept_name");
                emp.manager=rset.getString("E2.first_name") + " " + rset.getString("E2.last_name");
                return emp;
            }
            else
                return null;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get employee details");
            return null;
        }
    }

    /**
     * Prints an instance of an Employee to the console.
     * @param emp Employee to print
     */
    public void displayEmployee(Employee emp)
    {
        if (emp != null)
        {
            System.out.println(
                    emp.emp_no + " "
                            + emp.first_name + " "
                            + emp.last_name + "\n"
                            + emp.title + "\n"
                            + "Salary:" + emp.salary + "\n"
                            + emp.dept_name + "\n"
                            + "Manager: " + emp.manager + "\n");
        }
        else{
            System.out.println("employee null");
        }
    }

    /**
     * Gets a list of all employees (id, names, salary) saved in the employee database.
     * @return A list of employees or null if unsuccessful/database is empty.
     */
    public ArrayList<Employee> getAllEmployees()
    {
        try{
            // Create an SQL statement:
            Statement stmt = con.createStatement();
            // Create a string for the SQL statement:
            String query = "SELECT e.emp_no, e.first_name, e.last_name, s.salary " +
                            "FROM employees e " +
                            "JOIN salaries s ON (e.emp_no=s.emp_no) " +
                            "WHERE s.to_date='9999-01-01' " +
                            "ORDER BY e.emp_no ASC ";

            // Execute SQL stmt:
            ResultSet rset = stmt.executeQuery(query);

            // Extract employee information:
            ArrayList<Employee> employees = new ArrayList<>();
            while(rset.next()){
                Employee e = new Employee();
                e.emp_no=rset.getInt("e.emp_no");
                e.first_name=rset.getString("e.first_name");
                e.last_name=rset.getString("e.last_name");
                e.salary=rset.getInt("s.salary");
                employees.add(e);
            }
            return employees;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to load employee details.");
            return null;
        }
    }

    /**
     * Prints a list of employees and their salaries.
     * @param employees The list of employees to print.
     */
    public void printSalaries(ArrayList<Employee> employees)
    {
        if(employees==null){
            System.out.println("No employees");
            return;
        }
        // Print header
        System.out.println(String.format("%-10s %-15s %-20s %-8s", "Emp No", "First Name", "Last Name", "Salary"));
        // Loop over all employees in the list
        for (Employee emp : employees)
        {
            if (emp==null)
                continue;
            String emp_string =
                    String.format("%-10s %-15s %-20s %-8s",
                            emp.emp_no, emp.first_name, emp.last_name, emp.salary);
            System.out.println(emp_string);
        }
    }

    /**
     * Gets a list of employees with a given title and their salaries
     * @param title title
     * @return a list of employees with the given title and their salaries
     */
    public ArrayList<Employee> getSalariesByRole(String title)
    {
        try{
            // Create an SQL statement:
            Statement stmt = con.createStatement();
            // Create a string for the SQL statement:
            String query = "SELECT e.emp_no, e.first_name, e.last_name, s.salary " +
                    "FROM employees e " +
                    "JOIN salaries s ON (e.emp_no=s.emp_no) " +
                    "JOIN titles t ON (e.emp_no=t.emp_no) " +
                    "WHERE s.to_date='9999-01-01' AND t.to_date='9999-01-01' AND t.title='" + title + "' " +
                    "ORDER BY e.emp_no ASC ";

            // Execute SQL stmt:
            ResultSet rset = stmt.executeQuery(query);

            // Extract employee information:
            ArrayList<Employee> employees = new ArrayList<>();
            while(rset.next()){
                Employee e = new Employee();
                e.emp_no=rset.getInt("e.emp_no");
                e.first_name=rset.getString("e.first_name");
                e.last_name=rset.getString("e.last_name");
                e.salary=rset.getInt("s.salary");
                employees.add(e);
            }
            return employees;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to load employee details.");
            return null;
        }
    }

    /**
     * Gets department by name
     * @param dept_name name
     * @return Department object
     */
    public Department getDepartment(String dept_name){
        try{
            // Create an SQL statement:
            Statement stmt = con.createStatement();
            // Create a string for the SQL statement:
            String query = "SELECT d.dept_no, d.dept_name, dm.emp_no " +
                    "FROM departments d " +
                    "JOIN dept_manager dm ON (d.dept_no=dm.dept_no) " +
                    "WHERE dm.to_date='9999-01-01' AND d.dept_name='" + dept_name +"'";

            // Execute SQL stmt:
            ResultSet rset = stmt.executeQuery(query);

            // Extract employee information:
            Department dp = new Department();
            int employeeNo=0;
            while(rset.next()){
                dp.setDeptNo(rset.getString("d.dept_no"));
                dp.setDeptName(rset.getString("d.dept_name"));
                employeeNo=rset.getInt("dm.emp_no");
            }
            Employee manager = getEmployee(employeeNo);
            dp.setManager(manager);
            return dp;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to load department details.");
            return null;
        }
    }

    /**
     * Gets a list of employees in a given department and their salaries
     * @param department department
     * @return a list of employees in the given department and their salaries
     */
    public ArrayList<Employee> getSalariesByDepartment(Department department)
    {
        try{
            // Create an SQL statement:
            Statement stmt = con.createStatement();
            // Create a string for the SQL statement:
            String query = "SELECT e.emp_no, e.first_name, e.last_name, s.salary " +
                    "FROM employees e " +
                    "JOIN salaries s ON (e.emp_no=s.emp_no) " +
                    "JOIN dept_emp d ON (e.emp_no=d.emp_no) " +
                    "WHERE s.to_date='9999-01-01' AND d.dept_no='" + department.getDeptNo() + "' " +
                    "ORDER BY e.emp_no ASC ";

            // Execute SQL stmt:
            ResultSet rset = stmt.executeQuery(query);

            // Extract employee information:
            ArrayList<Employee> employees = new ArrayList<>();
            while(rset.next()){
                Employee e = new Employee();
                e.emp_no=rset.getInt("e.emp_no");
                e.first_name=rset.getString("e.first_name");
                e.last_name=rset.getString("e.last_name");
                e.salary=rset.getInt("s.salary");
                employees.add(e);
            }
            return employees;
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to load employee details.");
            return null;
        }
    }

    public static void main(String[] args)
    {
        // Create a new app:
        App a = new App();

        //connect to database:
        a.connect();

        // Get an Employee:
        /*Employee emp = a.getEmployee(255530);
        // Display results
        a.displayEmployee(emp); */

        // Salary report:
        /* ArrayList<Employee> employees = a.getAllEmployees();
        System.out.println("The size of employees array: " + employees.size()); */

        // Salary by role:
        /*ArrayList<Employee> employees = a.getSalariesByRole("Engineer");
        a.printSalaries(employees);*/

        Department dept = a.getDepartment("Sales");
        ArrayList<Employee> employees = a.getSalariesByDepartment(dept);
        a.printSalaries(employees);

        // disconnect:
        a.disconnect();


    }
}