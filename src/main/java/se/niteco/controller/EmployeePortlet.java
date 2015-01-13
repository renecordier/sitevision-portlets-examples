package se.niteco.controller;

import javax.portlet.*;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.niteco.model.Employee;
import se.niteco.service.EmployeeService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Portlet class of Niteco's employees
 */
@Controller
@RequestMapping(value="VIEW")
public class EmployeePortlet {
	
	@Autowired
	@Qualifier("employeeService")
	private EmployeeService service;
	
	private Map<String, String> errorMap;//error messages when adding or editing an employee
	private Map<String, String> valuesMap;//keeping values to show in add or edit employee
	
	@RenderMapping
	public String showEmployee(Model model, RenderRequest request, RenderResponse response){
	
		//Set add url
		PortletURL showAddUrl = response.createRenderURL();
		showAddUrl.setParameter("action", "showAdd");
		model.addAttribute("showAddUrl", showAddUrl);

		//Set edit url
		PortletURL editUrl = response.createRenderURL();
		editUrl.setParameter("action", "showEdit");
		model.addAttribute("editUrl", editUrl);

		//Set remove url
		PortletURL removeUrl = response.createActionURL();
		removeUrl.setParameter("action", "deleteEmployee");
		model.addAttribute("removeUrl", removeUrl);
		
		//Get list of employee
		List<Employee> lst = service.getEmployees();
		model.addAttribute("employees", lst);
		
		return "listEmployee";
	}
	
	@RenderMapping(params = "action=showAdd")
	public String showAdd(Model model, RenderRequest request, RenderResponse response){
		
		//Set url to model
		PortletURL actionUrl = response.createActionURL();
		actionUrl.setParameter("action", "insertEmployee");
		
		//Set cancel url
		PortletURL cancelUrl = response.createActionURL();
		cancelUrl.setParameter("action", "cancel");
		
		model.addAttribute("actionUrl", actionUrl);
		model.addAttribute("cancelUrl", cancelUrl);
		
		model.addAttribute("addPage", true);
		model.addAttribute("errors", errorMap);
		model.addAttribute("employee", valuesMap);
		
		return "addEditEmployee";
	}
	
	@ActionMapping(params = "action=insertEmployee")
	public void doAdd(ActionRequest request, ActionResponse response){
		String id = request.getParameter("employeeId");
		String name = request.getParameter("employeeName");
		String email = request.getParameter("employeeEmail");
		String team = request.getParameter("employeeTeam");
		String role = request.getParameter("employeeRole");
		String salary = request.getParameter("employeeSalary");
		
		errorMap = new HashMap<String, String>();
		if (name == null || name.trim().equalsIgnoreCase("")) {
			errorMap.put("name", "Please enter a valid name");
		}
		if (email == null || email.trim().equalsIgnoreCase("")) {
			errorMap.put("email", "Please enter a valid email");
		}
		if (team == null || team.trim().equalsIgnoreCase("")) {
			errorMap.put("team", "Please enter a valid team");
		}
		if (role == null || role.trim().equalsIgnoreCase("")) {
			errorMap.put("role", "Please enter a valid role");
		}
		if (salary == null || salary.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(salary)) {
			errorMap.put("salary", "Please enter a valid salary");
		}
		if (id == null || id.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(id)) {
			errorMap.put("id", "Please enter a valid id number");
		} else {
			if (!service.isIdUnique(Integer.parseInt(id))) {
				errorMap.put("id", "Id number not unique ! Please enter a valid id number");
			}
		}
		
		if (errorMap.isEmpty()) { 
			service.addEmployee(new Employee(Integer.parseInt(id), name, email, team, role, Integer.parseInt(salary)));
		} else {
			// contains property name to property value map, for re-rendering
			// the form with values that were entered by the user for each form field
			valuesMap = new HashMap<String, String>();
			valuesMap.put("name", name);
			valuesMap.put("email", email);
			valuesMap.put("team", team);
			valuesMap.put("role", role);
			valuesMap.put("salary", salary);
			valuesMap.put("id", id);
			response.setRenderParameter("action", "showAdd");
		}	
	}
	
	@RenderMapping(params = "action=showEdit")
	public String showEdit(Model model, RenderRequest request, RenderResponse response){

		//Set url to model
		PortletURL actionUrl = response.createActionURL();
		actionUrl.setParameter("action", "updateEmployee");
		
		//Set cancel url
		PortletURL cancelUrl = response.createActionURL();
		cancelUrl.setParameter("action", "cancel");
				
		model.addAttribute("actionUrl", actionUrl);
		model.addAttribute("cancelUrl", cancelUrl);
		
		//Get selected employee
		String employeeId = request.getParameter("employeeId");
		if (employeeId != null) {
			int id = Integer.parseInt(employeeId);
			Employee employee = service.getEmployee(id);
			valuesMap = new HashMap<String, String>();
			valuesMap.put("name", employee.getName());
			valuesMap.put("email", employee.getEmail());
			valuesMap.put("team", employee.getTeam());
			valuesMap.put("role", employee.getRole());
			valuesMap.put("salary", employee.getSalary()+"");
			valuesMap.put("id", employee.getId()+"");
		}
		
		model.addAttribute("addPage", false);
		model.addAttribute("errors", errorMap);
		model.addAttribute("employee", valuesMap);
		
		return "addEditEmployee";
	}
	
	@ActionMapping(params = "action=updateEmployee")
	public void doEdit(ActionRequest request, ActionResponse response){
		String id = request.getParameter("employeeId");
		String name = request.getParameter("employeeName");
		String email = request.getParameter("employeeEmail");
		String team = request.getParameter("employeeTeam");
		String role = request.getParameter("employeeRole");
		String salary = request.getParameter("employeeSalary");
		
		errorMap = new HashMap<String, String>();
		
		if (name == null || name.trim().equalsIgnoreCase("")) {
			errorMap.put("name", "Please enter a valid name");
		}
		if (email == null || email.trim().equalsIgnoreCase("")) {
			errorMap.put("email", "Please enter a valid email");
		}
		if (team == null || team.trim().equalsIgnoreCase("")) {
			errorMap.put("team", "Please enter a valid team");
		}
		if (role == null || role.trim().equalsIgnoreCase("")) {
			errorMap.put("role", "Please enter a valid role");
		}
		if (salary == null || salary.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(salary)) {
			errorMap.put("salary", "Please enter a valid salary");
		}
		if (id == null || id.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(id)) {
			errorMap.put("id", "Please enter a valid id number");
		}
		
		if (errorMap.isEmpty()) { 
			service.updateEmployee(new Employee(Integer.parseInt(id), name, email, team, role, Integer.parseInt(salary)));
		} else {
			// contains property name to property value map, for re-rendering
			// the form with values that were entered by the user for each form field
			valuesMap = new HashMap<String, String>();
			valuesMap.put("name", name);
			valuesMap.put("email", email);
			valuesMap.put("team", team);
			valuesMap.put("role", role);
			valuesMap.put("salary", salary);
			valuesMap.put("id", id);
			response.setRenderParameter("action", "showEdit");
		}		
	}
	
	@ActionMapping(params = "action=deleteEmployee")
	public void doRemove(ActionRequest request){
		String id = request.getParameter("employeeId");
		if (id != null) { //delete action
			service.removeEmployee(Integer.parseInt(id));
		}
	}
	
	@ActionMapping(params = "action=cancel")
	public void doCancel(ActionRequest request){
		errorMap = new HashMap<String, String>();
		valuesMap = new HashMap<String, String>();
	}
	
	/**
	 * The rendering method of the portlet.
	 * 
	 * @param request 	the rendering request
	 * @param response	the rendering response
	 * @throws PortletException
	 * @throws IOException
	 *//*
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException{
		String veloPage = "";
		boolean addPage = false;
		if(request.getParameter("status") == null || request.getParameter("status").equals("default") ){
			//default view
			request.setAttribute("employees", employeeService.getEmployees());
			veloPage = "listEmployee.vm";
		}
		else {
			if(request.getParameter("status").equals("add")){
				addPage = true;
				request.setAttribute("addPage", addPage);
				request.setAttribute("errors", errorMap);
				request.setAttribute("employee", valuesMap);
				veloPage = "addEditEmployee.vm";// delegate the view into addEmployee.vm
			}
			else if(request.getParameter("status").equals("edit")){
				String employeeId = request.getParameter("employeeId");
				if (employeeId != null) {
					int id = Integer.parseInt(employeeId);
					Employee employee = employeeService.getEmployee(id);
					errorMap = new HashMap<String, String>();
					valuesMap = new HashMap<String, String>();
					valuesMap.put("name", employee.getName());
					valuesMap.put("email", employee.getEmail());
					valuesMap.put("team", employee.getTeam());
					valuesMap.put("role", employee.getRole());
					valuesMap.put("salary", employee.getSalary()+"");
					valuesMap.put("id", employee.getId()+"");
				}
				request.setAttribute("addPage", addPage);
				request.setAttribute("errors", errorMap);
				request.setAttribute("employee", valuesMap);
				veloPage = "addEditEmployee.vm";// delegate the view into editEmployee.vm
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
	 * @throws PortletException
	 * @throws IOException
	 *//*
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException{
		String action = request.getParameter("action");
		if (action == null) { //for delete and edit actions i put an other param, so i can use the value as the id
			String delete = request.getParameter("delete");
			if (delete != null) { //delete action
				int employeeId = Integer.parseInt(delete);
				employeeService.removeEmployee(employeeId);
				response.setRenderParameter("status", "default");
			}
			String edit = request.getParameter("edit");
			if (edit != null) { //edit action
				response.setRenderParameter("status", "edit");
				response.setRenderParameter("employeeId", edit);
			}
		} else { //if action
			if (action.equals("addPage")) //if we want to add an employee
				response.setRenderParameter("status", "add");
			else if (action.equals("editEmployeeAction")) { //if we want to edit an employee
				errorMap = new HashMap<String, String>();
				valuesMap = new HashMap<String, String>();
				boolean success = editEmployeeAction(request, response);
				if (success) {
					response.setRenderParameter("status", "default");
				} else {
					response.setRenderParameter("status", "edit");
				}
			}
			else if (action.equals("addEmployeeAction")) { //if we register an employee
				errorMap = new HashMap<String, String>();
				valuesMap = new HashMap<String, String>();
				boolean success = addEmployeeAction(request, response);
				if (success) {
					response.setRenderParameter("status", "default");
				} else {
					response.setRenderParameter("status", "add");
				}
			} else if (action.equals("cancelAction")) { //cancel
				errorMap = new HashMap<String, String>();
				valuesMap = new HashMap<String, String>();
				response.setRenderParameter("status", "default");
			} else {//default view
				response.setRenderParameter("status", "default");
			}
		}
	}
	
	/**
	 * This method checks if all the data of an employee are valid and then allow to update 
	 * the employee in the data object.
	 * 
	 * @param request
	 * @param response
	 * @return true if the registration of the employee is successfull, false otherwise
	 * @throws PortletException
	 * @throws IOException
	 *//*
	private boolean editEmployeeAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
		String id = request.getParameter("employeeId");
		String name = request.getParameter("employeeName");
		String email = request.getParameter("employeeEmail");
		String team = request.getParameter("employeeTeam");
		String role = request.getParameter("employeeRole");
		String salary = request.getParameter("employeeSalary");
		
		if (name == null || name.trim().equalsIgnoreCase("")) {
			errorMap.put("name", "Please enter a valid name");
		}
		if (email == null || email.trim().equalsIgnoreCase("")) {
			errorMap.put("email", "Please enter a valid email");
		}
		if (team == null || team.trim().equalsIgnoreCase("")) {
			errorMap.put("team", "Please enter a valid team");
		}
		if (role == null || role.trim().equalsIgnoreCase("")) {
			errorMap.put("role", "Please enter a valid role");
		}
		if (salary == null || salary.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(salary)) {
			errorMap.put("salary", "Please enter a valid salary");
		}
		if (id == null || id.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(id)) {
			errorMap.put("id", "Please enter a valid id number");
		}
		
		if (errorMap.isEmpty()) { 
			employeeService.updateEmployee(new Employee(Integer.parseInt(id), name, email, team, role, Integer.parseInt(salary)));
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
	
	/**
	 * This method checks if all the data of an employee are valid and then allow to register 
	 * the employee in the data object.
	 * 
	 * @param request
	 * @param response
	 * @return true if the registration of the employee is successfull, false otherwise
	 * @throws PortletException
	 * @throws IOException
	 *//*
	private boolean addEmployeeAction(ActionRequest request, ActionResponse response) throws PortletException, IOException{
		String id = request.getParameter("employeeId");
		String name = request.getParameter("employeeName");
		String email = request.getParameter("employeeEmail");
		String team = request.getParameter("employeeTeam");
		String role = request.getParameter("employeeRole");
		String salary = request.getParameter("employeeSalary");
		
		if (name == null || name.trim().equalsIgnoreCase("")) {
			errorMap.put("name", "Please enter a valid name");
		}
		if (email == null || email.trim().equalsIgnoreCase("")) {
			errorMap.put("email", "Please enter a valid email");
		}
		if (team == null || team.trim().equalsIgnoreCase("")) {
			errorMap.put("team", "Please enter a valid team");
		}
		if (role == null || role.trim().equalsIgnoreCase("")) {
			errorMap.put("role", "Please enter a valid role");
		}
		if (salary == null || salary.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(salary)) {
			errorMap.put("salary", "Please enter a valid salary");
		}
		if (id == null || id.trim().equalsIgnoreCase("") || !StringUtils.isNumeric(id)) {
			errorMap.put("id", "Please enter a valid id number");
		} else {
			if (!employeeService.isIdUnique(Integer.parseInt(id))) {
				errorMap.put("id", "Id number not unique ! Please enter a valid id number");
			}
		}
		
		if (errorMap.isEmpty()) { 
			employeeService.addEmployee(new Employee(Integer.parseInt(id), name, email, team, role, Integer.parseInt(salary)));
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
	}*/
	
}
