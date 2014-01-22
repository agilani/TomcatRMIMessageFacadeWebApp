package ie.gmit;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class AsyncService {

	public static void main(String[] args)
	{
		try
		{
			CryptoCompressionService cs = new CryptoCompressionServiceImpl();
			LocateRegistry.createRegistry(1099);
			Naming.rebind("CryptoCompressionService", cs);
			System.out.println("\r\n\r\n\r\n\r\n\r\n\r\nCrypto Server has started\r\n\r\n\r\n\r\n\r\n\r\nPress ctrl+c to close service");
		} catch (Exception e) {
			System.out.println("\r\n\r\n\r\n\r\n\r\n\r\nCrypto Server is already running\r\n\r\n\r\n\r\n\r\n\r\n");
			System.exit(0);
		}
	}
}
