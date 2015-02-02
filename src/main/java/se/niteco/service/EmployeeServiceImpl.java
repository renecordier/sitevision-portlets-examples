package se.niteco.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.niteco.service.EmployeeService;

import org.springframework.stereotype.Service;

import se.niteco.model.Employee;

/**
 * Implementation of the employee service to manage employees
 */
@Service(value="employeeService")
public class EmployeeServiceImpl implements EmployeeService {
	//List of employees
	private List<Employee> employeeList = Collections.synchronizedList(new ArrayList<Employee>());
	
	/**
	 * Constructor
	 */
	public EmployeeServiceImpl() {
		
	}

	/**
	 * Get the list of employees
	 * @return employeeList
	 */
	public List<Employee> getEmployees() {
		return this.employeeList;
	}

	/**
	 * Add an employee in the list
	 * @param employee
	 */
	public void addEmployee(Employee employee) {
		this.employeeList.add(employee);
	}

	/**
	 * Search if the id is unique or not
	 * @param id
	 * @return isUnique
	 */
	public boolean isIdUnique(int id) {
		boolean isUnique = true;
		for(Employee employee : this.employeeList) {
			if(employee.getId() == id) {
				isUnique = false;
				break;
			}
		}
		return isUnique;
	}

	/**
	 * Return an employee giving his id
	 * @param id
	 * @return matchingEmployee
	 */
	public Employee getEmployee(int id) {
		Employee matchingEmployee = null;
		for(Employee employee : employeeList) {
			if(employee.getId() == id) {
				matchingEmployee = employee;
				break;
			}
		}
		return matchingEmployee;
	}

	/**
	 * Remove an employee with its given id 
	 */
	public void removeEmployee(int id) {
		employeeList.remove(getEmployee(id));
	}

	/**
	 * Search employees
	 * Not implemented yet...
	 */
	public List<Employee> searchEmployees(String name) {
		//todo
		return null;
	}

	/**
	 * Update a given employee
	 * @param employee
	 */
	public void updateEmployee(Employee employee) {
		int index = getEmployeeIndex(employee.getId());
		employeeList.set(index, employee);
	}

	/**
	 * Get the index of an employee in the list
	 * @param id
	 * @return index
	 */
	public int getEmployeeIndex(int id) {
		int index;
		for (index = 0; index < employeeList.size(); index++) {
			if(employeeList.get(index).getId() == id)
				break;
		}
		return index;
	}

	/**
	 * Set a list of employees
	 * @param employees
	 */
	public void setEmployees(List<Employee> employees) {
		this.employeeList = employees;
	}

}
