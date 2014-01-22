package ie.gmit;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptDecrypt{

	public UserMessage encryptData(UserMessage umsg) throws RemoteException, UnsupportedEncodingException {
		UserMessage newMsgAfterAction = new UserMessage(); //creating a message as response
		newMsgAfterAction.setAction(umsg.getAction()); //the action of the message which is getting performed
		newMsgAfterAction.setMessageID(umsg.getMessageID()); //the message id
		newMsgAfterAction.setMessagePWD(umsg.getMessagePWD()); //the original password by user or generated by system
		newMsgAfterAction.setFileName(umsg.getFileName()); //the original filename by user
		newMsgAfterAction.IsFile(umsg.IsFile()); //is it file or not
		newMsgAfterAction.setOutputPath(umsg.getOutputPath());//the original output path from the request
		
		try
		{
			SecretKey secretKey = this.generateKey(umsg.getMessagePWD()); //get the secret key made for encryption
			
			Cipher cypher;
			try {
				cypher = Cipher.getInstance("AES"); //getting an instance of AES
				cypher.init(Cipher.ENCRYPT_MODE, secretKey); //Initializing the properties
				newMsgAfterAction.setMessage(cypher.doFinal(umsg.getMessage())); //doing cyphering and setting it as message for message object
				newMsgAfterAction.setMessageStatus(MessageStatusCodeEnum.Successful);
			} catch (NoSuchPaddingException e) {
				newMsgAfterAction.setMessage("Error: Couldn't get instance of AES Ciphering".getBytes("UTF-8"));
				newMsgAfterAction.setMessageStatus(MessageStatusCodeEnum.Failed);
			} catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
				newMsgAfterAction.setMessage("Error: The key provided isn't right".getBytes("UTF-8"));
				newMsgAfterAction.setMessageStatus(MessageStatusCodeEnum.Failed);
			}
			
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e1) {
			newMsgAfterAction.setMessage("Error: The key couldn't be generated.".getBytes("UTF-8"));
			newMsgAfterAction.setMessageStatus(MessageStatusCodeEnum.Failed);
		}
	
		return newMsgAfterAction;
	}

	public UserMessage decryptData(UserMessage umsg) throws RemoteException, UnsupportedEncodingException {
		UserMessage newMsgAfterAction = new UserMessage(); //creating a message as response
		newMsgAfterAction.setAction(umsg.getAction()); //the action of the message which is getting performed
		newMsgAfterAction.setMessageID(umsg.getMessageID()); //the message id
		newMsgAfterAction.setMessagePWD(umsg.getMessagePWD()); //the original password by user or generated by system
		newMsgAfterAction.setFileName(umsg.getFileName()); //the original filename by user
		newMsgAfterAction.IsFile(umsg.IsFile()); //is it file or not
		newMsgAfterAction.setOutputPath(umsg.getOutputPath());//the original output path from the request
		
		try
		{
			SecretKey secretKey = this.generateKey(umsg.getMessagePWD()); //get the secret key made for encryption
			
			Cipher cypher;
			try {
				cypher = Cipher.getInstance("AES"); //getting an instance of AES
				cypher.init(Cipher.DECRYPT_MODE, secretKey); //Initializing the properties
				newMsgAfterAction.setMessage(cypher.doFinal(umsg.getMessage())); //doing cyphering and setting it as message for message object
				newMsgAfterAction.setMessageStatus(MessageStatusCodeEnum.Successful);
			} catch (NoSuchPaddingException e) {
				newMsgAfterAction.setMessage("Error: Couldn't get instance of AES Ciphering".getBytes("UTF-8"));
				newMsgAfterAction.setMessageStatus(MessageStatusCodeEnum.Failed);
			} catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
				newMsgAfterAction.setMessage(("Error: The key provided isn't right" + e.getMessage()).getBytes("UTF-8"));
				newMsgAfterAction.setMessageStatus(MessageStatusCodeEnum.Failed);
			}
			
		} catch (UnsupportedEncodingException | NoSuchAlgorithmException e1) {
			newMsgAfterAction.setMessage("Error: The key couldn't be generated.".getBytes("UTF-8"));
			newMsgAfterAction.setMessageStatus(MessageStatusCodeEnum.Failed);
		}

		return newMsgAfterAction;
	}
	
	private SecretKeySpec generateKey(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, RemoteException
	{
		byte[] key = (password).getBytes("UTF-8");
		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		key = sha.digest(key);
		key = Arrays.copyOf(key, 16); // use only first 128 bit

		SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
		
		return secretKeySpec;
	}
}

//http://stackoverflow.com/a/3452620/1171333
//learned how to generate key using my own password from here