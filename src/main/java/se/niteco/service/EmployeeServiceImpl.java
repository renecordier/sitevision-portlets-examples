package se.niteco.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.portlet.PortletContext;

import org.springframework.stereotype.Service;

import se.niteco.model.Employee;

/**
 * Implementation of the employee service to manage employees
 */
@Service(value="employeeService")
public class EmployeeServiceImpl implements EmployeeService {
	
	private List<Employee> employeeList = Collections.synchronizedList(new ArrayList<Employee>());
	
	public EmployeeServiceImpl() {
		/*employeeList.add(new Employee(1, "Khoi", "khoi@niteco.se", "RxEye", "Team leader", 1000));
		employeeList.add(new Employee(2, "Rene", "rene@niteco.se", "SiteVision", "Java dev", 500));
		employeeList.add(new Employee(3, "Xon", "xon@niteco.se", "SiteVision", "Java dev", 500));
		employeeList.add(new Employee(4, "Calvin", "calvin@niteco.se", "PMs", "PMO", 3000));*/
	}

	
	public List<Employee> getEmployees() {
		return this.employeeList;
	}

	public void addEmployee(Employee employee) {
		this.employeeList.add(employee);
	}

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

	public void removeEmployee(int id) {
		employeeList.remove(getEmployee(id));
	}

	public List<Employee> searchEmployees(String name) {
		//todo
		return null;
	}

	public void updateEmployee(Employee employee) {
		int index = getEmployeeIndex(employee.getId());
		employeeList.set(index, employee);
	}

	public int getEmployeeIndex(int id) {
		int index;
		for (index = 0; index < employeeList.size(); index++) {
			if(employeeList.get(index).getId() == id)
				break;
		}
		return index;
	}

	public void setEmployees(List<Employee> employees) {
		this.employeeList = employees;
	}

}
