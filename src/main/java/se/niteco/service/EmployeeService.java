package se.niteco.service;

import java.util.List;

import se.niteco.model.Employee;

/**
 * Interface EmployeeService. Interface of all the basic services for managing employees.
 */
public interface EmployeeService {
	List<Employee> getEmployees();
	void setEmployees(List<Employee> employees);
	void addEmployee(Employee employee);
	boolean isIdUnique (int id);
	Employee getEmployee(int id);
	void removeEmployee(int id);
	List<Employee> searchEmployees(String name);
	void updateEmployee(Employee employee);
	int getEmployeeIndex(int id);
	
}
