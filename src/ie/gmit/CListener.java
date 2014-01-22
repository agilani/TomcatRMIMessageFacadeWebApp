package ie.gmit;

import java.rmi.Naming;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class CListener implements ServletContextListener {

	private Thread t; //thread
	private Server s; //instance of server class
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		//Application is finishing
		if(s!=null)
			s.shutdown(); //shutdown rmi server
		if(t!=null)
			t.interrupt(); //kill the thread off
		System.err.println("\r\n\r\nDS App is stopping and all the running threads are finishing\r\n\r\n\r\n");
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		//context variable
		Integer webapp_ready_for_use = Integer.parseInt(arg0.getServletContext().getInitParameter("webapp_ready_for_use"));
		
		/*
		//see if registry exist in rmi registry
		//if not create registry
		//else move on to the queue manipulator thread
		try
		{
			//registry exist
			CryptoCompressionService cs = (CryptoCompressionService) Naming.lookup("rmi://localhost:1099/CryptoCompressionService");
			try {
				t = new Thread(QueueManipulator.getInstance(cs));
			    //t.setDaemon(true);
			    t.start();
			} catch (Exception e) {
				System.err.println("Error: Queue Checking Thread has not started successfully. Stop tomcat and restart and redeploy app");
				System.err.println("Error Details: " + e.getMessage());
			}
		}
		catch(Exception e)
		{
			//an error occured. most probably registry doesn't exist
			s = new Server(); //server is a class which creates object and bind it to registry
		}
		*/
		
		/////////////////////////////////////
		//the above code was something i was trying... before i modified the code of queuemanipulator to accept external reference of cs
		//it was working. but i need to have more classes related to rmi packaged with this ds.war for deployment so i went with the following code
		//
		//following
		////////////////////////////////////
		
		//check if the binding exist in registry
		//if so we are good to go
		//else display an error and ask to start ie.gmit.AsyncService first
		
		try
		{
			//registry exist
			CryptoCompressionService cs = (CryptoCompressionService) Naming.lookup("rmi://localhost:1099/CryptoCompressionService");
			webapp_ready_for_use = 1;
			//start the queue manipulater thread and pass our remote stub to work with
			try {
				t = new Thread(QueueManipulator.getInstance(cs));
			    //t.setDaemon(true);
			    t.start();
			} catch (Exception e) {
				System.err.println("Error: Queue Checking Thread has not started successfully. Stop tomcat and restart and redeploy app");
				System.err.println("Error Details: " + e.getMessage());
			}
		}
		catch(Exception e)
		{
			//an error occured. most probably registry doesn't exist
			System.err.println("Couldn't locate the RMI binding. Please first run the ie.gmit.AsyncService from crypto.ds and then restart this app");
			//going to disable the index.jsp output and instead will display an erro
			//going to achieve this using a param from web.xml
			//webapp_ready_for_use
			
			webapp_ready_for_use = 0;
		}
		
		//set servlet attribute to determine if app should run or not
		arg0.getServletContext().setAttribute("webapp_ready_for_use", webapp_ready_for_use);
	}
}
