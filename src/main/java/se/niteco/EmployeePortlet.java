package se.niteco;

import javax.portlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class EmployeePortlet extends GenericPortlet{
	
	public void doView(RenderRequest request, RenderResponse response) 
			throws PortletException, IOException{
		PrintWriter write = response.getWriter();
		

		write.println("This is Niteco employees list using Portlet API 2.0");
		String tableHtml = "<table>"
				+ "<tr>"
				+ "<td> Khoi </td>"
				+ "<td> Leader </td>"
				+ "</tr>"
				+ "<tr>"
				+ "<td> XonMX </td>"
				+ "<td> Developer </td>"
				+ "</tr>"
				+ "</table>";
		
		response.setContentType("text/html");
		write.println(tableHtml);
		
	}
}
