package se.niteco;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class EmployeePortlet extends GenericPortlet{
	
	public void doView(RenderRequest request, RenderResponse response) 
			throws PortletException, IOException{
		PrintWriter write = response.getWriter();
		write.println("This is Niteco employee list");
	}
}
