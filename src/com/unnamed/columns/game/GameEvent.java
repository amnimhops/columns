package com.unnamed.columns.game;

public interface GameEvent {
	public void onPlayerAdded(Player p);
	public void onPlayerRemoved(Player p,String reason);
	public void onProcess();
	public void onGameEnd();
}
