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
import org.terramagnetica.opengl.miscellaneous.Timer;

/** Cette classe gère l'écran de chargement, ainsi que tout ce qui s'affiche
 * avant l'écran d'accueil (éditeur du jeu, animation de lancement, etc)  */
public class TitleScreen extends GameScreen {
	
	private Timer chrono = new Timer();
	private boolean finished = false;
	
	private static final int TIME_OF_ANIMATION = 0;
	
	public TitleScreen() {
		this.setSoundPlayed("");
	}

	/** Dessine l'écran de chargement.*/
	public void drawWaiting() {
		
	}
	
	@Override
	protected void drawComponent() {
		if (!this.chrono.isRunning()) {
			this.chrono.start();
		}
		
		long now = chrono.getTime();
		
		//dessin
		
		if (now >= TIME_OF_ANIMATION) {
			finished = true;
			return;
		}
	}
	
	@Override
	public GuiActionEvent processLogic() {
		if (!(getState() == VISIBLE)) {
			return super.processLogic();
		}
		
		if (this.finished) {
			this.nextPanel(new ScreenMainMenu());
		}
		
		return GuiActionEvent.NULL_EVENT;
	}

	@Override
	public int timeToAppear() {
		return 0;
	}

	@Override
	public int timeToDestroy() {
		return 0;
	}
}
