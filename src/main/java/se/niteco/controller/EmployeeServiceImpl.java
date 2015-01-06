package se.niteco.controller;

import java.util.List;

import javax.portlet.PortletContext;

import se.niteco.data.EmployeeDataObject;
import se.niteco.model.Employee;

/**
 * Implementation of the employee service to manage employees
 */
public class EmployeeServiceImpl implements EmployeeService {
	private PortletContext context;
	
	public EmployeeServiceImpl(PortletContext context) {
		this.context = context;
	}

	public List<Employee> getEmployees() {
		EmployeeDataObject employeeList = (EmployeeDataObject) context.getAttribute("employeeList");
		return employeeList.getEmployees();
	}

	public void addEmployee(Employee employee) {
		

	}

	public boolean isIdUnique(int id) {
		
		return false;
	}

	public Employee getEmployee(int id) {
		
		return null;
	}

	public void removeEmployee(int id) {
		

	}

	public List<Employee> searchEmployees(String name) {
		
		return null;
	}

}
