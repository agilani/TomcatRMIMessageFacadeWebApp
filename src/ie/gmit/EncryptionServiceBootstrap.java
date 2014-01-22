package ie.gmit;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class EncryptionServiceBootstrap extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	

	
	//Because <load-on-startup> is set to 1, this init() method will be executed when Tomcat is started
	public void init() throws ServletException {
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	}
	
	//If anyone issues a POST request, dispatch the request and response objects to the GET method
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
 	}
}