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

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.Page;
import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.gui.GuiActionEvent;
import org.terramagnetica.opengl.gui.GuiBorderLayout;
import org.terramagnetica.opengl.gui.GuiButtonText1;
import org.terramagnetica.opengl.gui.GuiContainer;
import org.terramagnetica.opengl.gui.GuiFrameContainer;
import org.terramagnetica.opengl.gui.GuiLabel;
import org.terramagnetica.opengl.gui.GuiLayout002;
import org.terramagnetica.opengl.gui.GuiScrollPanel;
import org.terramagnetica.opengl.gui.GuiScrollPanel.ScrollBar;
import org.terramagnetica.ressources.RessourcesManager;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;

public class ScreenBeforeLevelH extends GameScreen {
	
	private int id = 0;
	
	private GuiPage desc;
	private GuiButtonText1 go;
	private GuiButtonText1 retour;
	
	private GuiScrollPanel shouldBeActualised;
	
	public ScreenBeforeLevelH() {
		this(0);
	}
	
	public ScreenBeforeLevelH(int id) {
		this.id = id;
		
		this.go = new GuiButtonText1(0.1, 0.1, 0.9, -0.1, "Jouer");
		this.retour = new GuiButtonText1(-0.9, 0.1, -0.1, -0.1, "Retour");
		Page page = new Page(RessourcesManager.getLevelPlot(id));
		this.desc = new GuiPage(page);
		
		GuiLabel title = new GuiLabel("Niveau " + (id + 1), 20, 0, 0);
		GuiFrameContainer cont = new GuiFrameContainer();
		GuiContainer buttonPan = new GuiContainer();
		GuiContainer titlePan = new GuiContainer();
		GuiContainer scrollPan = new GuiContainer();
		GuiScrollPanel scroll = new GuiScrollPanel(null, -1.5, 1, 1.5, -1);
		GuiBorderLayout lay = new GuiBorderLayout();
		
		lay.setBorderHeight(0.3);
		cont.setLayout(lay);
		scrollPan.setLayout(new GuiLayout002());
		
		buttonPan.setBackground(new Color4f(0, 0, 0, 25));
		titlePan.setBackground(new Color4f(0, 0, 0, 25));
		scroll.setColor(new Color4f(209, 182, 0));
		title.setColor(TEXT_COLOR_DEFAULT);
		
		scroll.setScrollBarsUsed(ScrollBar.VERTICAL);
		scroll.fillBackground(false);
		
		cont.add(buttonPan, GuiBorderLayout.BOTTOM);
		cont.add(titlePan, GuiBorderLayout.TOP);
		cont.add(scrollPan, GuiBorderLayout.CENTER);
		
		buttonPan.add(this.go);
		buttonPan.add(this.retour);
		scroll.setContent(this.desc);
		scrollPan.add(scroll);
		titlePan.add(title);
		
		this.add(cont);
		
		this.shouldBeActualised = scroll;
		
		GuiDefaultTransition tt = new GuiDefaultTransition(this);
		tt.add(this.go, GuiDefaultTransition.DROITE);
		tt.add(this.retour, GuiDefaultTransition.GAUCHE);
		this.setTransition(tt);
	}
	
	@Override
	public int timeToAppear() {
		return MILLIS_APPEAR_DEFAULT;
	}

	@Override
	public int timeToDestroy() {
		return 0;
	}
	
	@Override
	protected void drawComponent(Painter painter) {
		drawDefaultBackground(painter);
	}
	
	@Override
	protected void drawChildren(Painter painter) {
		super.drawChildren(painter);
		
		if (this.shouldBeActualised != null) {
			this.shouldBeActualised.actualise();
			this.shouldBeActualised = null;
		}
	}
	
	@Override
	public GuiActionEvent processLogic() {
		if (this.getState() != VISIBLE) {
			return super.processLogic();
		}
		
		if (this.go.processLogic() == GuiActionEvent.CLICK_EVENT) {
			TerraMagnetica.theGame.runHistory();
			this.nextPanel(new ScreenGamePlaying());
		}
		
		if (this.retour.processLogic() == GuiActionEvent.CLICK_EVENT) {
			TexturesLoader.unloadTextureSet(GameRessources.getTextureSetByLevel(this.id + 1));
			this.nextPanel(new ScreenInGameMenu());
		}
		
		return GuiActionEvent.NULL_EVENT;
	}
}
