package com.unnamed.columns.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.unnamed.columns.config.Config;
/**
 * Columns game bread & butter
 * 
 * This class mimics a real board, and supplies the neccesary logic for the game,
 * like block connections, score, etc 
 * 
 * @author amnimhops
 *
 */
public class Board implements Serializable{
	private static final long serialVersionUID = 1L;

	public final static int ColumnSize = 3;
	public final static int StatusProcessing = 1;
	public final static int StatusIdle = 1;
	
	/*
	 * width and height of the board, shortcut for blockMatrix.lenght and blockMatrix[n].length
	 */
	int width,height;
	/**
	 * Block matrix, self explanatory
	 */
	Block[][] blockMatrix;
	/**
	 * List of active columns
	 */
	private ConcurrentHashMap<String,Column> columns;
	/**
	 * Board status, idle or working
	 */
	int status;
	
	/**
	 * Class constructor.
	 * 
	 * Generates a new board with a given height & width
	 * @param width Width of the board
	 * @param height Height of the board
	 */
	public Board(int width,int height){
		this.width=width;
		this.height=height;
		
		this.blockMatrix = new Block[height][];
		this.columns = new ConcurrentHashMap<String,Column>();
		
		for(int c=0;c<blockMatrix.length;c++){
			blockMatrix[c] = new Block[width];
		}
		

	}
	
	Block[][] getBlockMatrix(){
		return blockMatrix;
	}
	
	
	public void fixColumn(Column c){
		for(int row = 0;row<c.getSize();row++){
			blockMatrix[row+c.getY()][c.getX()]=c.getBlocks().get(row);
			blockMatrix[row+c.getY()][c.getX()].setStatus(Block.StableStatus);
			blockMatrix[row+c.getY()][c.getX()].setConnected(false);
		}
	}
	public boolean moveColumn(Column c, int xOffset, int yOffset){
		c.setX(c.getX()+xOffset);
		c.setY(c.getY()+yOffset);
		
		return true;
	}
	public void dropColumn(String id){
		Column col = columns.get(id);

		if(col!=null){
			if(col.getY()+col.getSize()<height && getBlockAt(col.getX(), col.getY()+col.getSize())==null){
				col.setY(col.getY()+1);
			}
		}
	}
	private boolean columnCanMove(Column c, int xOffset, int yOffset){
		for(int row = c.getY();row<c.getSize()+c.getY();row++){
			if(c.getX()+xOffset<0||(c.getX()+xOffset)>=width||row+yOffset<0||(row+yOffset>=height)){
				return false;
			}else{
				if(blockMatrix[row+yOffset][c.getX()+xOffset]!=null){
					return false;
				}
			}
		}
		
		return true;
	}
	
	public void addColumn(String id, Column col){
		this.columns.put(id, col);
	}
	public List<Column>  getColumns(){
		return new ArrayList<Column>(columns.values());
	}
	
	public Column getColumn(String id){
		return columns.get(id);
	}
	
	public void moveColumnLeft(String id){
		Column c = this.columns.get(id);
		if(columnCanMove(c, -1,0)){
			moveColumn(c,-1,0);
		}
	}
	public void moveColumnRight(String id){
		Column c = this.columns.get(id);
		if(columnCanMove(c, 1,0)){
			moveColumn(c,1,0);
		}
	}
	
	public void rollColumn(String id){
		Column c= this.columns.get(id);
		if(c!=null){
			List<Block> newblocklist = new ArrayList<Block>();
			for(int row=1;row<c.getBlocks().size();row++){
				newblocklist.add(c.getBlocks().get(row));
			}
			newblocklist.add(c.getBlocks().get(0));
			c.setBlocks(newblocklist);
		}
	}
	
	public List<Block> connected(int x, int y){
		List<Block> connections = new ArrayList<Block>();
		
		Block block = getBlockAt(x, y);
		Block top = getBlockAt(x,y-1);
		Block bottom = getBlockAt(x,y+1);
		Block left = getBlockAt(x-1,y);
		Block right = getBlockAt(x+1,y);
		Block tl = getBlockAt(x-1,y-1);
		Block tr = getBlockAt(x+1,y-1);
		Block bl = getBlockAt(x-1,y+1);
		Block br = getBlockAt(x+1,y+1);
		
		if(block!=null){
		
			if(block.equals(top) && block.getStatus()==Block.StableStatus && block.equals(bottom)){
				block.setConnected(true);
				top.setConnected(true);
				bottom.setConnected(true);
				connections.add(block);
				connections.add(top);
				connections.add(bottom);
			}
			
			if(block.equals(left) && block.getStatus()==Block.StableStatus && block.equals(right)){
				block.setConnected(true);
				left.setConnected(true);
				right.setConnected(true);
				connections.add(block);
				connections.add(left);
				connections.add(right);
			}
			
			if(block.equals(tl) && block.getStatus()==Block.StableStatus && block.equals(br)){
				block.setConnected(true);
				tl.setConnected(true);
				br.setConnected(true);
				connections.add(block);
				connections.add(br);
				connections.add(tl);
			}
			if(block.equals(tr) && block.getStatus()==Block.StableStatus && block.equals(bl)){
				block.setConnected(true);
				tr.setConnected(true);
				bl.setConnected(true);
				connections.add(block);
				connections.add(tr);
				connections.add(bl);
			}
		}
		
		if(top!=null && top.getStatus()==Block.StableStatus && top.equals(tl) && top.equals(tr)){
			top.setConnected(true);
			tr.setConnected(true);
			tl.setConnected(true);
			connections.add(top);
			connections.add(tr);
			connections.add(tl);
		}
		if(bottom!=null && bottom.getStatus()==Block.StableStatus && bottom.equals(bl) && bottom.equals(br)){
			bottom.setConnected(true);
			bl.setConnected(true);
			br.setConnected(true);
			connections.add(bottom);
			connections.add(bl);
			connections.add(br);
		}
		if(left!=null && left.getStatus()==Block.StableStatus && left.equals(tl) && left.equals(bl)){
			left.setConnected(true);
			tl.setConnected(true);
			bl.setConnected(true);
			connections.add(left);
			connections.add(tl);
			connections.add(bl);
		}
		if(right!=null && right.getStatus()==Block.StableStatus && right.equals(tr) && right.equals(br)){
			right.setConnected(true);
			tr.setConnected(true);
			br.setConnected(true);
			connections.add(right);
			connections.add(tr);
			connections.add(br);
		}
		
		return connections;
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public int getHeight(){
		return this.height;
	}
	
	
	public Block getBlockAt(int x, int y){
		return blockMatrix[y][x];
	}
	public void process(){
		int stableBlockCount = 0;
		int fallingBlockCount = 0;
		
		status = StatusProcessing;
		
		for(int y=blockMatrix.length-2;y>=0;y--){
			for(int x=0;x<blockMatrix[y].length;x++){
				if(blockMatrix[y][x]!=null){
					if(blockMatrix[y+1][x]==null){
						blockMatrix[y][x].setStatus(Block.FallingStatus);
						blockMatrix[y+1][x]=blockMatrix[y][x];
						blockMatrix[y][x]=null;
						fallingBlockCount++;
					}else{
						blockMatrix[y][x].setStatus(Block.StableStatus);
						stableBlockCount++;
					}
				}
			}
		}
		
		for(String id:columns.keySet()){
			Column c = columns.get(id);
			if(c.getY()+c.getSize()<height && getBlockAt(c.getX(),c.getY()+c.getSize())==null){
				c.setY(c.getY()+1);
			}else{
				// Fix column to board and destroy
				fixColumn(c);
				if(c.getY()>0){
					columns.remove(id);
				}else{
					// 	Nothing. Keeping alive the column prevents the game bean to generate another column
				}
				
			}
		}
		
		// Set last line stable by default
		for(int x=0;x<width;x++){
			if(blockMatrix[height-1][x]!=null){
				blockMatrix[height-1][x].setStatus(Block.StableStatus);
			}
		}
				
		if(true){
			checkConnected();

			for(int y=0;y<blockMatrix.length;y++){
				for(int x=0;x<blockMatrix[y].length;x++){
					if(blockMatrix[y][x]!=null && blockMatrix[y][x].isConnected()){
						blockMatrix[y][x]=null;
					}
				}
			}

		}
		
		status = StatusIdle;
	}
	
	public List<Block> checkConnected(){
		List<Block> blocksConnected=new ArrayList<Block>();
		
		for(int y = 1;y<blockMatrix.length-1;y++){
			for(int x=1;x<blockMatrix[y].length-1;x++){

				List<Block> connections=connected(x, y);
				
				for(Block current:connections){
					if(!blocksConnected.contains(current)){
						blocksConnected.add(current);
					}
				}
			}
		}
		
		return blocksConnected;
	}
	
	public void addRandomRow(){
		for(int c=0;c<width;c++){
			float b = (float) Math.random();
			if(b>=.5f){
				Config cfg = Config.getInstance();
				blockMatrix[0][c]=new Block((int)(cfg.getBlockImages().length*Math.random()),Block.FallingStatus);
			}
			
		}
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
}
