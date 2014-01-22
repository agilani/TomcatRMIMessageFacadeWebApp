package ie.gmit;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class QueueManipulator implements Runnable
{
	private CryptoCompressionService myRemoteService; //to store remote object reference
	private static QueueManipulator qm;
	
	//constructor 
	private QueueManipulator(CryptoCompressionService cs) throws MalformedURLException, RemoteException, NotBoundException
	{
		//remote object
		myRemoteService = cs;
	}
	
	public synchronized static QueueManipulator getInstance(CryptoCompressionService cs) throws MalformedURLException, RemoteException, NotBoundException
	{
		
		if(qm==null)
			qm = new QueueManipulator(cs);
		return qm;
	}
	
	
	@Override
	public void run() {
		System.err.println("\r\nQueue processing service is starting...");
		try
		{
			while(!Thread.currentThread().isInterrupted())
			{
				
				System.err.println("\r\nQueue processing service is running...");
				
				//check if inqueue locked. if locked means there is something being entered in the queue
				//if locked don't do anything
				if(!myQueues.getInstance().isInQueueLocked)
				{
					//check if queue has something
					//if yes then fire rmi on each item in the queue
					//else don't do nothing
					if(!myQueues.getInstance().myInQueue.isEmpty())
					{
						//lock the inqueue so nothing is added further
						System.err.println("..." + myQueues.getInstance().myInQueue.size() + " Jobs found in system to be processed");
						System.err.println("......Locking queue for processing");
						myQueues.getInstance().isInQueueLocked = true;
						//run a loop to process all the queue at once
						while(!myQueues.getInstance().myInQueue.isEmpty())
						{
							//take element out of the end of the queue
							UserMessage messageToProcess = myQueues.getInstance().myInQueue.pollLast();
							//if message is unprocessed and it has something then process
							if(messageToProcess!=null && messageToProcess.getMessageStatus()==MessageStatusCodeEnum.Unproccessed)
							{
								//try processing the request over rmi
								try {
									//invoke rmi
									ArrayList<UserMessage> responseFromRMI = myRemoteService.processThisRequest(messageToProcess);
									
									//if a response came back
									if(responseFromRMI!=null)
									{
										//add it to the outqueue
										myQueues.getInstance().myOutQueue.put(messageToProcess.getMessageID(), responseFromRMI);
									}
									else
									{
										//nothing came back
										//System.out.println("null came back");
									}
								} catch (RemoteException | UnsupportedEncodingException e) {
									e.printStackTrace();
									System.err.println("\r\n............Some Error Occurred. Unlocking queue after processing due to error");
									myQueues.getInstance().isInQueueLocked = false; //release lock
								}
							}
						}
						System.err.println("......Unlocking queue after processing");
						myQueues.getInstance().isInQueueLocked = false; //release lock
					}
				}
				
				//put thread to sleep
				try {
					Thread.sleep(60000);
				} catch (Exception e) {
					//System.out.println("Error: " + e.getMessage());
				}
			}
		}
		catch(Exception e)
		{
			try {
				this.finalize();
			} catch (Throwable e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		System.out.println("......Thread is dying....");
	}
}
