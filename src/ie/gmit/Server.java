package ie.gmit;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;


public class Server {
	
	public Server()
	{
		try {
			CryptoCompressionService cs = new CryptoCompressionServiceImpl();
			LocateRegistry.createRegistry(1099);
			Naming.rebind("CryptoCompressionService", cs);
			System.out.println("\r\n\r\n\r\n\r\n\r\n\r\nCrypto Server has started\r\n\r\n\r\n\r\n\r\n\r\n");
		} catch (Exception e) {
			System.out.println("\r\n\r\n\r\n\r\n\r\n\r\nCrypto Server is already running\r\n\r\n\r\n\r\n\r\n\r\n");
		}

	}
	
	public void shutdown()
	{
		System.out.println("\r\n\r\nCrypto Server shutting down.\r\n\r\n");
		System.exit(0);
	}
}
