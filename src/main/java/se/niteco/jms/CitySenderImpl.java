package se.niteco.jms;

import se.niteco.jms.CitySender;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

@Component("citySender")
public class CitySenderImpl implements CitySender {
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }
 
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
	
    @Transactional
	public void sendCities(final String cities) {
		// TODO Auto-generated method stub
    	 System.out.println("Sending JMS Message");
         System.out.println(cities);
  
         //jmsTemplate.convertAndSend(cities);
         /*jmsTemplate.send(new MessageCreator() {
        	 public Message createMessage(Session session) throws JMSException {
        			 return session.createObjectMessage(cities);
        	 }
         });*/
         
         ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
		 Connection conn = null;
		 Session session = null;
		 try {
			 conn = cf.createConnection();
			 session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			 Destination destination = new ActiveMQQueue("queue");
			 MessageProducer producer = session.createProducer(destination);
			 TextMessage message = session.createTextMessage();
			 message.setText(cities);
			 producer.send(message);
		 } catch (JMSException e) {
			 e.printStackTrace();
		 } finally {
			 try {
				 if (session != null) {
					 session.close();
				 }
				 if (conn != null) {
					 conn.close();
				 }
			 } catch (JMSException ex) {
				 ex.printStackTrace();
			 }
		 }
  
         System.out.println("Message sent.");
	}

}
