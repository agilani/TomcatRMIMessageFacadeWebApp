package ie.gmit;

import java.io.UnsupportedEncodingException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface CryptoCompressionService extends Remote {
	//an interface
	public ArrayList<UserMessage> processThisRequest(UserMessage umsg) throws RemoteException, UnsupportedEncodingException;
}
