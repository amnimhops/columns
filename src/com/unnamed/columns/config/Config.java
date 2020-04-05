package com.unnamed.columns.config;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Main configuration class
 * Reads by default the file /resources/config.json and loads its configuration
 * @author amnimhops
 *
 */
public class Config {
	String[] blockImages;
	String blockBackground;
	String midi;
	int serverPort;
	String serverHost;
	int boardWidth,boardHeight;
	int maxPlayers;
	int columnSize;
	
	// Configuration singleton
	private static transient Config globalCfg;
	
	/**
	 * No user configuration allowed
	 */
	private Config(){
		
	}
	
	/* Public getters */
	public String[] getBlockImages(){
		return this.blockImages;
	}
	
	public String getBlockBackground(){
		return blockBackground;
	}
	
	public String getMidi(){
		return midi;
	}
	public int getServerPort(){
		return serverPort;
	}
	public String getServerHost(){
		return serverHost;
	}
	public int getBoardWidth() {
		return boardWidth;
	}

	public int getBoardHeight() {
		return boardHeight;
	}

	public int getMaxPlayers() {
		return maxPlayers;
	}

	public int getColumnSize() {
		return columnSize;
	}

	/**
	 * Class entry point. Gives acces to class singleton.
	 * @return Config The config singleton
	 */
	public static Config getInstance(){
		if(Config.globalCfg==null){
			Config.globalCfg = Config.load();
		}
		
		return globalCfg;
	}
	
	/**
	 * Parses configuration file. No need of public access
	 * @return
	 */
	private static Config load(){
		Reader reader = new InputStreamReader(Config.class.getResourceAsStream("/resources/config.json"));
		Type configType = new TypeToken<Config>() {
		}.getType();

		Gson gson = new Gson();
		return gson.fromJson(reader, configType);
	}	
}
