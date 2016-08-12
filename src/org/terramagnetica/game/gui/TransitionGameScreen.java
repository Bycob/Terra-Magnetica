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
along with BynarysCode. If not, see <http://www.gnu.org/licenses/>.
 </LICENSE> */

package org.terramagnetica.game.gui;

import org.terramagnetica.opengl.gui.GuiActionEvent;

public class TransitionGameScreen extends GameScreen {
	
	private int tAppear = 0, tDestroy = 0;
	
	@Override
	public int timeToAppear() {
		return tAppear;
	}

	@Override
	public int timeToDestroy() {
		return tDestroy;
	}
	
	public void setTAppear(int time) {
		this.tAppear = time;
	}
	
	public void setTDestroy(int time) {
		this.tDestroy = time;
	}
	
	@Override
	public void draw() {
		this.selectDrawingMode();
		this.drawChildren();
	}
	
	@Override
	public void drawComponent() {}
	@Override
	public GuiActionEvent processLogic() {return GuiActionEvent.NULL_EVENT;}
}
