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

import org.lwjgl.input.Mouse;

public class MouseInput {
	
	private List<MouseEvent> events = new ArrayList<MouseEvent>();
	
	public MouseInput() {}
	
	public void sendEvents(GuiComponent listener) {
		
		synchronized (events) {
			for (MouseListener l : listener.getMouseListeners()) {
				for (MouseEvent e : events) {
					l.eventMouse(e);
				}
			}
			
			events = new ArrayList<MouseEvent>();
		}
	}
	
	public void registerInput() {
		synchronized (events) {
			while (Mouse.isCreated() && Mouse.next()) {
				events.add(new MouseEvent(Mouse.getEventButton(), Mouse.getEventButtonState(),
						Mouse.getEventX(), Mouse.getEventY()));
			}
		}
	}
}
