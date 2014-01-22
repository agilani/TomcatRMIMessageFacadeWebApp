package ie.gmit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class myQueues {

	public LinkedList<UserMessage> myInQueue; //the in queue
	public Map<String, ArrayList<UserMessage>> myOutQueue; //the out queue
	public boolean isInQueueLocked = false; //a bool to implement mutex
	
	static myQueues mq; //a static instance of the object
	
	//private constructor
	private myQueues()
	{
		myInQueue = new LinkedList<UserMessage>();
		myOutQueue = new HashMap<String, ArrayList<UserMessage>>();
	}
	
	//synchronized method to give instance of queue
	public static synchronized myQueues getInstance()
	{
		//if instance of object is not initialized then initialize
		if(mq==null)
		{
			mq = new myQueues();
		}
		//return instance
		return mq;
	}
}
