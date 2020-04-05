package com.unnamed.columns.game;

import java.util.ArrayList;
import java.util.List;

import com.unnamed.columns.config.Config;

/**
 * Block factory class
 * @author amnimhops
 *
 */
public class BlockFactory {
	private static List<Block> blocks;
	
	/*
	 * First access ensures the class loads its configuration
	 */
	static{
		BlockFactory.blocks = new ArrayList<Block>();

		for(int c=0;c<Config.getInstance().getBlockImages().length;c++){
			BlockFactory.addBlock(new Block(c,Block.DefaultBlock));
		}
	}
	
	/**
	 * Add new block to the factory
	 * @param block Block to be added
	 */
	public static void addBlock(Block block){
		BlockFactory.blocks.add(block);
	}
	/**
	 * Return a block by its index
	 * @param no index of the block
	 * @return Block cloned block
	 */
	public static Block getBlock(int no){
		if(no<BlockFactory.blocks.size()){
			return BlockFactory.blocks.get(no).clone();
		}else{
			return new Block(Block.DefaultBlock,Block.StableStatus);
		}
	}
	
	/**
	 * Returns a random block
	 * @return Block a random cloned block
	 */
	public static Block random(){
		return BlockFactory.getBlock((int)(BlockFactory.blocks.size()*Math.random())).clone();
	}
	/**
	 * Returns a random list of blocks
	 * @param size amount of blocks being retrieved
	 * @return List<Block> random list of cloned blocks
	 */
	public static List<Block>random(int size){
		List<Block> list = new ArrayList<Block>();
		for(int c=0;c<size;c++){
			list.add(BlockFactory.getBlock((int)(BlockFactory.blocks.size()*Math.random())).clone());
		}
		
		return list;
	}
}
