package com.unnamed.columns.midi;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;

public class MidiPlayer {
	Sequence sequence;
	Sequencer sequencer;
	Synthesizer synthesizer;
	String midiFile;
	public MidiPlayer(String file){
		midiFile = file;
	}
	
	public void start(){
		
		try {
			sequence = MidiSystem.getSequence(MidiPlayer.class.getResource(midiFile));
			sequencer = MidiSystem.getSequencer(false);
			sequencer.open();
			sequencer.setSequence(sequence);
			synthesizer = MidiSystem.getSynthesizer();

			synthesizer.open();
			
			Receiver	synthReceiver = synthesizer.getReceiver();
			Transmitter	seqTransmitter = sequencer.getTransmitter();
			
			seqTransmitter.setReceiver(synthReceiver);
		    
			sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
			
			sequencer.start();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
		}
		
	}
	
	public void stop(){
		if(sequencer!=null){
			sequencer.stop();
			try {
				synthesizer.getReceiver().close();
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
			synthesizer.close();
			
		}
	}
	
	public void setSpeed(float s){
		if(sequencer!=null){
			sequencer.setTempoFactor(s);
		}
	}
	
	public float getSpeed(){
		return sequencer.getTempoFactor();
	}
}
