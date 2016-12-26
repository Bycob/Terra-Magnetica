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

import org.lwjgl.glfw.GLFW;

public class KeyboardInput {
	
	private List<KeyboardEvent> events = new ArrayList<KeyboardEvent>();
	
	public KeyboardInput() {}
	
	/**
	 * Envoie à chaque listeners tous les évènements enregistrés.
	 */
	public void sendEvents(GuiComponent listener) {
		synchronized (events) {
			KeyboardListener[] keyboardListeners = listener.getKeyboardListeners();
			for (KeyboardEvent e : events) {
				for (KeyboardListener l : keyboardListeners) {
					l.eventKey(e);
				}
			}
			
			for (KeyboardListener l : keyboardListeners) {
				if (l instanceof WriterInput) {
					((WriterInput) l).sendEvents();
				}
			}
			
			if (events.size() != 0)
				events = new ArrayList<KeyboardEvent>();
		}
	}
	/*
	 * Enregistre tous les évènements clavier disponibles.
	 *
	public void registerInput() {
		synchronized (events) {
			while (Keyboard.isCreated() && Keyboard.next()) {
				events.add(new KeyboardEvent(Keyboard.getEventCharacter(),
						Keyboard.getEventKey(), Keyboard.getEventKeyState()));
			}
		}
	}*/

	public void addKeyEvent(int key, int scancode, int action, int mods) {
		synchronized (this.events) {
			this.events.add(new KeyboardEvent((char) scancode, key, action == GLFW.GLFW_PRESS));
		}
	}
}
