package se.niteco.jms;

import java.util.List;

import se.niteco.jms.CitySender;
import se.niteco.model.City;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jms.core.JmsTemplate;

/**
 * Class for producing messages and sending a list of cities
 */
@Component("citySender")
public class CitySenderImpl implements CitySender {
	
	@Autowired
	private static JmsTemplate jmsTemplate; //DI of jmstemplate
	
	public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }
 
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        CitySenderImpl.jmsTemplate = jmsTemplate;
    }
    
    /**
     * Public constructor
     */
    public CitySenderImpl() {
		
	}
    
    /**
     * Public constructor
     * @param jmsTemplate
     */
    public CitySenderImpl(JmsTemplate jmsTemplate) {
		CitySenderImpl.jmsTemplate = jmsTemplate;
	}
	
    /**
     * Function to send the message with the list of cities to employee's portlet
     */
	public void sendCities(final List<City> cities) {
    	 System.out.println("Sending JMS Message");
         System.out.println(cities);
  
         try {
        	 jmsTemplate.convertAndSend(cities);
         } catch (Exception e) {
        	 e.printStackTrace();
         }
  
         System.out.println("Message sent.");
	}

}
