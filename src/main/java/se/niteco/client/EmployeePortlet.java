package se.niteco.client;

import javax.portlet.*;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Portlet class of Niteco's employees
 */
public class EmployeePortlet extends GenericVelocityPortlet {
	
	/**
	 * The rendering method of the portlet.
	 * 
	 * @param request 	the rendering request
	 * @param response	the rendering response
	 */
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException{
		if(request.getParameter("status") == null || request.getParameter("status").equals("default") ){
			//default view
			super.doView(request, response);
		}
		else {
			if(request.getParameter("status").equals("add")){
				PortletRequestDispatcher dispatcher = this.getPortletContext().getRequestDispatcher("/velocity/addEmployee.vm");
				// delegate the view into addEmployee.vm
				dispatcher.include(request, response);	
			}
			else if(request.getParameter("status").equals("edit")){
				PortletRequestDispatcher dispatcher = this.getPortletContext().getRequestDispatcher("/velocity/editEmployee.vm");
				// delegate the view into editEmployee.vm
				dispatcher.include(request, response);	
			}
		}
	}
	
	/**
	 * The action method of the portlet.
	 * 
	 * @param request	the action request
	 * @param response	the response request 
	 */
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException{
		String action = request.getParameter("action");
		if (action.equals("add")) //if we add an employee
			response.setRenderParameter("status", "add");
		else if (action.equals("edit")) //if we edit an employee
			response.setRenderParameter("status", "edit");
		else //default view
			response.setRenderParameter("status", "default");
	}
	
}
