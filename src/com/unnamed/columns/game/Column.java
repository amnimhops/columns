package com.unnamed.columns.game;

import java.io.Serializable;
import java.util.List;

public class Column implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<Block> blocks;
	int x,y;
	
	public Column(int x, int y, List<Block> blocks){
		this.x=x;
		this.y=y;
		this.blocks=blocks;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getSize() {
		return blocks.size();
	}
	
	public boolean inRange(int x, int y){
		return (x==this.x && y>=this.y && y<this.y+this.blocks.size());
	}

	public List<Block> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Block> blocks) {
		this.blocks = blocks;
	}
}
