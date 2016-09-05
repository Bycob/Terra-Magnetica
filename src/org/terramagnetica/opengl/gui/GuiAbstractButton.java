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

import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.Vec2i;

public abstract class GuiAbstractButton extends GuiComponent implements MouseListener {
	
	/** Indique si le bouton a été cliqué. */
	private boolean clicked = false;
	/** L'état du bouton. */
	private int state;
	
	public static final int PRESSED = 2;
	public static final int MOUSE_ON = 1;
	public static final int NORMAL = 0;
	
	private static final int[] availableStates = {PRESSED, MOUSE_ON, NORMAL};
	
	protected GuiAbstractButton() {}
	
	@Override
	public void eventMouse(MouseEvent e) {
		if (!this.isVisible()) {
			this.state = NORMAL;
			return;
		}
		
		int button = e.getButton();
		boolean state = e.getState();
		Vec2i mouse = new Vec2i(e.getX(), e.getY());
		
		if (this.getBoundsDisp().contains(mouse)) {
			if (!state){
				if (this.state == PRESSED && button == BUTTON_LEFT) {
					this.clicked = true;
					this.state = MOUSE_ON;
				}
				if (this.state == NORMAL)  {
					this.state = MOUSE_ON;
				}
			}
			else if (button == BUTTON_LEFT) {
				this.state = PRESSED;
			}
		}
		else {
			this.state = NORMAL;
		}
	}
	
	@Override
	public GuiActionEvent processLogic(){
		if (!this.isVisible()) {
			this.clicked = false;
		}
		
		if (this.clicked) {
			this.clicked = false;
			return GuiActionEvent.CLICK_EVENT;
		}
		
		return GuiActionEvent.NULL_EVENT;
	}
	
	public int getState() {
		return state;
	}
	
	protected void setState(int state) {
		if (!Util.arrayContainsi(availableStates, state))
			throw new IllegalArgumentException("state " + state + " is not enabled");
		this.state = state;
	}
	
	public boolean isClicked() {
		return this.clicked;
	}
	
	public void click() {
		this.clicked = true;
	}
}
