package ie.gmit;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public class EncryptionServiceHandler extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		
		//create a message object
		UserMessage msg;

		//read the form data and assign the return value to the msg
		msg = this.saveAndReadFile(req, resp);
		
		//checking if form was read properly if not then msg will be null
		if(msg!=null)
		{
			//Do something with the information...like add to a queue...?
			//going to check if isInQueueLocked is true
			//if it is then wait until it is set to be false
			if(myQueues.getInstance().isInQueueLocked)
			{
				//miming a wait with a loop. I know it will finish as soon as the all the previous rmi's are processed
				while(myQueues.getInstance().isInQueueLocked)
				{
					//do nothing. Just killing time here
				}
			}
			System.err.println("...New processing request received");
			System.err.println("......Locking the in coming request queue for the new entrant");
			myQueues.getInstance().isInQueueLocked = true; //set a lock
			myQueues.getInstance().myInQueue.add(msg);
			System.err.println("......Unlocking the in coming request queue after the successful entry");
			myQueues.getInstance().isInQueueLocked = false; //release lock
			
			//Return some HTML back to the client that periodically polls a servlet to check if the request is processed.
			//giving some response back to user
			resp.setContentType("text/html"); //Set the MIME type of our HTTP response
			PrintWriter out = resp.getWriter(); //Use a PrintWriter to build the output to the client browser
			out.print("your request has been submitted to the queue<br>");
			out.print("Your request id is " + msg.getMessageID() + "<br> and is being processed.<br>Thank you for using this service.<br>");
			out.print("You can check the response at <a href='http://localhost:8080/ds/response?msgID=" + msg.getMessageID() + "'>http://localhost:8080/ds/response?msgID= " + msg.getMessageID() + "</a> in few seconds. Please allow up to 60 seconds<br>");
		}
		else
		{
			//Return some HTML back to the client that periodically polls a servlet to check if the request is processed.
			//giving some response back to user
			resp.setContentType("text/html"); //Set the MIME type of our HTTP response
			PrintWriter out = resp.getWriter(); //Use a PrintWriter to build the output to the client browser
			out.print("your request has been not submitted to the processing queue<br>Some error with form data<br>");
		}
	}
	
	//If anyone issues a POST request, dispatch the request and response objects to the GET method
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
 	}
	
	//removes extension from the file name
	public static String removeExtension(String s)
	{

	    String separator = System.getProperty("file.separator");
	    String filename;

	    // Remove the path upto the filename.
	    int lastSeparatorIndex = s.lastIndexOf(separator);
	    if (lastSeparatorIndex == -1) {
	        filename = s;
	    } else {
	        filename = s.substring(lastSeparatorIndex + 1);
	    }

	    // Remove the extension.
	    int extensionIndex = filename.lastIndexOf(".");
	    if (extensionIndex == -1)
	        return filename;

	    return filename.substring(0, extensionIndex);
	}
	
	//read the form data and read the file if any supplied
	private UserMessage saveAndReadFile(HttpServletRequest request, HttpServletResponse resp) throws IOException
	{
		UserMessage tempMsg = new UserMessage(); //a new instance of the usermessage

		//webapp's absolute path to the data folder. set message object's field
		tempMsg.setOutputPath(getServletContext().getRealPath("/data")+"/");
		
		
		//reading the form data
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart)
		{
			//couldn't read the form
			return null;
		}
		else
		{
			//a list of FileItem
			List<FileItem> items;
			try {
				//try parsing request contents
				items = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
				for (FileItem item : items)
				{
					if (item.isFormField())
					{
						//below are all input fields
						String fieldname = item.getFieldName(); //get field name
				        String fieldvalue = item.getString(); //get field value
				        
				        //parse action to be performed
				        if(fieldname.contains("cmbOperation"))
				        {
				    		switch(fieldvalue)
				    		{
				    			case "Encrypt":
				    				tempMsg.setAction(MessageActionsEnum.Encrypt);
				    				break;
				    			case "Decrypt":
				    				tempMsg.setAction(MessageActionsEnum.Decrypt);
				    				break;
				    			case "Compress":
				    				tempMsg.setAction(MessageActionsEnum.Compress);
				    				break;
				    			case "Decompress":
				    				tempMsg.setAction(MessageActionsEnum.Decompress);
				    				break;
				    			case "EncryptAndCompress":
				    				tempMsg.setAction(MessageActionsEnum.EncryptAndCompress);
				    				break;
				    			default:
				    				tempMsg.setAction(MessageActionsEnum.DecompressAndDecrypt);
				    				break;
				    		}
				        }
				        
				        //parse password if provided else setup a default one
				        if(fieldname.contains("pwd"))
				        {
				        	String salt = "G00279198";
				        	if(fieldvalue.isEmpty() || fieldvalue.length()<9)
				        		tempMsg.setMessagePWD(salt);
				        	else
				        		tempMsg.setMessagePWD(salt+fieldvalue);
				        	
				        }
				        
				        //parse text message if provided

				        if(fieldname.contains("txtMessage"))
				        {
					        //but if a file is provided then ignore it
					        if(!tempMsg.IsFile())
					        {
					        	//if no file is provided and no message is provided then just set a dummy message
					        	if(fieldvalue.isEmpty())
					        		tempMsg.setMessage("Message box was empty".getBytes("UTF-8"));
					        	else
					        		tempMsg.setMessage(fieldvalue.getBytes("UTF-8"));
					        	tempMsg.IsFile(false);
					        }
				        }
					 }
					 else
					 {
						 //this is file upload
						 
						 String filename = FilenameUtils.getName(item.getName()); //get filename
						 tempMsg.setFileName(removeExtension(filename)); //set file name in the message object
						 
						 //read file
						 //if cannot set the isFile flag of message object to false and use message field or my own message
						 InputStream filecontent;
						 try
						 {
							 filecontent = item.getInputStream();
							 byte[] theString = IOUtils.toByteArray(filecontent);
							
							 if(theString.length>0)
							 {
								tempMsg.setMessage(theString);
								tempMsg.IsFile(true);
							 }
							 else
							 {
								 tempMsg.IsFile(false);
							 }
							 
						}
						catch (IOException e)
						{
							tempMsg.IsFile(false);
						}
				    }
				 }
				 
			 }
			 catch (FileUploadException e1)
			 {
				 //COULDN'T READ FORM DATA AT ALL
				 return null;
			 }
			 
			 if(tempMsg.getAction()!=null && tempMsg.getMessage()!=null && tempMsg.getMessagePWD()!=null)
			 {
				Date sysDate = new Date( ); //a new instance of the date to get system date
				SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMddhhmmssS"); //formating the date
				String msgID = ft.format(sysDate); 
				tempMsg.setMessageID(msgID); //setting id of the message
				tempMsg.setMessageStatus(MessageStatusCodeEnum.Unproccessed);//setting the processing action
				return tempMsg; //return newly formed message
			 }
			 else
			 {
				 //PARTS OF FORM IS MISSING SO MESSAGE COULDN'T BE FORMED PROPERLY
			 	return null;
			 }
		}			 
	}
}
