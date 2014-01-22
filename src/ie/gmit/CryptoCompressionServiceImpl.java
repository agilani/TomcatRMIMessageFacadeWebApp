package ie.gmit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class CryptoCompressionServiceImpl extends UnicastRemoteObject implements CryptoCompressionService {

	private static final long serialVersionUID = 1L;
	
	protected CryptoCompressionServiceImpl() throws RemoteException {
		super();
	}
	
	@Override
	public ArrayList<UserMessage> processThisRequest(UserMessage umsg) throws RemoteException, UnsupportedEncodingException
	{
		//an array list to store the user message and response message
		ArrayList<UserMessage> responseToSendBack = new ArrayList<UserMessage>();
		responseToSendBack.add(umsg); //add the original message to the list
		
		//create an empty object
		UserMessage methodProcessingResponse = new UserMessage();
		
		//check the action flag associate with the message
		//perform action based on it
		switch(umsg.getAction())
		{
			case Compress:
				methodProcessingResponse = doCompression(umsg);
				break;
			case Decompress:
				methodProcessingResponse = doDecompression(umsg);
				break;
			case DecompressAndDecrypt:
				methodProcessingResponse = doDecryption(umsg);
				if(methodProcessingResponse.getMessageStatus()!=MessageStatusCodeEnum.Failed)
					methodProcessingResponse = doDecompression(methodProcessingResponse);					
				break;
			case Decrypt:
				methodProcessingResponse = doDecryption(umsg);
				break;
			case Encrypt:
				methodProcessingResponse = doEncryption(umsg);
				break;
			case EncryptAndCompress:
				methodProcessingResponse = doCompression(umsg);
				if(methodProcessingResponse.getMessageStatus()!=MessageStatusCodeEnum.Failed)
					methodProcessingResponse = doEncryption(methodProcessingResponse);	
				break;
			default:
				methodProcessingResponse = umsg;
				break;
		}
		
		//if message processing status is not failed then write the response back to disc
		if(methodProcessingResponse.getMessageStatus()!=MessageStatusCodeEnum.Failed)
			methodProcessingResponse = fileWriter(methodProcessingResponse);
		
		//add the response to the list
		responseToSendBack.add(methodProcessingResponse);
		
		//send list back
		return responseToSendBack;
	}
	
//////////////////////////method delegation
	
	
	private UserMessage doEncryption(UserMessage umsg) throws RemoteException, UnsupportedEncodingException {
		EncryptDecrypt ed = new EncryptDecrypt();
		UserMessage reply = ed.encryptData(umsg);
		return reply;
	}

	private UserMessage doDecryption(UserMessage umsg) throws RemoteException, UnsupportedEncodingException {
		EncryptDecrypt ed = new EncryptDecrypt();
		UserMessage reply = ed.decryptData(umsg); 
		return reply;
	}

	private UserMessage doCompression(UserMessage umsg) throws RemoteException, UnsupportedEncodingException {
		CompressDecompress cd = new CompressDecompress();
		UserMessage reply = cd.compress(umsg); 
		return reply;
	}

	private UserMessage doDecompression(UserMessage umsg) throws RemoteException, UnsupportedEncodingException {
		CompressDecompress cd = new CompressDecompress();
		UserMessage reply = cd.decompress(umsg); 
		return reply;
	}
	
	private UserMessage fileWriter(UserMessage toBeWritten) throws UnsupportedEncodingException
	{
		String filename = "";
		
		if(toBeWritten.IsFile())
		{
			filename = toBeWritten.getFileName();
			
		}
		else
		{
			filename = toBeWritten.getMessageID();
		}
		
		filename += ".gz";
		
		try {
			
			FileOutputStream output = new FileOutputStream(new File(toBeWritten.getOutputPath() + filename));
			output.write(toBeWritten.getMessage());
			output.close();
			toBeWritten.setMessage(new String("You request has been processed. The resulting file is " + toBeWritten.getOutputPath() + "" + filename).getBytes("UTF-8"));
		} catch (IOException e) {
			toBeWritten.setMessage(new String("You request failed at file writing level. Error: " + e.getMessage()).getBytes("UTF-8"));
			toBeWritten.setMessageStatus(MessageStatusCodeEnum.Failed);
		}
		return toBeWritten;
	}

}
