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

import org.lwjgl.glfw.GLFW;

public class MouseInput {
	
	private ArrayList<MouseEvent> events = new ArrayList<MouseEvent>();
	
	private double mouseX, mouseY;
	private double dwheel = 0;
	private boolean wheelUpdated = false;
	
	private long lastEventNanos = 0;
	
	public MouseInput() {}
	
	public void sendEvents(GuiComponent listener) {
		
		synchronized (events) {
			//On envoie les évènements à tout le monde
			for (MouseListener l : listener.getMouseListeners()) {
				for (MouseEvent e : events) {
					l.eventMouse(e);
				}
			}
			
			//On vide la liste des évènements
			this.events.clear();
		}
		
		//Mise à jour de l'état de la molette
		if (!this.wheelUpdated) {
			this.dwheel = 0;
		}
		this.wheelUpdated = false;
	}
	
	public void addCursorEvent(double x, double y) {
		this.mouseX = x;
		this.mouseY = y;
		
		this.lastEventNanos = GuiWindow.getTimeNanos();
	}
	
	public void addMouseButtonEvent(int button, int action, int mods) {
		synchronized (events) {
			this.events.add(new MouseEvent(button, action == GLFW.GLFW_PRESS, (int) this.mouseX, (int) this.mouseY));
		}

		this.lastEventNanos = GuiWindow.getTimeNanos();
	}
	
	public void addMouseWheelEvent(double xoffset, double yoffset) {
		this.dwheel = yoffset;
		this.wheelUpdated = true;

		this.lastEventNanos = GuiWindow.getTimeNanos();
	}
	
	public int getMouseX() {
		return (int) this.mouseX;
	}
	
	public int getMouseY() {
		return (int) this.mouseY;
	}
	
	public double getMouseXFullRes() {
		return this.mouseX;
	}
	
	public double getMouseYFullRes() {
		return this.mouseY;
	}
	
	/** Retourne le mouvement de la molette de souris pendant le tour de jeu
	 * actuel. */ 
	public double getMouseDWheel() {
		return this.dwheel;
	}

	public long getLastEventNanos() {
		return this.lastEventNanos;
	}
}
