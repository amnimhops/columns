package com.unnamed.columns.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible of handling incoming messages
 * without blocking main thread.
 *
 * After every incoming message, the dispatcher sends it to his event handler
 * 
 * For every dispatcher an UUID is generated in order to identify each client
 * 
 * @author amnimhops
 *
 */
public class MessageDispatcher {
	public final static long DEFAULT_IDLE_TIME = 100;
	EventHandler eventHandler;
	boolean terminate;
	long idleTime;
	UUID uuid;
	
	public MessageDispatcher() {
		this.terminate=false;
		this.idleTime=DEFAULT_IDLE_TIME;
		this.eventHandler=null;
		this.uuid = UUID.randomUUID();
	}
	
	public void dispatch(final InputStream ioStream){
		Thread dispatcher = new Thread(new Runnable() {
			@Override
			public void run() {
				
				try {
					while(!terminate){
						if(ioStream.available()>0){
							Message msg = new Message(ioStream);
							eventHandler.onMessageReceived(uuid.toString(),msg);
						}else{
							Thread.sleep(idleTime);
						}
					}
					ioStream.close();
				} catch (IOException e) {
					Logger.getLogger(MessageDispatcher.class.getName()).log(Level.SEVERE,"IO Error:"+e.getMessage());
					e.printStackTrace();
				} catch (InterruptedException e) {
					Logger.getLogger(MessageDispatcher.class.getName()).log(Level.SEVERE,"Thread interrupted");
					e.printStackTrace();
				}
				
				Logger.getLogger(MessageDispatcher.class.getName()).log(Level.INFO,"Closing dispatcher");
			}
		},"MessageDispatcher-"+uuid.toString());
		
		dispatcher.start();
	}
	
	/**
	 * Terminate dispatcher on next iteration
	 */
	public void terminate(){
		this.terminate=true;
	}

	public EventHandler getEventHandler() {
		return eventHandler;
	}

	public void setEventHandler(EventHandler eventHandler) {
		this.eventHandler = eventHandler;
	}

	public long getIdleTime() {
		return idleTime;
	}

	public void setIdleTime(long idleTime) {
		this.idleTime = idleTime;
	}
}
