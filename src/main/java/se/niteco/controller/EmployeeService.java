package se.niteco.controller;

import java.util.List;

import se.niteco.model.Employee;

/**
 * Interface EmployeeService. Interface of all the basic services for managing employees.
 */
public interface EmployeeService {
	List<Employee> getEmployees();
	void addEmployee(Employee employee);
	boolean isIdUnique (int id);
	Employee getEmployee(int id);
	void removeEmployee(int id);
	List<Employee> searchEmployees(String name);
}
