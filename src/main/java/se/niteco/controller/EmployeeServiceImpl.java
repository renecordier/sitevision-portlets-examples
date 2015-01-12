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
		EmployeeDataObject employeeList = (EmployeeDataObject) context.getAttribute("employeeList");
		employeeList.getEmployees().add(employee);
	}

	public boolean isIdUnique(int id) {
		EmployeeDataObject employeeList = (EmployeeDataObject) context.getAttribute("employeeList");
		boolean isUnique = true;
		for(Employee employee : employeeList.getEmployees()) {
			if(employee.getId() == id) {
				isUnique = false;
				break;
			}
		}
		return isUnique;
	}

	public Employee getEmployee(int id) {
		EmployeeDataObject employeeList = (EmployeeDataObject) context.getAttribute("employeeList");
		Employee matchingEmployee = null;
		for(Employee employee : employeeList.getEmployees()) {
			if(employee.getId() == id) {
				matchingEmployee = employee;
				break;
			}
		}
		return matchingEmployee;
	}

	public void removeEmployee(int id) {
		EmployeeDataObject employeeList = (EmployeeDataObject) context.getAttribute("employeeList");
		employeeList.getEmployees().remove(getEmployee(id));
	}

	public List<Employee> searchEmployees(String name) {
		//todo
		return null;
	}

	public void updateEmployee(Employee employee) {
		EmployeeDataObject employeeList = (EmployeeDataObject) context.getAttribute("employeeList");
		int index = getEmployeeIndex(employee.getId());
		employeeList.getEmployees().set(index, employee);
	}

	public int getEmployeeIndex(int id) {
		EmployeeDataObject employeeList = (EmployeeDataObject) context.getAttribute("employeeList");
		int index;
		for (index = 0; index < employeeList.getEmployees().size(); index++) {
			if(employeeList.getEmployees().get(index).getId() == id)
				break;
		}
		return index;
	}

}
