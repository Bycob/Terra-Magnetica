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

import org.terramagnetica.game.Sauvegarde_Histoire;
import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.gui.GuiActionEvent;
import org.terramagnetica.opengl.gui.GuiButtonText1;
import org.terramagnetica.opengl.gui.GuiLabel;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;

public class ScreenEndGame extends GameScreen {
	
	private GuiButtonText1 retour, retry; private GuiLabel youwon;
	
	public ScreenEndGame() {
		this.retry = new GuiButtonText1(-0.9, -0.6, 0.1, -0.8, "Recommencer");
		this.retour = new GuiButtonText1(0.6, -0.6, 0.8, -0.8, "retour");
		this.youwon = new GuiLabel("Le jeu est terminé", 20, 0, 0.5);
		
		this.youwon.setColor(this.retour.getTextColor());
		
		this.add(this.retry);
		this.add(retour);
		this.add(youwon);
	}
	
	@Override
	public int timeToAppear() {
		return 0;
	}

	@Override
	public int timeToDestroy() {
		return 0;
	}
	
	@Override
	public void drawComponent(Painter p) {
		TextureQuad tex = TexturesLoader.getQuad("decor/terrain1.png.sol");
		p.ensure2D();
		p.setColor(new Color4f(0.5f, 0.5f, 0.5f));
		tex.fillScreen2D(0.5, 0.5, true, p);
	}
	
	@Override
	public GuiActionEvent processLogic() {
		if (this.getState() != VISIBLE) {
			return super.processLogic();
		}
		
		if (this.retour.processLogic() == GuiActionEvent.CLICK_EVENT) {
			this.nextPanel(new ScreenInGameMenu());
			return GuiActionEvent.NULL_EVENT;
		}
		
		if (this.retry.processLogic() == GuiActionEvent.CLICK_EVENT) {
			TerraMagnetica.theGame.save.story = new Sauvegarde_Histoire(TerraMagnetica.theGame.save);
			this.nextPanel(new ScreenInGameMenu());
		}
		
		return GuiActionEvent.NULL_EVENT;
	}
}
