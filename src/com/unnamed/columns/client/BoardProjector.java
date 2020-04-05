package com.unnamed.columns.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.JPanel;

import com.unnamed.columns.config.Config;
import com.unnamed.columns.game.Block;
import com.unnamed.columns.game.Board;
import com.unnamed.columns.game.Column;

public class BoardProjector extends JPanel{
	private static final long serialVersionUID = 1L;
	Board board;
	Image[] images;
	Image blockBackground;
	
	public void setBoard(Board board){
		this.board = board;
	}
	
	public BoardProjector(){
		super();
		
		images = new Image[Config.getInstance().getBlockImages().length];
		
		int c=0;
		for(String imgPath:Config.getInstance().getBlockImages()){
			images[c++]=Toolkit.getDefaultToolkit().getImage(BoardProjector.class.getResource(imgPath));
		}
		
		blockBackground = Toolkit.getDefaultToolkit().getImage(BoardProjector.class.getResource(Config.getInstance().getBlockBackground()));
		
	}
	
	public void paint(Graphics g){
		if(board!=null){
			Graphics2D gd2 = (Graphics2D)g;
			
			int bWidth = board.getWidth();
			int bHeight = board.getHeight();
	
			int width = getWidth();
			int height = getHeight();
			
			int boxWidth = width/bWidth;
			int boxHeight = height/bHeight;
			
			
			float imgScaleW = (float)boxWidth/images[0].getWidth(null);
			float imgScaleH = (float)boxHeight/images[0].getHeight(null);
	
			gd2.scale(imgScaleW, imgScaleH);
			gd2.setColor(Color.white);
			
			for(int y=0;y<board.getHeight();y++){
				for(int x=0;x<board.getWidth();x++){
					gd2.drawImage(blockBackground,x*blockBackground.getWidth(null),y*blockBackground.getHeight(null),null);
				}
			}
			gd2.setFont(new Font("Verdana",Font.PLAIN,20));
			for(int y=0;y<board.getHeight();y++){
				for(int x=0;x<board.getWidth();x++){
					Block block = board.getBlockAt(x, y);
					//gd2.drawString(x+","+y,x*images[0].getWidth(null),y*images[0].getHeight(null));
					if(block!=null){
						gd2.drawImage(images[block.getType()],x*images[block.getType()].getWidth(null),y*images[block.getType()].getHeight(null),null);
						if(block.isConnected()){
							Color old = gd2.getColor();
							gd2.setColor(Color.blue);
							gd2.fillRect(x*images[0].getWidth(null), y*images[0].getHeight(null), images[0].getWidth(null),images[0].getHeight(null));
							gd2.setColor(old);
						}
					}
					
					// Draw player columns
					for(Column col:board.getColumns()){
						if(col.inRange(x, y)){
							Block colBlock = col.getBlocks().get(y-col.getY());
							gd2.drawImage(images[colBlock.getType()],x*images[colBlock.getType()].getWidth(null),y*images[colBlock.getType()].getHeight(null),null);
						}
					}
				}
			}
	
			
		}
	}
}
