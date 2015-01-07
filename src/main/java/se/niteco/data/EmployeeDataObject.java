package se.niteco.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import se.niteco.model.Employee;

/**
 * Data object employee. A kind of static homemade database.
 */
public class EmployeeDataObject {
	private List<Employee> employees = Collections.synchronizedList(new ArrayList<Employee>());
	
	public EmployeeDataObject() {
		employees.add(new Employee(1, "Khoi", "khoi@niteco.se", "RxEye", "Team leader", "1000"));
		employees.add(new Employee(2, "Rene", "rene@niteco.se", "SiteVision", "Java dev", "500"));
		employees.add(new Employee(3, "Xon", "xon@niteco.se", "SiteVision", "Java dev", "500"));
		employees.add(new Employee(4, "Calvin", "calvin@niteco.se", "PMs", "PMO", "3000"));
	}
	
	public List<Employee> getEmployees() {
		return employees;
	}
}
