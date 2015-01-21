package se.niteco.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
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
			request.getPortletSession().setAttribute("cities", cityServ.getCities(), PortletSession.APPLICATION_SCOPE);
			init = false;
		}
      	List<City> lst = cityServ.getCities();
      	model.addAttribute("cities", lst);
      	
      	model.addAttribute("mode", "view");
      	
		return "listCities";
	}
	
	@RenderMapping(params = "action=showAdd")
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
		if (init) {
			loadCityList(request); 
			init = false;
		}
      	List<City> lst = cityServ.getCities();
      	model.addAttribute("cities", lst);
      	
      	model.addAttribute("mode", "add");
      	
      	int idNew = cityServ.getNewCityId();
      	model.addAttribute("idNew", idNew);
      	
		return "listCities";
	}
	
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
		request.getPortletSession().setAttribute("cities", cityServ.getCities(), PortletSession.APPLICATION_SCOPE);
	}
	
	@RenderMapping(params = "action=showEdit")
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
		if (init) {
			loadCityList(request); 
			init = false;
		}
      	List<City> lst = cityServ.getCities();
      	model.addAttribute("cities", lst);
		
		//Get selected city
		String cityId = request.getParameter("cityId");
		model.addAttribute("idEdit", Integer.parseInt(cityId));
		
		model.addAttribute("mode", "edit");

		return "listCities";
	}
	
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
		
		request.getPortletSession().setAttribute("cities", cityServ.getCities(), PortletSession.APPLICATION_SCOPE);
	}
	
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
			
			request.getPortletSession().setAttribute("cities", cityServ.getCities(), PortletSession.APPLICATION_SCOPE);
		}
	}
	
	@ActionMapping(params = "action=cancel")
	public void doCancel(ActionRequest request){
		
	}
}
