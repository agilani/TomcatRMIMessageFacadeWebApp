Deployment
----------------------------------------------------
Please deploy whole project as a new project. Because the index file and the web.xml are presumably different from everyone else.
the webapp works only if it ie.gmit.AsyncService from crypto.jar has been started
else it displays an error on the index.jsp page when user navigate there. if you see an error then follow the instruction.

Assumptions
---------------------------
Reading the assignment descriptor i assumed that we have to read a file and generate the message from there.
And if user doesn't specify a file then the message should be taken from the text area.
I have also included an input box to specify the password for encryption.
I have also included a seperate form to lookup jobs using the job ids.

Overview
----------------------------
The user of the site can select an action and can provides a message to be processed from a file or a textarea and can also include a password. When user submit the request, it goes into a processing queue and user is presented with a link where they can check for the result. The app process the request asynchrounously and if outcome is successful, then create a resulting file in the data folder which is located in the WEBAPPS/DS/
I did development on a windows pc so the file stores in the data folder properly. You might have to run the tomcat using Administration rights for app to write on the C:
I didn't try it using a UNIX based system. You might have to run tomcat using sudo just so appropriate writing permissions are granted.

Design
----------------------------
To create a asyncronhous service i looked in to the Messaging Beans of EJB 3.0 and JMS but Apache Tomcat doesn't support both out of the box.

So i made a QueueManipulator class which is a runable. I used the singleton creation pattern so i can have a single instance in my app. 

I created a CListener class which implements "ServletContextListener" and overriden the "contextDestroyed" and "contextInitialized" methods.
In the "contextInitialized" which starts when the webapp starts, i started a thread of QueueManipulator.
I could also bind the service to rmi registry here so we don't need the AsyncService. I have left this part of the code commented.
but instead i am relying on the ie.gmit.AsyncService to run.

In "contextDestroyed" i tried to stop the thread but it seems this methods gets called only if app is undeployed. I was under the assumption that it will get 
called if someone stops the webapp or restart it. I didn't really use your Bootstrap class.

Anyway, in the QueueManipulator i am manipulating the queues using a thread. the thread will keep running untill an interupt is recieved.
It will sleep for 60sec and then check if there is anything in the IN Queue, i am processing all the messages and lodge the processed messages in a seperate OUT Queue.

I declared queues in a class called myQueues. I again used singleton pattern for this class. So i have 
one instance of the queues everywhere in my app. For concurrency i am using a locking mechanism to keep a synchronized access to queues.
e.g: when entering info in the queue, i lock the queue and unlock it straight after i am finished.
vice versa, when i am checking the inqueue for messages and retrieving them for processing i am again locking the queues.
This myQueue class has a linkedlist to store the incoming messages
	LinkedList<UserMessage> inQueue;
and a map to store the arraylist of processed messages.
	Map<String, ArrayList<UserMessage>> outQueue;
the key of the map is the Message ID associated with the incoming message and the arraylist contains the original message and the processed message.

I also took liberty of adding two enum classes of 
	MessageActionEnum - have (Encrypt, Decrypt, Compress, Decompress, EncryptAndCompress, DecompressAndDecrypt) to define action to be performed
	MessageStatusCodeEnum - have (UnProcessed, Failed, Successfull) to define the result of the processing


Command Prompt JAR Execution
-----------------------------
To run the jar file please type
java -cp crypto.jar ie.gmit.AsyncService


I have also made a command prompt utitliy to do the operations on any given file
to run it type
java -cp crypto.jar ie.gmit.Service [flag] [filename]

[flag] [filename]
this utility takes the following flags
e    - Encryption
-c   - Decryption
c    - Compresson
-c   - Decompression
ec   - Encryption & Compression
-ec  - Decryption & Decompression


It takes the arguments and process it straigt away. The resulting file is written where you execute the crypto.jar.


Project Dependencies
---------------------------
servlet-api.jar
commons-io-2.4.jar
commons-codec-1.4.jar
commons-fileupload-1.3.jar

have been packaged in ds.war to be deployed and has been packaged in crypto.jar


