package com.napier.sem;

import java.sql.*;

public class App
{
    /*
    Connection to MySQL database
     */
    private static Connection con = null;

    /**
     * Connect to the MySQL database.
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
                System.out.println("Failed to connect to database attempt " + Integer.toString(i));
                System.out.println(sqle.getMessage());
            }
            catch (InterruptedException ie)
            {
                System.out.println("Thread interrupted? Should not happen.");
            }
        }
    }

    /*
    Disconnect from the mySQL database
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

    public static void main(String[] args)
    {
        // Create a new app:
        App a = new App();

        //connect to database:
        a.connect();

        // Get Employee
        Employee emp = a.getEmployee(255530);
        // Display results
        a.displayEmployee(emp);

        // disconnect:
        a.disconnect();
    }
}