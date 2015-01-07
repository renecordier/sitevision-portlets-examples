package se.niteco.client;

import javax.portlet.*;

import org.apache.commons.lang.StringUtils;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

import se.niteco.controller.EmployeeService;
import se.niteco.controller.EmployeeServiceImpl;
import se.niteco.model.Employee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Portlet class of Niteco's employees
 */
public class EmployeePortlet extends GenericVelocityPortlet {
	
	private static EmployeeService employeeService;
	private Map<String, String> errorMap;//error messages when adding or editing an employee
	private Map<String, String> valuesMap;//keeping values to show in add or edit employee
	
	public void init() {
		employeeService = new EmployeeServiceImpl(this.getPortletContext());
		errorMap = new HashMap<String, String>(); 
		valuesMap = new HashMap<String, String>();  
	}

	public static EmployeeService getBookService() {
		return employeeService;
	}
	
	/**
	 * The rendering method of the portlet.
	 * 
	 * @param request 	the rendering request
	 * @param response	the rendering response
	 */
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException{
		String veloPage = "";
		if(request.getParameter("status") == null || request.getParameter("status").equals("default") ){
			//default view
			request.setAttribute("employees", employeeService.getEmployees());
			veloPage = "listEmployee.vm";
		}
		else {
			if(request.getParameter("status").equals("add")){
				request.setAttribute("errors", errorMap);
				request.setAttribute("employee", valuesMap);
				veloPage = "addEmployee.vm";// delegate the view into addEmployee.vm
			}
			else if(request.getParameter("status").equals("edit")){
				veloPage = "editEmployee.vm";// delegate the view into editEmployee.vm
			}
		}
		getPortletContext().getRequestDispatcher(response.encodeURL("/velocity/" + veloPage))
				.include(request, response);
	}
	
	/**
	 * The action method of the portlet.
	 * 
	 * @param request	the action request
	 * @param response	the response request 
	 */
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException{
		String action = request.getParameter("action");
		if (action.equals("addPage")) //if we want to add an employee
			response.setRenderParameter("status", "add");
		else if (action.equals("editPage")) //if we want to edit an employee
			response.setRenderParameter("status", "edit");
		else if (action.equals("addEmployeeAction")) {
			errorMap = new HashMap<String, String>();
			valuesMap = new HashMap<String, String>();
			boolean success = addEmployeeAction(request, response);
			if (success) {
				response.setRenderParameter("status", "default");
			} else {
				response.setRenderParameter("status", "add");
			}
		} else if (action.equals("cancelAction")) {
			errorMap = new HashMap<String, String>();
			valuesMap = new HashMap<String, String>();
			response.setRenderParameter("status", "default");
		} else //default view
			response.setRenderParameter("status", "default");
	}
	
	private boolean addEmployeeAction(ActionRequest request, ActionResponse response) throws PortletException, IOException{
		String id = request.getParameter("employeeId");
		String name = request.getParameter("employeeName");
		String email = request.getParameter("employeeEmail");
		String team = request.getParameter("employeeTeam");
		String role = request.getParameter("employeeRole");
		String salary = request.getParameter("employeeSalary");
		
		if (name == null || name.trim().equalsIgnoreCase("")) {
			errorMap.put("name", "Please enter employee's name");
		}
		if (email == null || email.trim().equalsIgnoreCase("")) {
			errorMap.put("email", "Please enter employee's email");
		}
		if (team == null || team.trim().equalsIgnoreCase("")) {
			errorMap.put("team", "Please enter employee's team");
		}
		if (role == null || role.trim().equalsIgnoreCase("")) {
			errorMap.put("role", "Please enter employee's role");
		}
		if (salary == null || salary.trim().equalsIgnoreCase("")) {
			errorMap.put("salary", "Please enter employee's salary");
		}
		if (id == null || id.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(id)) {
			errorMap.put("id", "Please enter a valid id number");
		} else {
			if (!employeeService.isIdUnique(Integer.parseInt(id))) {
				errorMap.put("id", "Id number not unique ! Please enter a valid id number");
			}
		}
		
		if (errorMap.isEmpty()) { 
			employeeService.addEmployee(new Employee(Integer.parseInt(id), name, email, team, role, salary));
			return true;
		} else {
			request.setAttribute("errors", errorMap);
			// contains property name to property value map, for re-rendering
			// the form with values that were entered by the user for each form field
			valuesMap = new HashMap<String, String>();
			valuesMap.put("name", name);
			valuesMap.put("email", email);
			valuesMap.put("team", team);
			valuesMap.put("role", role);
			valuesMap.put("salary", salary);
			valuesMap.put("id", id);
			return false;
		}		
	}
	
}
