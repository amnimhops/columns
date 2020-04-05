package com.unnamed.columns.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.unnamed.columns.config.Config;
import com.unnamed.columns.game.ColumnsGame;
import com.unnamed.columns.game.Command;
import com.unnamed.columns.game.GameEvent;
import com.unnamed.columns.game.Player;
import com.unnamed.columns.net.EventHandler;
import com.unnamed.columns.net.Message;
import com.unnamed.columns.net.MessageDispatcher;

public class ColumnsServer {
	public static void main(String[] args){
		ServerSocket server;
		final ColumnsGame game = new ColumnsGame();
		
		game.setEvents(new GameEvent() {
			@Override public void onProcess() {
				Message msg = new Message(Command.Board,game.getBoard());
				
				for(Player current:game.getPlayers()){
					try {
						current.sendMessage(msg);
					} catch (IOException e) {
						Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"Error de E/S, eliminando al jugador");
						game.removePlayer(current.getId(),e.getMessage());
					}
				}
			}
			@Override public void onPlayerRemoved(Player p,String reason) {
				Logger.getLogger(this.getClass().getName()).log(Level.INFO,"El jugador "+p.getId()+" ha sido eliminado ("+reason);
			}
			@Override public void onPlayerAdded(Player p) {
				Message msg = new Message(Command.Info,"El jugador "+p.getId()+" se ha unido al juego");
				
				for(Player current:game.getPlayers()){
					try {
						current.sendMessage(msg);
					} catch (IOException e) {
						Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"Error de E/S, eliminando al jugador");
						game.removePlayer(current.getId(),e.getMessage());
						e.printStackTrace();
					}
				}
			}
			@Override
			public void onGameEnd() {
				Message msg = new Message(Command.EndOfGame,"El juego ha terminado");

				for(Player current:game.getPlayers()){
					try {
						current.sendMessage(msg);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				game.removeAllPlayers();
				
			}
		});
		
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress(Config.getInstance().getServerPort()));
			
			while(true){
				final Socket socket = server.accept();
				final MessageDispatcher dispatcher = new MessageDispatcher();
				
				EventHandler eHandler = new EventHandler() {
					@Override
					public void onMessageReceived(String sender,Message msg) {
						switch(msg.getType()){
						case Command.Login:
							try {
								game.addPlayer(new Player(sender, socket.getOutputStream()));
								if(!game.isStarted()){
									game.start();
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						case Command.Logout:
							game.removePlayer(sender,"El jugador se ha marchado");
							dispatcher.terminate();
							break;
						case Command.MoveLeft:
							game.moveLeft(sender);
							break;
						case Command.MoveRight:
							game.moveRight(sender);
							break;
						case Command.Drop:
							game.moveDrop(sender);
							break;
						case Command.Roll:
							game.moveRoll(sender);
							
						}
					}
				};
				
				dispatcher.setEventHandler(eHandler);
				dispatcher.dispatch(socket.getInputStream());
			}
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
