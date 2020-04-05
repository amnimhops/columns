package com.unnamed.columns.client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import com.unnamed.columns.config.Config;
import com.unnamed.columns.game.Board;
import com.unnamed.columns.game.Command;
import com.unnamed.columns.game.Player;
import com.unnamed.columns.midi.MidiPlayer;
import com.unnamed.columns.net.EventHandler;
import com.unnamed.columns.net.Message;
import com.unnamed.columns.net.MessageDispatcher;

public class ColumnsClient {
	public static void main(String[] args){
		final JFrame jf = new JFrame();
		final BoardProjector bp;
		final MidiPlayer mp = new MidiPlayer(Config.getInstance().getMidi());
		
		jf.setBounds(0, 0, 800,600);
		bp = new BoardProjector();
		jf.add(bp);
		jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() { 
				Socket socket;
				final Player player;
				final MessageDispatcher dispatcher= new MessageDispatcher();
				boolean end=false;
				try {
					socket = new Socket(Config.getInstance().getServerHost(), Config.getInstance().getServerPort() );
					player= new Player(UUID.randomUUID().toString(), socket.getOutputStream());

					// Create event handler
					dispatcher.setEventHandler(new EventHandler() {
						@Override
						public void onMessageReceived(String sender,Message msg) {
							switch(msg.getType()){
							case Command.LoginOk:
								Logger.getLogger(getClass().getName()).log(Level.INFO,(String)msg.getData());
								try {
									player.sendMessage(new Message(Command.Board,"damelo todo!"));
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							case Command.Crowd:
								Logger.getLogger(getClass().getName()).log(Level.INFO,(String)msg.getData());
								dispatcher.terminate();
								break;
							case Command.Board:
								//Block[][] blocks = (Block[][])msg.getData();
								bp.setBoard((Board)msg.getData());
								
								//Logger.getLogger(getClass().getName()).log(Level.INFO,blocks.length+","+blocks[0].length);
								break;
							case Command.Info:
								Logger.getLogger(getClass().getName()).log(Level.INFO,(String)msg.getData());
								break;
							case Command.EndOfGame:
								JOptionPane.showMessageDialog(bp, (String)msg.getData());
								jf.dispose();
							}
						}
					});
					// Create window listener
					jf.addWindowListener(new WindowListener() {
						@Override public void windowOpened(WindowEvent arg0) {}
						@Override public void windowIconified(WindowEvent arg0) { }
						@Override public void windowDeiconified(WindowEvent arg0) { }
						@Override public void windowDeactivated(WindowEvent arg0) { }
						@Override public void windowClosing(WindowEvent arg0) { }
						@Override public void windowClosed(WindowEvent arg0) {	System.exit(0); }
						@Override public void windowActivated(WindowEvent arg0) { }
					});
					// Create key listener
					jf.addKeyListener(new KeyListener() {
						@Override public void keyTyped(KeyEvent arg0) {}
						@Override public void keyReleased(KeyEvent arg0) {}
						@Override public void keyPressed(KeyEvent arg0) {
							try{
								if(arg0.getKeyCode()==KeyEvent.VK_LEFT){
									player.sendMessage(new Message(Command.MoveLeft,"izquierda"));
								}
								if(arg0.getKeyCode()==KeyEvent.VK_RIGHT){
									player.sendMessage(new Message(Command.MoveRight,"derecha"));	
								}
								if(arg0.getKeyCode()==KeyEvent.VK_DOWN){
									player.sendMessage(new Message(Command.Drop,"pabajo"));	
								}
								if(arg0.getKeyCode()==KeyEvent.VK_UP){
									player.sendMessage(new Message(Command.Roll,"rueda"));	
								}
								
							}catch(IOException e){
								e.printStackTrace();
							}
						}
					});
					
					// Begin message dispatcher
					dispatcher.dispatch(socket.getInputStream());
					// Send login request
					player.sendMessage(new Message(Command.Login,"lalal"));
					// Begin redraw loop
					while(!end){
						//board.process();
						//player.sendMessage(new Message(Command.Board,"damelo"));
						bp.repaint();
						//c++;
						Thread.sleep(100);
					}
				}catch(IOException e){
					e.printStackTrace();
					end=true;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					end=true;
				}
			}
		});

		// Start main thread
		t.start();
		// Start midi thread
		mp.start();
		mp.setSpeed(1.25f);
		// Swing stuff
		jf.setVisible(true);
		
		
		
	}

}
