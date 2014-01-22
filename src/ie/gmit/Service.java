
package ie.gmit;

import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class Service  {

	public static void main(String[] args) throws RemoteException, MalformedURLException, NotBoundException, UnsupportedEncodingException
	{
		//command line argument check
		if(args.length<2 || args[0].length()>3 || args[1].length()<0)
			printErr();
		
		//store values of arguments in variables
		String actionFlag = args[0].toLowerCase();
		String filename = args[1];
		
		//make a message
		UserMessage um = new UserMessage();

		//check the action to be performed
		switch(actionFlag)
		{
			case("c"):
				um.setAction(MessageActionsEnum.Compress);
				break;
			case("-c"):
				um.setAction(MessageActionsEnum.Decompress);
				break;
			case("e"):
				um.setAction(MessageActionsEnum.Encrypt);
				break;
			case("-e"):
				um.setAction(MessageActionsEnum.Decrypt);
				break;
			case("ec"):
				um.setAction(MessageActionsEnum.EncryptAndCompress);
				break;
			case("-ec"):
				um.setAction(MessageActionsEnum.DecompressAndDecrypt);
				break;
			default:
				printErr();
				break;
		}
		
		//get instance of date to make id
		Date sysDate = new Date();
		SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMddhhmmssS");
		String msgID = ft.format(sysDate);	
		um.setMessageID(msgID);

		//set processing flag to unprocessed
		um.setMessageStatus(MessageStatusCodeEnum.Unproccessed);
		
		//set output folder of the message - empty for the same folder
		um.setOutputPath("");
		
		//setting the default password
		um.setMessagePWD("G00279198");
		
		try
		{
			//read the file as byte array
			RandomAccessFile f = new RandomAccessFile(filename, "r");
			byte[] filedata = new byte[(int)f.length()];
			f.read(filedata);
			f.close();
			
			um.IsFile(true);
			um.setFileName(removeExtension(filename));
			um.setMessage(filedata);
			
			System.out.println("Filename: " + um.getFileName());
		}
		catch(Exception e)
		{
			System.out.println("Cannot read the file. Assuming it is a string");
			um.IsFile(false);
			um.setFileName(um.getMessageID());
			um.setMessage(filename.getBytes());
		}
		
		//initialize the server class which will bind the rmi registry
		Server s = new Server();
		
		//rmi lookup
		CryptoCompressionService cs = (CryptoCompressionService) Naming.lookup("rmi://localhost:1099/CryptoCompressionService");

		//because a reply comes back as an arraylist
		ArrayList<UserMessage> reply =  cs.processThisRequest(um);

		//iterate throught the array
		Iterator<UserMessage> ri = reply.iterator();
		
		//print the result
		while(ri.hasNext())
		{
			UserMessage obj = ri.next();
			if(obj.getMessageStatus() == MessageStatusCodeEnum.Unproccessed)
				System.out.println("--------\r\nRequest Sent\r\n--------\r\n\r\n");
			else
				System.out.println("--------\r\nResponse Recieved\r\n--------\r\n\r\n");
			System.out.println("ID: " + obj.getMessageID());
			System.out.println("Message: " + new String(obj.getMessage(), "UTF-8"));
			System.out.println("Password: " + obj.getMessagePWD());
			System.out.println("Action: " + obj.getAction().toString());
			System.out.println("Code: " + obj.getMessageStatus().toString());
			System.out.println("\r\n");
		}
		
		s.shutdown();
	}
	
	//print the standard error
	private static void printErr()
	{
		System.out.println("Operation Failed:");
		System.out.println("This service accept an operation flag and a filename as c:/abc.txt and outputs a c:/abc.gz");
		System.out.println("Flags are as follow.");
		System.out.println("e - Encryption");
		System.out.println("-e - Decryption");
		System.out.println("c - Compression");
		System.out.println("-c - Decompression");
		System.out.println("ec - Encryption & Compression");
		System.out.println("-ec - Decompression & Decryption");
		System.exit(0);
	}
	
	//removes extension from the file name
	public static String removeExtension(String s) {

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
}
