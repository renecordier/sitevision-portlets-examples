package se.niteco.controller;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import se.niteco.data.EmployeeDataObject;

/**
 * Servlet managing the context attribute employeeList 
 */
public class EmployeeListContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent event) {
		event.getServletContext().removeAttribute("employeeList");
	}

	public void contextInitialized(ServletContextEvent event) {
		EmployeeDataObject employees = new EmployeeDataObject();
		event.getServletContext().setAttribute("employeeList", employees);
	}

}
