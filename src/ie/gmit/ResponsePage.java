package ie.gmit;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResponsePage extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		//Get the form information
		String msgID = req.getParameter("msgID").trim();

		//giving some response back to user
		resp.setContentType("text/html"); //Set the MIME type of our HTTP response
		PrintWriter out = resp.getWriter(); //Use a PrintWriter to build the output to the client browser
		
		out.print("<html><head><title>Request Processing Statut</title></head><body>");
		
		//check if there was a request id sent
		if(!msgID.trim().isEmpty())
		{
			
			//try searching for the msgid in the queue
			if(myQueues.getInstance().myOutQueue.containsKey(msgID))
			{
				//get the message from the queue
				ArrayList<UserMessage> msgs = myQueues.getInstance().myOutQueue.get(msgID);
				
				//display result
				out.print("<p><h2>The request id " + msgs.get(0).getMessageID() + " has been processed</h2></p>");
				out.print("<p><h2>Using Password: " + msgs.get(0).getMessagePWD() + "</h2></p>");
				out.print("<p><h2>Action Performed: " + msgs.get(0).getAction().toString() + "</h2></p>");
				
				out.print("<p><h3>Original Request</h3><br><textarea cols='50' rows='10'>");
				out.print(new String(msgs.get(0).getMessage(), "UTF-8"));
				out.print("</textarea></<p>");
				
				out.print("<p><h3>Response</h3><br>");
				out.print(new String(msgs.get(1).getMessage(), "UTF-8"));
				out.print("</<p>");
				
				//remove message from the queue because it has been served
				//outQueue.remove(msgID);
				//out.print("<p>Requested id " + msgID + " has been removed from processing queue.</p>");
			}
			else
			{
				//message is not in the queue yet
				out.print("<h3>your request with <b>request id " + msgID + "</b> has not yet been processed.<br>Please try back again in few seconds.</h3><br>the demon thread is configured to check the queue for the messages once in 60secs.");
			}
		}
		else
		{
			//no message id provided
			out.print("<h3>please enter a request id.</h3>");
		}
		out.print("</body></html>");
	}
	
	//If anyone issues a POST request, dispatch the request and response objects to the GET method
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
 	}
}
