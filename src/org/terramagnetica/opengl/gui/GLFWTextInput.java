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
import java.util.HashMap;

import org.lwjgl.glfw.GLFW;
import org.terramagnetica.opengl.gui.KeyboardEvent.KeyState;

import net.bynaryscode.util.Util;

public class GLFWTextInput implements KeyboardListener {
	
	private ArrayList<TextListener> listeners = new ArrayList<TextListener>();
	private HashMap<Character, Boolean> refused = new HashMap<Character, Boolean>();
	
	public GLFWTextInput(TextListener... listeners) {
		Util.addAll(listeners, this.listeners, false);
		
		accept((char) 0, false);
		accept((char) 8, false);
	}
	
	public void addTextListener(TextListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public void eventKey(KeyboardEvent e) {
		for (TextListener l : this.listeners) {
			if (e.getKey() == GLFW.GLFW_KEY_UNKNOWN) {
				if (isAccepted(e.getCharacter())) {
					l.write(e.getCharacter());
				}
			}
			else {
				if (e.getKeyState() == KeyState.RELEASE) continue;
				
				if (e.getKey() == GLFW.GLFW_KEY_RIGHT || e.getKey() == GLFW.GLFW_KEY_LEFT) {
					l.move(e.getKey());
				}
				else if (e.getKey() == GLFW.GLFW_KEY_DELETE || e.getKey() == GLFW.GLFW_KEY_BACKSPACE) {
					l.remove(e.getKey());
				}
			}
		}
	}
	
	/** ajoute ou retire un caractère à la liste des caractères refusés,
	 * en fonction du paramètre <tt>accepted</tt>.<br>
	 * Les touches correspondant au caractère en question n'enverront plus de signal.<br>
	 * Par défaut tous les caractères sont acceptés, sauf le caractère nul et le backspace (effacer à gauche).
	 * @param character - le caractère
	 * @param accepted - <code>true</code> si accepté (retiré de la liste), <code>false</code> si refusé
	 * 	(ajouté à la liste) */
	public synchronized void accept(char character, boolean accepted) {
		this.refused.put(character, accepted);
	}
	
	/** ajoute ou retire chaque caractère que contient le tableau dans la liste des caractères refusés,
	 * en fonction du paramètre <tt>accepted</tt>.<br>
	 * voir {@link GLFWTextInput#accept(char, boolean)} pour plus de détails.
	 * @see GLFWTextInput#accept(char, boolean) */
	public void accept(char[] characters, boolean accepted) {
		if (characters == null) throw new NullPointerException("characters == null !");
		for (char character : characters) {
			accept(character, accepted);
		}
	}
	
	public boolean isAccepted(char character) {
		Boolean result = this.refused.get(character);
		return result != null ? result : true;
	}
}
