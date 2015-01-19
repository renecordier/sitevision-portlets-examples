package se.niteco.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import se.niteco.model.City;
import se.niteco.service.CityService;
import se.niteco.service.CityServiceImpl;
import senselogic.sitevision.api.Utils;
import senselogic.sitevision.api.context.PortletContextUtil;
import senselogic.sitevision.api.metadata.MetadataUtil;
import senselogic.sitevision.api.property.PropertyUtil;

@Controller
@RequestMapping(value="VIEW")
public class CityPortlet {
	@Autowired
	@Qualifier("cityService")
	private CityService cityServ;
	
	protected final static String META_CITIES_LIST = "cityList";
	
	protected final Gson gson = new Gson();
    
    private final Type citiesType =  new TypeToken<ArrayList<City>>() {}.getType();
    
    private boolean init = true;
    
    private VelocityEngine velocityEngine; 
    
    /**
     * @param velocityEngine the velocityEngine to set
     */
    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    /**
     * @return the velocityEngine
     */
    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }
    
    protected void loadCityList(PortletRequest request) { 
		String cityJSON = null;
		cityServ = new CityServiceImpl();
		
		Utils utils = (Utils)request.getAttribute("sitevision.utils");
        PortletContextUtil pcUtil = utils.getPortletContextUtil();
        PropertyUtil propertyUtil = utils.getPropertyUtil();
        
        Node currentPage = pcUtil.getCurrentPage();
       
        cityJSON = propertyUtil.getString(currentPage, META_CITIES_LIST);
        System.out.println(cityJSON);
        
        if (cityJSON != null && cityJSON.trim().length() > 0) {
            try {
            	cityServ.setCities((List<City>) gson.fromJson(cityJSON, citiesType));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
	}
	
	protected void saveCityList(PortletRequest request) throws Exception {
        Utils utils = (Utils)request.getAttribute("sitevision.utils");
        PortletContextUtil pcUtil = utils.getPortletContextUtil();
        MetadataUtil metaUtil = utils.getMetadataUtil();
        Node currentPage = pcUtil.getCurrentPage();
        
        metaUtil.setMetadataPropertyValue(currentPage, META_CITIES_LIST, gson.toJson(cityServ.getCities()));
    }
	
	@RenderMapping
	public String showEmployee(Model model, RenderRequest request, RenderResponse response, PortletPreferences pref){
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
		removeUrl.setParameter("action", "deleteCity");
		model.addAttribute("removeUrl", removeUrl);

		//Get list of employee
		if (init) {
			loadCityList(request); 
			init = false;
		}
      	List<City> lst = cityServ.getCities();
      	model.addAttribute("cities", lst);
      	
		return "listCities";
	}
	
	@RenderMapping(params = "action=showAdd")
	public String showAdd(Model model, RenderRequest request, RenderResponse response){
		/*
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
		
		return "addEditEmployee";*/
		return "listCities";
	}
	
	@ActionMapping(params = "action=insertCity")
	public void doAdd(ActionRequest request, ActionResponse response){
		/*String id = request.getParameter("employeeId");
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
		}*/	
	}
	
	@RenderMapping(params = "action=showEdit")
	public String showEdit(Model model, RenderRequest request, RenderResponse response){
/*
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
		
		return "addEditEmployee";*/
		return "listCities";
	}
	
	@ActionMapping(params = "action=updateCity")
	public void doEdit(ActionRequest request, ActionResponse response){
		/*String id = request.getParameter("employeeId");
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
		}*/		
	}
	
	@ActionMapping(params = "action=deleteCity")
	public void doRemove(ActionRequest request){
		/*String id = request.getParameter("employeeId");
		if (id != null) { //delete action
			service.removeEmployee(Integer.parseInt(id));
			try {
				saveEmployeesList(request);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
	
	@ActionMapping(params = "action=cancel")
	public void doCancel(ActionRequest request){
		/*errorMap = new HashMap<String, String>();
		valuesMap = new HashMap<String, String>();*/
	}
}
