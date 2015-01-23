package se.niteco.communication;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import se.niteco.model.City;

@Component
public class CityListReceiverImpl implements MessageListener {
	
    private List<City> cities;
    /*
    //@Autowired
    private JmsTemplate jmsTemplate;
 
    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }
 
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }*/
 
    @Transactional
    public void onMessage(Message message) {
        System.out.println("In onMessage()");
        System.out.println("Printing JMS Message Details ->"+message);
        System.out.println("Converting to object message...");
 
        ObjectMessage objectMessage = (ObjectMessage) message;
 
        System.out.println("Printing downcasted Object Message Details ->" + objectMessage);
 
        System.out.println("Downcasting ObjectMessage to cities...");
        try {
        	cities = (List<City>) objectMessage.getObject();
        } catch (JMSException e) {
            e.printStackTrace();
        }
 
        System.out.println("Going out of onMessage()");
        System.out.println("Going out of Receiver...Bye");
	}

}
