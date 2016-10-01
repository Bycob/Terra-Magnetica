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

package org.terramagnetica.game.gui;

import org.terramagnetica.game.NullDescriptionException;
import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.gui.GuiActionEvent;
import org.terramagnetica.opengl.gui.GuiButtonText1;
import org.terramagnetica.opengl.gui.GuiLabel;

public class ScreenYouWonH extends GameScreen {
	
	private GuiButtonText1 nextLevel;
	private GuiButtonText1 backTitleScreen;
	private GuiLabel youWon;
	
	public ScreenYouWonH() {
		
		this.youWon = new GuiLabel("Niveau terminé !", 20, 0, 0.5);
		this.backTitleScreen = new GuiButtonText1(0.1, -0.6, 0.9, -0.8, "Menu");
		this.nextLevel = new GuiButtonText1(-0.9, -0.6, -0.1, -0.8, "Niveau suivant");

		this.youWon.setColor(this.nextLevel.getTextColor());
		
		GuiDefaultTransition tt = new GuiDefaultTransition(this);
		tt.add(this.nextLevel, GuiDefaultTransition.BAS | GuiDefaultTransition.GAUCHE);
		tt.add(this.backTitleScreen, GuiDefaultTransition.BAS | GuiDefaultTransition.DROITE);
		tt.add(this.youWon, GuiDefaultTransition.HAUT);
		
		tt.setSpeed(SPEED_DEFAULT);
		this.setTransition(tt);
		
		this.add(this.youWon);
		this.add(this.backTitleScreen);
		this.add(this.nextLevel);
	}
	
	@Override
	public int timeToAppear() {
		return MILLIS_APPEAR_DEFAULT;
	}

	@Override
	public int timeToDestroy() {
		return MILLIS_DESTROY_DEFAULT;
	}
	
	@Override
	public void drawComponent(Painter painter) {
		this.drawDefaultBackground(painter);
	}
	
	@Override
	public GuiActionEvent processLogic() {
		if (this.getState() != VISIBLE) {
			return super.processLogic();
		}
		
		if (this.backTitleScreen.processLogic() == GuiActionEvent.CLICK_EVENT) {
			this.nextPanel(new ScreenInGameMenu());
			return GuiActionEvent.NULL_EVENT;
		}
		if (this.nextLevel.processLogic() == GuiActionEvent.CLICK_EVENT) {
			if (TerraMagnetica.theGame.save.story.isEnded()) {
				this.nextPanel(new ScreenEndGame());
			}
			else {
				try {
					this.nextPanel(new ScreenBeforeLevelH(TerraMagnetica.theGame.save.story.getCurrentLevelID()));
				} catch (NullDescriptionException e) {
					TerraMagnetica.theGame.runHistory();
					this.nextPanel(new ScreenGamePlaying());
				}
			}
		}
		
		
		return GuiActionEvent.NULL_EVENT;
	}
}
