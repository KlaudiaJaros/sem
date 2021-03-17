package com.napier.sem;

/**
 * Department class to store information about a department.
 */
public class Department {
    private String deptNo;
    private String deptName;
    private Employee manager;

    /**
     * Gets department number.
     * @return department number
     */
    public String getDeptNo() {
        return deptNo;
    }

    /**
     * Sets department number
     * @param deptNo number to be set
     */
    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    /**
     * Gets department name.
     * @return department name
     */
    public String getDeptName() {
        return deptName;
    }

    /**
     * Sets department name
     * @param deptName name to be set
     */
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * Gets the department manager
     * @return Employee object representing the department manager
     */
    public Employee getManager() {
        return manager;
    }

    /**
     * Set the department manager
     * @param manager Employee object to be set as the dept manager
     */
    public void setManager(Employee manager) {
        this.manager = manager;
    }
}
