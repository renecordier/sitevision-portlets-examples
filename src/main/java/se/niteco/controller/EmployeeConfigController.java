package se.niteco.controller;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * Portlet class of the configuration controller
 */
@Controller
@RequestMapping(value="CONFIG")
public class EmployeeConfigController {
	//DI of velocity engine
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
	
    /**
     * Default render view of the configuration controller
     * @param request
     * @param response
     * @param model
     * @param pref
     * @return
     * @throws PortletException
     * @throws IOException
     */
	@RenderMapping
	public String doConfig(RenderRequest request, RenderResponse response, Model model, PortletPreferences pref) 
			throws PortletException, IOException{
		System.out.println("Configuration Mode");

		//Set configuration URL
		PortletURL configUrl = response.createActionURL();
		model.addAttribute("configUrl", configUrl);
		configUrl.setWindowState(WindowState.MINIMIZED);
		
		return "configView";
	}
	
	/**
	 * Action of saving configuration
	 * @param request
	 * @param response
	 * @param session
	 * @param pref
	 * @throws PortletException
	 * @throws IOException
	 */
	@ActionMapping
	public void saveConfig(ActionRequest request, ActionResponse response, PortletSession session, PortletPreferences pref)
			throws PortletException, IOException{
		
		pref.setValue("mode", request.getParameter("mode"));
		pref.store();

		response.setPortletMode(PortletMode.VIEW);
	}
}
