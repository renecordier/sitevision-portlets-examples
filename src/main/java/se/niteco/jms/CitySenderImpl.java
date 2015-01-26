package se.niteco.jms;

import se.niteco.jms.CitySender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jms.core.JmsTemplate;

@Component("citySender")
public class CitySenderImpl implements CitySender {
	
	@Autowired
	private static JmsTemplate jmsTemplate;
	
	public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }
 
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        CitySenderImpl.jmsTemplate = jmsTemplate;
    }
    
    public CitySenderImpl() {
		
	}
    
    public CitySenderImpl(JmsTemplate jmsTemplate) {
		CitySenderImpl.jmsTemplate = jmsTemplate;
	}
	
	public void sendCities(final String cities) {
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
