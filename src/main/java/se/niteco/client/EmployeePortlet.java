package se.niteco.client;

import javax.portlet.*;

import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

import java.io.IOException;
import java.io.PrintWriter;

/*
 * 
 */
public class EmployeePortlet extends GenericVelocityPortlet {
	
	public void doView(RenderRequest request, RenderResponse response) 
			throws PortletException, IOException{
		if(request.getParameter("status") == null || request.getParameter("status").equals("view") ){
			// In case it's null, return into default view that's already defined in the portlet.xml viewPage
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
	
	
	public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException{
		String action = request.getParameter("action");
		if (action.equals("add"))
			response.setRenderParameter("status", "add");
		else if (action.equals("edit"))
			response.setRenderParameter("status", "edit");
		else
			response.setRenderParameter("status", "view");
	}
	
}
