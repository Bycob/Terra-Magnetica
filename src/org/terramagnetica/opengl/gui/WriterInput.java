/* <LICENSE>
Copyright (C) 2013-2016 Louis JEAN

This file is part of Terra Magnetica.

Terra Magnetica is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Terra Magnetica is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Terra Magnetica. If not, see <http://www.gnu.org/licenses/>.
 </LICENSE> */

package org.terramagnetica.opengl.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.terramagnetica.opengl.miscellaneous.Timer;

public class WriterInput implements KeyboardListener {
	
	private static List<WriterInput> registerList = new ArrayList<WriterInput>();
	
	private List<Writer> writers = new ArrayList<Writer>();
	
	private boolean running;
	
	private char keyChar;
	private int key;
	private boolean state;
	
	private char oldKeyChar;
	private boolean oldState;
	private List<Character> arePressed = new ArrayList<Character>();
	
	private int timeWaiting = 0;
	private int timeRepeatChar = 500;
	private int timeBetweenChars = 50;
	private Timer chrono = new Timer();
	
	private List<Character> refused = new ArrayList<Character>();
	
	public WriterInput(Writer... writers) {
		for (Writer writer : writers) {
			this.writers.add(writer);
		}
		
		registerList.add(this);
		
		refused.add((char) 0);
		refused.add((char) 8);
	}
	
	/**
	 * Pour chaque objet {@link WriterInput} existant, appelle la m�thode
	 * {@link #sendEvents()}.
	 */
	public static void allSendEvents() {
		for (WriterInput input : registerList) {
			input.sendEvents();
		}
	}
	
	/**
	 * D�termine les nouvelles entr�es clavier � partir de celles qu'� d�j�
	 * re�ues le {@link WriterInput}, puis les redistribue � tous les
	 * {@link Writer} ajout�s.
	 */
	public void sendEvents() {
		boolean isWaiting = (chrono.getTime() < timeWaiting);
		
		if (state && !oldState) {
			callWriters();
			timeWaiting = timeRepeatChar;
		}
		
		if (oldState && state) {
			if (keyChar == oldKeyChar) {
				if (!isWaiting) {
					callWriters();
					timeWaiting = this.timeBetweenChars;
				}
			}
			else {
				oldState = false;
				return;
			}
		} 
		
		if (oldState && !state) {
			if (arePressed.size() != 0)
				arePressed.remove(keyChar);
			if (arePressed.size() != 0) {
				state = true;
				keyChar = arePressed.get(arePressed.size() - 1);
			}
			else {
				timeWaiting = 0;
			}
		}
		
		oldKeyChar = keyChar;
		oldState = state;
		
		if (!isWaiting)
			chrono.restart();
	}
	
	private void callWriters() {
		if (!refused.contains(keyChar)) {
			for (Writer writer : this.writers) {
				writer.write(keyChar);
			}
		}
		
		switch (key) {
		case Keyboard.KEY_BACK :
		case Keyboard.KEY_DELETE :
			for (Writer writer : this.writers) {
				writer.remove(key);
			}
			break;
		case Keyboard.KEY_LEFT :
		case Keyboard.KEY_RIGHT :
			for (Writer writer : this.writers) {
				writer.move(key);
			}
		}
	}
	
	/** ajoute ou retire un caract�re � la liste des caract�res refus�s,
	 * en fonction du param�tre <tt>accepted</tt>.<br>
	 * Les touches correspondant au caract�re en question n'enverront plus de signal.<br>
	 * Par d�faut tous les caract�res sont accept�s, sauf le caract�re nul et le backspace (effacer � gauche).
	 * @param character - le caract�re
	 * @param accepted - <code>true</code> si accept� (retir� de la liste), <code>false</code> si refus�
	 * 	(ajout� � la liste) */
	public synchronized void accept(char character, boolean accepted) {
		if (accepted) {
			if (refused.size() != 0) refused.remove(character);
		}
		else {
			refused.add(character);
		}
	}
	
	/** ajoute ou retire chaque caract�re que contient le tableau dans la liste des caract�res refus�s,
	 * en fonction du param�tre <tt>accepted</tt>.<br>
	 * voir {@link WriterInput#accept(char, boolean)} pour plus de d�tails.
	 * @see WriterInput#accept(char, boolean) */
	public void accept(char[] characters, boolean accepted) {
		if (characters == null) throw new NullPointerException("characters == null !");
		for (char character : characters) {
			accept(character, accepted);
		}
	}
	
	@Override
	public void eventKey(KeyboardEvent e) {
		keyChar = e.getCharacter();
		key = e.getKey();
		state = e.getKeyState();
		sendEvents();
	}
}
