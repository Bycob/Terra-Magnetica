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

import org.terramagnetica.game.GameInterruption;
import org.terramagnetica.opengl.gui.GuiComponent;

public abstract class GameRendering extends GuiComponent {
	
	protected GameInterruption nextInt = null;
	protected boolean modeAuto = false;
	
	public void pause() {
		
	}
	
	public void resume() {
		
	}
	
	public void setModeAuto(boolean flag) {
		this.modeAuto = flag;
	}
	
	public void interruptGame(GameInterruption i) {
		this.nextInt = i;
	}
	
	public GameInterruption nextInterruption() {
		GameInterruption next = this.nextInt;
		this.nextInt = null;
		return next;
	}
}
