package se.niteco.communication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import se.niteco.model.City;

@Service
public class CityListSenderImpl implements CityListSender {
	//@Autowired
    private JmsTemplate jmsTemplate;
 
    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }
 
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
	
	@Transactional
	public void sendCityList(String cities) {
		System.out.println("Sending JMS Message -> cities");
 
        jmsTemplate.convertAndSend(cities);
 
        System.out.println("Cities sent.");
	}
 
}
