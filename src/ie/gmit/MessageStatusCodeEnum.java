package ie.gmit;

import java.io.Serializable;

//just a status code to be attached to every message
//purily for practice and making it look good purposes.
public enum MessageStatusCodeEnum implements Serializable {
	Unproccessed, Failed, Successful;
}
