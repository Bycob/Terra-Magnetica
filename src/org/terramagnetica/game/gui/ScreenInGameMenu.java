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

import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.NullDescriptionException;
import org.terramagnetica.opengl.gui.GuiActionEvent;
import org.terramagnetica.opengl.gui.GuiBorderLayout;
import org.terramagnetica.opengl.gui.GuiButtonText1;
import org.terramagnetica.opengl.gui.GuiContainer;
import org.terramagnetica.opengl.gui.GuiFrameContainer;
import org.terramagnetica.opengl.gui.GuiLabel;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;

public class ScreenInGameMenu extends GameScreen {
	
	private GuiButtonText1 returnButton;
	private GuiButtonText1 optionButton;
	private GuiButtonText1 freeGameButton;
	private GuiButtonText1 historyButton;
	
	public ScreenInGameMenu() {
		
		this.returnButton = new GuiButtonText1(0.1, -0.6, 0.9, -0.8, "Ecran titre");
		this.optionButton = new GuiButtonText1(-0.9, -0.6, -0.1, -0.8, "Options");
		this.freeGameButton = new GuiButtonText1(-0.9, 0.5, 0.9, 0.3, "Jeu libre");
		this.historyButton = new GuiButtonText1(-0.9, 0.0, 0.9, -0.2, "Histoire");
		
		GuiFrameContainer princPan = new GuiFrameContainer();
		GuiContainer centrePan = new GuiContainer();
		GuiContainer titlePan = new GuiContainer();
		GuiContainer lowPan = new GuiContainer();
		GuiBorderLayout princLayout = new GuiBorderLayout();
		
		Color4f color1 = new Color4f(0, 0, 0, 32);
		lowPan.setBackground(color1); titlePan.setBackground(color1);
		princPan.setLayout(princLayout);
		princPan.add(titlePan, GuiBorderLayout.TOP);
		princPan.add(lowPan, GuiBorderLayout.BOTTOM);
		princPan.add(centrePan, GuiBorderLayout.CENTER);
		
		GuiLabel lblTitle = new GuiLabel("Menu", 20, 0, 0);
		lblTitle.setColor(TEXT_COLOR_DEFAULT);
		titlePan.add(lblTitle);
		
		GuiDefaultTransition tt = new GuiDefaultTransition(this);
		tt.setSpeed(SPEED_DEFAULT);
		tt.add(this.returnButton, GuiDefaultTransition.BAS | GuiDefaultTransition.DROITE);
		tt.add(this.optionButton, GuiDefaultTransition.BAS | GuiDefaultTransition.GAUCHE);
		tt.add(this.freeGameButton, GuiDefaultTransition.HAUT);
		tt.add(this.historyButton, GuiDefaultTransition.HAUT);
		tt.add(lblTitle, GuiDefaultTransition.DROITE);
		tt.setBorderLayout(princLayout);
		
		this.setTransition(tt);
		
		centrePan.add(this.returnButton);
		centrePan.add(this.optionButton);
		centrePan.add(this.freeGameButton);
		centrePan.add(this.historyButton);
		this.add(princPan);
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
	protected void drawComponent() {
		drawDefaultBackground();
	}
	
	@Override
	public GuiActionEvent processLogic() {
		if (getState() != VISIBLE) {
			return super.processLogic();
		}
		
		if (returnButton.processLogic() == GuiActionEvent.CLICK_EVENT) {
			TerraMagnetica.theGame.exitCurrentSave();
			this.nextPanel(new ScreenMainMenu());
		}
		
		if (this.optionButton.processLogic() == GuiActionEvent.CLICK_EVENT) {
			this.nextPanel(new ScreenOptions());
		}
		
		if (this.historyButton.processLogic() == GuiActionEvent.CLICK_EVENT) {
			if (TerraMagnetica.theGame.save.story.isEnded()) {
				this.nextPanel(new ScreenEndGame());
			}
			else {
				try {
					int id = TerraMagnetica.theGame.save.story.getCurrentLevelID();
					TexturesLoader.loadTextureSet(GameRessources.getTextureSetByLevel(id + 1));
					this.nextPanel(new ScreenBeforeLevelH(id));
				} catch (NullDescriptionException e) {
					TerraMagnetica.theGame.runHistory();
					this.nextPanel(new ScreenGamePlaying());
				}
			}
		}
		
		if (this.freeGameButton.processLogic() == GuiActionEvent.CLICK_EVENT) {
			this.nextPanel(new ScreenFreeGame());
		}
		
		return GuiActionEvent.NULL_EVENT;
	}

}
