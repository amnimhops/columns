package com.unnamed.columns.game;

import java.io.IOException;
import java.io.OutputStream;

import com.unnamed.columns.net.Message;

public class Player {
	String id;
	int number;
	private OutputStream stream;
	
	public Player(String id, OutputStream stream){
		this.id=id;
		this.stream=stream;
	}
	
	public String getId() {
		return id;
	}

	public void sendMessage(Message msg) throws IOException{
		msg.send(stream);
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
