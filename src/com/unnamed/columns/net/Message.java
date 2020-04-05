package com.unnamed.columns.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class sends messages over a stream.
 * The class allow a full reconstruction/deconstruction of an object that implements Serializable interface
 *  
 * @author test
 *
 */
public class Message{
	public final static String HEADER = "MSG";
	int type;
	String id;
	Object data;
	
	/**
	 * Create a new message
	 * 
	 * @param type Message type
	 * @param data Object to be send. Must be a Serializable object
	 */
	public Message(int type, Object data){
		this.type=type;
		this.data=data;
	}
	
	/**
	 * Create a new message from an input stream
	 * 
	 * @param stream Stream containing the message
	 */
	public Message(InputStream stream){
		byte[] bHeader = new byte[HEADER.getBytes().length];
		byte[] bSize = new byte[4];
		byte[] bType = new byte[4];
		byte[] bId = new byte[UUID.randomUUID().toString().getBytes().length];
		byte[] content;
		
		try {
			
			stream.read(bHeader);
			// Check stream for a valid message
			if(new String(bHeader).equals(HEADER)){
				stream.read(bSize);
				stream.read(bType);
				stream.read(bId);
				this.id=new String(bId);
				int size = new BigInteger(bSize).intValue();
				this.type = new BigInteger(bType).intValue();

				content = new byte[size];
				// Read enough bytes to reconstruct the object
				stream.read(content);

				ByteArrayInputStream bais = new ByteArrayInputStream(content);
				ObjectInputStream ois = new ObjectInputStream(bais);
				// Reconstruct object
				this.data = ois.readObject();
				
				bais.close();
				ois.close();
			}
		} catch (IOException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,"IO Error:"+e.getMessage());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			Logger.getLogger(getClass().getName()).log(Level.SEVERE,"Serialization error: given class not found:"+e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * Sends this message over a stream
	 * 
	 * @param stream
	 * @throws IOException
	 */
	public void send(OutputStream stream) throws IOException{
		// Number this message
		this.id= UUID.randomUUID().toString();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		
		oos.writeObject(data);
		
		byte[] content = baos.toByteArray();
		baos.close();
		baos = new ByteArrayOutputStream();
		int msgSize = content.length;
		byte[] bSize = intToByteArray(msgSize);
		byte[] bType = intToByteArray(type);
		byte[] bId = id.getBytes();
		baos.write(HEADER.getBytes());
		baos.write(bSize);
		baos.write(bType);
		baos.write(bId);
		baos.write(content);
		byte[] byteContent=baos.toByteArray(); 
		stream.write(byteContent);
		
		baos.close();
		oos.close();
	}
	
	/**
	 * Get the byte array representation of an integer, in big endian
	 * @param value
	 * @return
	 */
	public byte[] intToByteArray(int value) {
	    return new byte[] {
	            (byte)(value >>> 24),
	            (byte)(value >>> 16),
	            (byte)(value >>> 8),
	            (byte)value};
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
