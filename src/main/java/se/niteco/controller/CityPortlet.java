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

<<<<<<< HEAD
import se.niteco.communication.CityListSender;
=======
import se.niteco.jms.CitySender;
import se.niteco.jms.CitySenderImpl;
>>>>>>> spring
import se.niteco.model.City;
import se.niteco.service.CityService;
import se.niteco.service.CityServiceImpl;
import senselogic.sitevision.api.Utils;
import senselogic.sitevision.api.context.PortletContextUtil;
import senselogic.sitevision.api.metadata.MetadataUtil;
import senselogic.sitevision.api.property.PropertyUtil;

/**
 * Public class of the city portlet
 */
@Controller
@RequestMapping(value="VIEW")
public class CityPortlet {
	@Autowired
	@Qualifier("cityService")
	private CityService cityServ; //DI of city service
	
	@Autowired
	@Qualifier("citySender")
	private static CitySender citySender; //DI of city sender
	
	protected final static String META_CITIES_LIST = "cityList"; //metadata name
	
	protected final Gson gson = new Gson();
    
    private final Type citiesType =  new TypeToken<ArrayList<City>>() {}.getType();
    
    private boolean init = true;
    
<<<<<<< HEAD
    private VelocityEngine velocityEngine;
    
    @Autowired
    private CityListSender cityListSender;
=======
    private VelocityEngine velocityEngine; // DI of velocity engine
>>>>>>> spring
    
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
     * Load the list of cities from sitevision's metadatas
     * @param request
     */
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
	
    /**
     * Save the list of cities in sitevision's metadatas
     * @param request
     * @throws Exception
     */
	protected void saveCityList(PortletRequest request) throws Exception {
        Utils utils = (Utils)request.getAttribute("sitevision.utils");
        PortletContextUtil pcUtil = utils.getPortletContextUtil();
        MetadataUtil metaUtil = utils.getMetadataUtil();
        Node currentPage = pcUtil.getCurrentPage();
        
        metaUtil.setMetadataPropertyValue(currentPage, META_CITIES_LIST, gson.toJson(cityServ.getCities()));
    }
	
	/**
	 * Default view mode of city portlet, showing the list of cities
	 * @param model
	 * @param request
	 * @param response
	 * @param pref
	 * @return
	 */
	@RenderMapping
	public String showCity(Model model, RenderRequest request, RenderResponse response, PortletPreferences pref){
		//Set add url
		PortletURL showAddUrl = response.createRenderURL();
		showAddUrl.setParameter("action", "showAddCity");
		model.addAttribute("showAddUrl", showAddUrl);

		//Set edit url
		PortletURL editUrl = response.createRenderURL();
		editUrl.setParameter("action", "showEditCity");
		model.addAttribute("editUrl", editUrl);

		//Set remove url
		PortletURL removeUrl = response.createActionURL();
		removeUrl.setParameter("action", "deleteCity");
		model.addAttribute("removeUrl", removeUrl);

		//Get list of employee
		if (init) {
			System.out.println("init");
			loadCityList(request); 
<<<<<<< HEAD
			//request.getPortletSession().setAttribute("cities", cityServ.getCities(), PortletSession.APPLICATION_SCOPE);
			cityListSender.sendCityList(gson.toJson(cityServ.getCities()));
=======
			citySender = new CitySenderImpl();
			citySender.sendCities(cityServ.getCities());
>>>>>>> spring
			init = false;
		}
      	List<City> lst = cityServ.getCities();
      	model.addAttribute("cities", lst);
      	model.addAttribute("request", request);
      	model.addAttribute("mode", "view");
      	
		return "listCities";
	}
	
	/**
	 * View to add a city in the list
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RenderMapping(params = "action=showAddCity")
	public String showAdd(Model model, RenderRequest request, RenderResponse response){
		//Set insert url
		PortletURL insertCityUrl = response.createActionURL();
		insertCityUrl.setParameter("action", "insertCity");
		model.addAttribute("insertCityUrl", insertCityUrl);
		
		//Set cancel url
		PortletURL cancelUrl = response.createActionURL();
		cancelUrl.setParameter("action", "cancel");
		model.addAttribute("cancelUrl", cancelUrl);
		
		//Get list of employee
      	List<City> lst = cityServ.getCities();
      	model.addAttribute("cities", lst);
      	
      	model.addAttribute("mode", "add");
      	
      	int idNew = cityServ.getNewCityId();
      	model.addAttribute("idNew", idNew);
      	model.addAttribute("request", request);
      	
		return "listCities";
	}
	
	/**
	 * Action to insert a city in the list
	 * @param request
	 * @param response
	 */
	@ActionMapping(params = "action=insertCity")
	public void doAdd(ActionRequest request, ActionResponse response){
		String id = request.getParameter("addId");
		String name = request.getParameter("addName");
		
		cityServ.addCity(new City(Integer.parseInt(id), name));
		
		try {
			saveCityList(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
<<<<<<< HEAD
		//request.getPortletSession().setAttribute("cities", cityServ.getCities(), PortletSession.APPLICATION_SCOPE);
		cityListSender.sendCityList(gson.toJson(cityServ.getCities()));
=======
		citySender.sendCities(cityServ.getCities());
>>>>>>> spring
	}
	
	/**
	 * View to edit a city in the list
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RenderMapping(params = "action=showEditCity")
	public String showEdit(Model model, RenderRequest request, RenderResponse response){

		//Set url to model
		PortletURL updateCityUrl = response.createActionURL();
		updateCityUrl.setParameter("action", "updateCity");
		
		//Set cancel url
		PortletURL cancelUrl = response.createActionURL();
		cancelUrl.setParameter("action", "cancel");
				
		model.addAttribute("updateCityUrl", updateCityUrl);
		model.addAttribute("cancelUrl", cancelUrl);
		
		//Get list of employee
      	List<City> lst = cityServ.getCities();
      	model.addAttribute("cities", lst);
		
		//Get selected city
		String cityId = request.getParameter("cityId");
		model.addAttribute("idEdit", Integer.parseInt(cityId));
		model.addAttribute("request", request);
		model.addAttribute("mode", "edit");

		return "listCities";
	}
	
	/**
	 * Action to update a city from the list
	 * @param request
	 * @param response
	 */
	@ActionMapping(params = "action=updateCity")
	public void doEdit(ActionRequest request, ActionResponse response){
		String id = request.getParameter("idUpdate");
		String name = request.getParameter("nameUpdate");
		
		cityServ.updateCity(new City(Integer.parseInt(id), name));
		try {
			saveCityList(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
<<<<<<< HEAD
		//request.getPortletSession().setAttribute("cities", cityServ.getCities(), PortletSession.APPLICATION_SCOPE);
		cityListSender.sendCityList(gson.toJson(cityServ.getCities()));
=======
		citySender.sendCities(cityServ.getCities());
>>>>>>> spring
	}
	
	/**
	 * Action to delete a city from the list
	 * @param request
	 * @param response
	 */
	@ActionMapping(params = "action=deleteCity")
	public void doRemove(ActionRequest request, ActionResponse response){
		String cityId = request.getParameter("cityId");
		if (cityId != null) { //delete action
			cityServ.removeCity(Integer.parseInt(cityId));
			try {
				saveCityList(request);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
<<<<<<< HEAD
			//request.getPortletSession().setAttribute("cities", cityServ.getCities(), PortletSession.APPLICATION_SCOPE);
			cityListSender.sendCityList(gson.toJson(cityServ.getCities()));
=======
			citySender.sendCities(cityServ.getCities());
>>>>>>> spring
		}
	}
	
	/**
	 * Cancel action
	 * @param request
	 */
	@ActionMapping(params = "action=cancel")
	public void doCancel(ActionRequest request){
		
	}
}
