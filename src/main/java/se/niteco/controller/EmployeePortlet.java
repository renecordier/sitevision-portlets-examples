package se.niteco.controller;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.jcr.Node;
import javax.portlet.*;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import se.niteco.model.City;
import se.niteco.model.Employee;
import se.niteco.service.CityService;
import se.niteco.service.CityServiceImpl;
import se.niteco.service.EmployeeService;
import se.niteco.service.EmployeeServiceImpl;
import senselogic.sitevision.api.Utils;
import senselogic.sitevision.api.context.PortletContextUtil;
import senselogic.sitevision.api.metadata.MetadataUtil;
import senselogic.sitevision.api.property.PropertyUtil;
import senselogic.sitevision.api.render.ImageRenderer;
import senselogic.sitevision.api.resource.ResourceLocatorUtil;
import senselogic.sitevision.api.webresource.ImageUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Portlet class of Niteco's employees
 */
@Controller("employeePortlet")
@RequestMapping(value="VIEW")
public class EmployeePortlet {
	
	@Autowired
	@Qualifier("employeeService")
	private EmployeeService service; //DI of employee service
	
	private static CityService cityServ; //city service
	
	private Map<String, String> errorMap;//error messages when adding or editing an employee
	private Map<String, String> valuesMap;//keeping values to show in add or edit employee
	
	protected final static String META_EMPLOYEES_LIST = "employeeList";//metadata name 
	
	protected final Gson gson = new Gson();
    
    private final Type employeesType =  new TypeToken<ArrayList<Employee>>() {}.getType();
    
    private boolean init = true;
    
    private VelocityEngine velocityEngine; //velocity engine
    
    private final String UPLOAD_FOLDER = "/home/rene/Pictures/sitevision"; //folder to store temporary pictures on the server
	
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
    
    /**
     * Consuming messages from city portlet to get an updated list of the cities
     * an employee can work in.
     * @param cities
     */
    public void handleCitiesAlert (List<City> cities) {
    	System.out.println("Receiver invoked...");
    	if (EmployeePortlet.cityServ == null)
    		EmployeePortlet.cityServ = new CityServiceImpl();
    	EmployeePortlet.cityServ.setCities(cities);
    	System.out.println("Going out of Receiver...Bye");
    }
	
    /**
     * Loading the list of employees from the metadata
     * @param request
     */
	protected void loadEmployeesList(PortletRequest request) { 
		String employeesJSON = null;
		service = new EmployeeServiceImpl();
		
		Utils utils = (Utils)request.getAttribute("sitevision.utils");
        PortletContextUtil pcUtil = utils.getPortletContextUtil();
        PropertyUtil propertyUtil = utils.getPropertyUtil();
        
        Node currentPage = pcUtil.getCurrentPage();
       
        employeesJSON = propertyUtil.getString(currentPage, META_EMPLOYEES_LIST);
        System.out.println(employeesJSON);
        
        if (employeesJSON != null && employeesJSON.trim().length() > 0) {
            try {
            	service.setEmployees((List<Employee>) gson.fromJson(employeesJSON, employeesType));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } 
	}
	
	/**
	 * Saving the list of employees in the metadata
	 * @param request
	 * @throws Exception
	 */
	protected void saveEmployeesList(PortletRequest request) throws Exception {
        Utils utils = (Utils)request.getAttribute("sitevision.utils");
        PortletContextUtil pcUtil = utils.getPortletContextUtil();
        MetadataUtil metaUtil = utils.getMetadataUtil();
        Node currentPage = pcUtil.getCurrentPage();
        
        metaUtil.setMetadataPropertyValue(currentPage, META_EMPLOYEES_LIST, gson.toJson(service.getEmployees()));
    }
	
	/**
	 * Deafult view mode to show the list of employees
	 * @param model
	 * @param request
	 * @param response
	 * @param pref
	 * @return
	 */
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
		removeUrl.setParameter("action", "deleteEmployee");
		model.addAttribute("removeUrl", removeUrl);
		
		//set edit pic url
		PortletURL editPicUrl = response.createRenderURL();
		editPicUrl.setParameter("action", "editPic");
		model.addAttribute("editPicUrl", editPicUrl);

		//Get list of employee
		if (init) {
			//cityReceiver = new CityReceiver();
			loadEmployeesList(request); 
			if (EmployeePortlet.cityServ == null)
        		EmployeePortlet.cityServ = new CityServiceImpl();
			init = false;
		}
		
      	List<Employee> lst = service.getEmployees();
      	model.addAttribute("employees", lst);
      	model.addAttribute("request", request);
      	String mode = pref.getValue("mode", "View");
		model.addAttribute("mode", mode);
		return "listEmployee";
	}
	
	/**
	 * View for editing an employee's picture
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RenderMapping(params = "action=editPic")
	public String editPic(RenderRequest request, RenderResponse response, Model model){
		String id = (String) request.getParameter("employeeId");
		Employee emp = service.getEmployee(Integer.parseInt(id));
		//set change image url
		PortletURL updateImageUrl = response.createActionURL();
		updateImageUrl.setParameter("action", "changeImage");
		
		//Set cancel url
		PortletURL cancelUrl = response.createActionURL();
		cancelUrl.setParameter("action", "cancel");
		model.addAttribute("cancelUrl", cancelUrl);
		
		model.addAttribute("employeeId", id);
		model.addAttribute("employee", emp);
		model.addAttribute("updateImageUrl", updateImageUrl);
		model.addAttribute("request", request);
		
		return "imageForm";
	}
	
	/**
	 * View for adding an employee to the list
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
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
		
		//get list of cities
		List<City> lst = EmployeePortlet.cityServ.getCities();
      	model.addAttribute("cities", lst);
      	model.addAttribute("request", request);
		
		return "addEditEmployee";
	}
	
	/**
	 * Action for adding an employee to the list
	 * @param request
	 * @param response
	 */
	@ActionMapping(params = "action=insertEmployee")
	public void doAdd(ActionRequest request, ActionResponse response){
		String id = request.getParameter("employeeId");
		String name = request.getParameter("employeeName");
		String email = request.getParameter("employeeEmail");
		String team = request.getParameter("employeeTeam");
		String role = request.getParameter("employeeRole");
		String salary = request.getParameter("employeeSalary");
		String cityId = request.getParameter("citySelect");
		
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
			City city = EmployeePortlet.cityServ.getCity(Integer.parseInt(cityId));
			service.addEmployee(new Employee(Integer.parseInt(id), name, email, team, role, Integer.parseInt(salary), city));
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
	
	/**
	 * View for editing en employee
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
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
		
		//get list of cities
		List<City> lst = EmployeePortlet.cityServ.getCities();
      	model.addAttribute("cities", lst);
      	model.addAttribute("request", request);
		
		return "addEditEmployee";
	}
	
	/**
	 * Action for updating an employee in the list
	 * @param request
	 * @param response
	 */
	@ActionMapping(params = "action=updateEmployee")
	public void doEdit(ActionRequest request, ActionResponse response){
		String id = request.getParameter("employeeId");
		String name = request.getParameter("employeeName");
		String email = request.getParameter("employeeEmail");
		String team = request.getParameter("employeeTeam");
		String role = request.getParameter("employeeRole");
		String salary = request.getParameter("employeeSalary");
		String cityId = request.getParameter("citySelect");
		
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
			City city = EmployeePortlet.cityServ.getCity(Integer.parseInt(cityId));
			service.updateEmployee(new Employee(Integer.parseInt(id), name, email, team, role, Integer.parseInt(salary), city));
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
	
	/**
	 * Action of deleting an employee
	 * @param request
	 */
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
	
	/**
	 * Cancel action
	 * @param request
	 */
	@ActionMapping(params = "action=cancel")
	public void doCancel(ActionRequest request){
		errorMap = new HashMap<String, String>();
		valuesMap = new HashMap<String, String>();
	}
	
	/**
	 * Action to change the image of an employee
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@ActionMapping(params = "action=changeImage")
	public void updateImage(ActionRequest request, ActionResponse response) 
			throws Exception{
		String id = (String) request.getParameter("id");
		
		File inputFile = (File) request.getAttribute("file");
		String format = getFormatName(inputFile);
		if (format.equals("jpeg"))
			format = "jpg";
		File outputFile = new File(UPLOAD_FOLDER + "/" + id + "." + format);
		
		BufferedImage img = ImageIO.read(inputFile);
		ImageIO.write(img, format, outputFile);
		
		Utils utils = (Utils) request.getAttribute("sitevision.utils");
		
		
		//Upload image to sitevision
		ImageUtil imageUtil = utils.getImageUtil();
		ResourceLocatorUtil rlUtil = utils.getResourceLocatorUtil();
		Node imgNode = rlUtil.getImageRepository();
		
		boolean hasNode = imgNode.hasNode(id + "." + format);
		if(hasNode == false){
			imageUtil.createImage(imgNode, id + "." + format, "file://" + outputFile.getPath());
		}
		else{
			imageUtil.updateBinaryContent(imgNode.getNode(id + "." + format), "file://" + outputFile.getPath());
		}
		
		Node imgLink = imgNode.getNode(id + "." + format);
		
		ImageRenderer imgRender = utils.getImageRenderer();
		imgRender.setImage(imgLink);
		imgRender.setDescription(id);
		
		//Update image to employee
		Employee emp = service.getEmployee(Integer.parseInt(id));
		emp.setPicture(imgRender.render());		
		service.updateEmployee(emp);
		
		outputFile.delete();
		
		try {
			saveEmployeesList(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the extension name of an image
	 * @param o
	 * @return
	 */
	private static String getFormatName(Object o) {
	    try {
	      ImageInputStream iis = ImageIO.createImageInputStream(o);
	      Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
	      if (!iter.hasNext()) {
	        return null;
	      }
	      ImageReader reader = (ImageReader) iter.next();
	      iis.close();

	      return reader.getFormatName().toLowerCase();
	    } catch (IOException e) {
	    }
	    return null;
	}
}
