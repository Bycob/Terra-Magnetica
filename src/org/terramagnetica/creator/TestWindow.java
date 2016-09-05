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

package org.terramagnetica.creator;

import javax.swing.JOptionPane;

import org.terramagnetica.game.GameEngine;
import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.Level;
import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.game.gui.GameScreen;
import org.terramagnetica.game.gui.GameWindow;
import org.terramagnetica.game.gui.ScreenGamePlaying;
import org.terramagnetica.opengl.gui.GuiTextPainter;
import org.terramagnetica.ressources.SoundManager;
import org.terramagnetica.ressources.TexturesLoader;
import org.terramagnetica.utile.GameException;

public class TestWindow extends GameWindow {

	protected Level theLevel;
	protected GameEngine game;
	
	public Creator pointer;
	
	public TestWindow(Level lvl) {
		this.theLevel = lvl;
		
		setInstance(this);
	}
	
	@Override
	public void refresh() {
		super.refresh();
		
		if (this.game.hasWon()) {
			this.gameRunning = false;
		}
	}
	
	@Override
	protected void init() {
		super.init();
		
		try {
			TerraMagnetica.theGame.runLevel(this.theLevel);
			this.game = TerraMagnetica.theGame.engine;
		}
		catch (GameException e) {
			JOptionPane.showMessageDialog(null,
					"Impossible de lancer le niveau : la position de départ du joueur n'est pas définie",
					"Impossible de lancer le niveau", JOptionPane.ERROR_MESSAGE);
		}
		
		ScreenGamePlaying sgp = new ScreenGamePlaying(this.game);
		sgp.setTest(true);
		setPanel(sgp);
	}
	
	@Override
	public void loadRessources() {
		TexturesLoader.loadTextureSet(GameRessources.guiTextureSet);
		
		GuiTextPainter.init();
		
		SoundManager.loadAudio();
	}
	
	@Override
	public void setPanel(GameScreen panel) {
		if (panel instanceof ScreenGamePlaying) {
			super.setPanel(panel);
		}
	}
	
	@Override
	protected void destroyApp() {
		super.destroyApp();
		
		if (this.game.hasWon()) {
			JOptionPane.showMessageDialog(pointer, "Vous avez gagné !", "Victoire", JOptionPane.INFORMATION_MESSAGE);
		}
		this.game.recreateRenders();
	}
}
