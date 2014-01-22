package ie.gmit;

import java.io.Serializable;
import java.rmi.RemoteException;

public class UserMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private String messageID; //to keep track of ID of the message
	private byte[] message; //actual message
	private String messagePWD; //any password associated with message
	private MessageActionsEnum action; //what to do with message
	private MessageStatusCodeEnum messageStatus; //success, failed or 
	private boolean isFile = false; //flag to indicate if message came from a file
	private String fileName; //store the file name if any
	private String outputPath; //to specify the path where the data will be saved
	
	//empty constructor
	public UserMessage() throws RemoteException
	{
		
	}
	
	//
	//*
	//* following are getters and setters for the privates above
	//*
	//
	
	public String getMessageID(){
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getMessagePWD() {
		return messagePWD;
	}

	public void setMessagePWD(String messagePWD) {
		this.messagePWD = messagePWD;
	}

	public MessageActionsEnum getAction() {
		return action;
	}

	public void setAction(MessageActionsEnum action) {
		this.action = action;
	}

	public MessageStatusCodeEnum getMessageStatus() {
		return messageStatus;
	}

	public void setMessageStatus(MessageStatusCodeEnum messageStatus) {
		this.messageStatus = messageStatus;
	}

	public boolean IsFile() {
		return isFile;
	}

	public void IsFile(boolean isFile) {
		this.isFile = isFile;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
}
