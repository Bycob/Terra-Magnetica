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
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;
import org.terramagnetica.game.GameInputBuffer.InputKey;

/**
 * La classe qui gère les options dans le jeu (contrôles...)
 * @author Louis JEAN
 *
 */
public class Options {
	
	private Map<InputKey, Integer> controls = new HashMap<InputKey, Integer>();
	
	public Options() {
		initControls();
	}
	
	private void initControls() {
		this.controls.put(InputKey.KEY_DOWN, Keyboard.KEY_DOWN);
		this.controls.put(InputKey.KEY_LEFT, Keyboard.KEY_LEFT);
		this.controls.put(InputKey.KEY_UP, Keyboard.KEY_UP);
		this.controls.put(InputKey.KEY_RIGHT, Keyboard.KEY_RIGHT);
		this.controls.put(InputKey.KEY_TALK, Keyboard.KEY_SPACE);
		this.controls.put(InputKey.KEY_ECHAP, Keyboard.KEY_ESCAPE);
	}
	
	private InputKey assignedInput(int keyID) {
		for (Entry<InputKey, Integer> e : this.controls.entrySet()) {
			if (e.getValue() == keyID) {
				return e.getKey();
			}
		}
		return null;
	}
	
	public void setInput(InputKey key, Integer keyID) {
		if (this.assignedInput(keyID) == null)
			this.controls.put(key, keyID);
		else throw new IllegalStateException("La touche est déjà utilisée.");
	}
	
	public int getInputID(InputKey key) {
		Integer result = this.controls.get(key);
		return result == null ? -1 : result;
	}
	
	public void loadOptions(String optionFile) {
		String[] lines = optionFile.split("\n");
		String line = lines.length == 0 ? null : lines[0];
		
		for (int i = 0 ; i < lines.length ; ++i) {
			line = lines[i];
			
			if (line.equals("controls")) {
				while (i < lines.length - 1 && !(line = lines[++i]).equals("")) {
					String[] kv = line.split(":");
					InputKey key;
					if (kv.length > 1 && (key = InputKey.getInputByName(kv[0])) != null) {
						try {
							this.controls.put(key, Integer.parseInt(kv[1]));
						} catch (NumberFormatException e) {}
					}
				}
			}
		}
	}
	
	public void writeOptions(StringBuilder builder) {
		builder.append("controls\n");
		
		for (Entry<InputKey, Integer> e : this.controls.entrySet()) {
			builder
			.append(e.getKey().getKeyName())
			.append(":")
			.append(e.getValue())
			.append("\n");
		}
		
		builder.append("\n");
	}
}
