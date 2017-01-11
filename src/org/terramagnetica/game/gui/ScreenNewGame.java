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

import java.awt.Toolkit;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.gui.Apparition;
import org.terramagnetica.opengl.gui.GuiActionEvent;
import org.terramagnetica.opengl.gui.GuiButtonText1;
import org.terramagnetica.opengl.gui.GuiLabel;
import org.terramagnetica.opengl.gui.GuiTextField1;
import org.terramagnetica.ressources.TexturesLoader;
import org.terramagnetica.utile.GameException;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.Util;

public class ScreenNewGame extends GameScreen {
	
	private GuiButtonText1 returnButton;
	private GuiButtonText1 ok;
	private GuiTextField1 name;
	private GuiLabel title;
	private GuiLabel invalid;
	private Apparition b_invalid = new Apparition();
	
	public ScreenNewGame() {
		
		GuiDefaultTransition tt = new GuiDefaultTransition(this);
		tt.setSpeed(SPEED_DEFAULT);
		
		this.returnButton = new GuiButtonText1(0.8, -0.6, 1, -0.8, "retour");
		this.ok = new GuiButtonText1(-0.4, -0.4, 0.4, -0.6, "Valider");
		this.name = new GuiTextField1(-0.9, 0.1, 0.9, -0.1);
		this.title = new GuiLabel("Entrez votre pseudo :", 20, 0, 0.6);
		this.invalid = new GuiLabel("Ce pseudo existe déjà !", 14, 0, 0.3);
		
		this.title.setColor(ok.getTextColor());
		this.invalid.setColor(new Color4f(255, 85, 0));
		this.invalid.setVisible(false);
		this.invalid.addBehavior(this.b_invalid);
		
		this.name.inputAccept(new char[]{'!', '?', '.', ';', '\\', '/'}, false);
		this.name.setMaxCapacity(30);
		
		this.add(returnButton);
		this.add(name);
		this.add(ok);
		this.add(title);
		this.add(this.invalid);
		
		tt.add(returnButton, GuiDefaultTransition.DROITE);
		tt.add(name, GuiDefaultTransition.BAS);
		tt.add(ok, GuiDefaultTransition.BAS);
		tt.add(title, GuiDefaultTransition.HAUT);
		
		this.setTransition(tt);
	}
	
	@Override
	protected void drawComponent(Painter painter) {
		TextureQuad tex = TexturesLoader.getQuad(Util.formatDecimal(GameRessources.SPEC_IMG_TERRAIN, 1) + GameRessources.TEX_SOL);
		painter.setColor(new Color4f(0.5f, 0.5f, 0.5f));
		tex.fillScreen2D(0.5, 0.5, true, painter);
	}
	
	@Override
	public GuiActionEvent processLogic() {
		if (getState() != VISIBLE) {
			return super.processLogic();
		}
		
		if (returnButton.processLogic() == GuiActionEvent.CLICK_EVENT){
			nextPanel(new ScreenMainMenu());
			return GuiActionEvent.NULL_EVENT;
		}
		
		if (ok.processLogic() == GuiActionEvent.CLICK_EVENT) {
			if (!this.name.getText().equals("")) {
				
				try {
					TerraMagnetica.theGame.createNewSauvegarde(this.name.getText());
					TerraMagnetica.theGame.saveAll();
					this.nextPanel(new ScreenInGameMenu());
				} catch (GameException e) {
					this.name.setText("");
					this.b_invalid.appearDuring(this.invalid, 1500);
					return GuiActionEvent.NULL_EVENT;
				}
			}
			else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
		
		return GuiActionEvent.NULL_EVENT;
	}
	
	@Override
	public int timeToAppear() {
		return MILLIS_APPEAR_DEFAULT;
	}

	@Override
	public int timeToDestroy() {
		return MILLIS_DESTROY_DEFAULT;
	}
}
