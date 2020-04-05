package com.unnamed.columns.game;

import java.io.Serializable;

/**
 * Block class. Work unit of columns
 * @author amnimhops
 *
 */
public class Block implements Serializable{
	private static final long serialVersionUID = 1L;

	public final static int DefaultBlock = -1;
	public final static int StableStatus = 0;
	public final static int FallingStatus = 1;
	
	int type;
	int status;
	boolean connected;
	
	/**
	 * Constructs a new block
	 * @param type type of block (defined by user)
	 * @param status (falling or stable)
	 */
	public Block(int type, int status){
		this.type=type;
		this.status=status;
		this.connected = false;
	}
	
	/**
	 * Comparator shortcut
	 */
	public boolean equals(Object other){
		return (other !=null && other instanceof Block && ((Block)other).type==type && ((Block)other).getStatus()==status);
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	/**
	 * Return an exact copy of the block
	 */
	public Block clone(){
		return new Block(this.type,this.status);
	}
}
