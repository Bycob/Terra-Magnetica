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

package org.terramagnetica.game;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard;

/**
 * Cette classe sauvegarde les touches pressées par l'utilisateur, 
 * et les restitues au jeu, elle sert donc de tampon pour stocker
 * les entrées de l'utilisateur au tour d'une boucle de jeu.
 * <p>Cette classe est Thread Safe, et peut donc être utilisable
 * lorsque le moteur de jeu et l'interface graphique ne tournent
 * pas sur le même thread.
 * @author Louis JEAN
 *
 */
public class GameInputBuffer {
	
	private static final InputKey[] AVAILABLE_KEY_INPUT;
	
	public enum InputKey {
		
		KEY_RIGHT("droite"),
		KEY_LEFT("gauche"),
		KEY_UP("haut"),
		KEY_DOWN("bas"),
		KEY_TALK("interroger"),
		KEY_ECHAP("quitter / pause");
		
		private String key_name;
		
		InputKey() {
			this(null);
		}
		
		InputKey(String name) {
			if (name == null) name = name();
			this.key_name = name;
		}
		
		public String getKeyName() {
			return this.key_name;
		}
		
		public static InputKey getInputByName(String name) {
			if (name == null) return null;
			for (InputKey v : values()) {
				if (v.getKeyName().equals(name) || v.name().equals(name)) {
					return v;
				}
			}
			
			return null;
		}
	}
	
	private static final Object LOCK = new Object();
	
	static {
		AVAILABLE_KEY_INPUT = InputKey.values();
	}
	
	private Map<InputKey, Boolean> inputMap = new HashMap<InputKey, Boolean>();
	public Options options;
	
	public GameInputBuffer() {
		this.options = TerraMagnetica.theGame == null ? new Options() : TerraMagnetica.theGame.options;
		
		for (InputKey input : AVAILABLE_KEY_INPUT) {
			this.pressKey(input, false);
		}
	}
	
	public void pressKey(InputKey key, boolean pressed) {
		synchronized (LOCK) {
			this.inputMap.put(key, pressed);
		}
	}
	
	public boolean isKeyPressed(InputKey key) {
		boolean result = false;
		
		synchronized (LOCK) {
			result = this.inputMap.get(key);
		}
		
		return result;
	}
	
	public void listenInput() {
		for (InputKey input : AVAILABLE_KEY_INPUT) {
			this.pressKey(input, Keyboard.isKeyDown(this.options.getInputID(input)));
		}
	}
}
