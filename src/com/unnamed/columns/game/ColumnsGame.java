package com.unnamed.columns.game;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.unnamed.columns.config.Config;

public class ColumnsGame {
	Board board;
	ConcurrentHashMap<String,Player> players;
	Thread gameThread;
	GameEvent events;
	boolean started;
	boolean stop;
	
	public ColumnsGame(){
		players = new ConcurrentHashMap<String, Player>();
	}
	
	public void start(){
		board = new Board(Config.getInstance().getBoardWidth(),Config.getInstance().getBoardHeight()+Config.getInstance().getColumnSize());
		
		gameThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(!stop){
					try {
						board.process();
						// Create new column for each player if not exists
						for(Player current:players.values()){
							if(board.getColumn(current.getId())==null){
								int playerSpace = board.getWidth()/players.values().size();
								board.addColumn(current.getId(),new Column(current.getNumber()*playerSpace/2,0,BlockFactory.random(Config.getInstance().getColumnSize())));
							}
						}
						events.onProcess();
						
						// Check end of game
						for(int x=0;x<board.getWidth();x++){
							if(board.getBlockAt(x, 0)!=null && board.getBlockAt(x, 0).getStatus()==Block.StableStatus){
								stop=true;
								events.onGameEnd();
								break;
							}
						}
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						stop=true;
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		},"GameThread");
		started=true;
		gameThread.start();
	}
	
	public synchronized void addPlayer(Player player){
		if(players.values().size()<Config.getInstance().getMaxPlayers()){
			players.put(player.getId(), player);
			events.onPlayerAdded(player);
		}
		int c=0;
		for(Player current:players.values()){
			current.setNumber(c++);
		}
	}
	
	public void removeAllPlayers(){
		this.players.clear();
	}
	
	public Player getPlayer(String id){
		if(players.containsKey(id)){
			return players.get(id);
		}else{
			return null;
		}
	}

	public void stop(){
		stop=true;
	}
	
	public Block[][] getBoardBlocks(){
		return this.board.getBlockMatrix();
	}

	public Board getBoard(){
		return board;
	}
	public synchronized void removePlayer(String playerId, String reason) {
		Player player = players.remove(playerId);
		if(player!=null){
			events.onPlayerRemoved(player,reason);
		}
	}

	public GameEvent getEvents() {
		return events;
	}

	public void setEvents(GameEvent events) {
		this.events = events;
	}

	public Collection<Player> getPlayers() {
		return this.players.values();
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}
	
	public void moveLeft(String playerId){
		board.moveColumnLeft(playerId);
	}
	public void moveRight(String playerId){
		board.moveColumnRight(playerId);
	}
	public void moveDrop(String playerId){
		board.dropColumn(playerId);
	}
	public void moveRoll(String playerId){
		board.rollColumn(playerId);
	}
}
