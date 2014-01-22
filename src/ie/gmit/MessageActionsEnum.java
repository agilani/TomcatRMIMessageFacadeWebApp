package ie.gmit;

import java.io.Serializable;

//actions to be performed on the message
public enum MessageActionsEnum implements Serializable {
	Encrypt, Decrypt, Compress, Decompress, EncryptAndCompress, DecompressAndDecrypt;
}
