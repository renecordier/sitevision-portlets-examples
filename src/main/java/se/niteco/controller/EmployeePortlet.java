package se.niteco.controller;

import javax.jcr.Node;
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
import se.niteco.service.EmployeeServiceImpl;
import senselogic.sitevision.api.Utils;
import senselogic.sitevision.api.context.PortletContextUtil;
import senselogic.sitevision.api.metadata.MetadataUtil;
import senselogic.sitevision.api.property.PropertyUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
	
	protected final static String C_EMPLOYEES_LIST = "employeesList";
	
	protected final Gson gson = new Gson();
    
    private final Type employeesType =  new TypeToken<ArrayList<Employee>>() {}.getType();
    
    private boolean init = true;
	
	protected void loadEmployeesList(PortletRequest request) { 
		String employeesJSON = null;
		
		Utils utils = (Utils)request.getAttribute("sitevision.utils");
        PortletContextUtil pcUtil = utils.getPortletContextUtil();
        MetadataUtil metaUtil = utils.getMetadataUtil();
        PropertyUtil propertyUtil = utils.getPropertyUtil();
        
        Node currentPage = pcUtil.getCurrentPage();
        
        employeesJSON = propertyUtil.getString(currentPage, C_EMPLOYEES_LIST);
        
        if (employeesJSON != null && employeesJSON.trim().length() > 0) {
            try {
            	service.setEmployees((List<Employee>) gson.fromJson(employeesJSON, employeesType));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        	service = new EmployeeServiceImpl();
        }
	}
	
	protected void saveEmployeesList(PortletRequest request) throws Exception {
        Utils utils = (Utils)request.getAttribute("sitevision.utils");
        PortletContextUtil pcUtil = utils.getPortletContextUtil();
        MetadataUtil metaUtil = utils.getMetadataUtil();
        Node currentPage = null;
        currentPage = pcUtil.getCurrentPage();
        String plop = null;
        plop = gson.toJson(service.getEmployees());
        
        metaUtil.setMetadataPropertyValue(currentPage, C_EMPLOYEES_LIST, plop);
    }
	
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
        /*if (!model.containsAttribute(C_EMPLOYEES_LIST)) {
            loadEmployeesList(request);            
            model.addAttribute(C_EMPLOYEES_LIST, service);
        } else {
        	service = (EmployeeService)model.asMap().get(C_EMPLOYEES_LIST);
        }*/
		if (init) {
			loadEmployeesList(request); 
			init = false;
		}
		
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
			try {
				saveEmployeesList(request);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			try {
				saveEmployeesList(request);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			try {
				saveEmployeesList(request);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@ActionMapping(params = "action=cancel")
	public void doCancel(ActionRequest request){
		errorMap = new HashMap<String, String>();
		valuesMap = new HashMap<String, String>();
	}
	
}
