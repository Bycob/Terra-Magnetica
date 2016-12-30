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

import org.lwjgl.glfw.GLFW;
import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.opengl.gui.GuiDialog;
import org.terramagnetica.opengl.gui.GuiDialogMessage;
import org.terramagnetica.opengl.gui.GuiWindow;
import org.terramagnetica.opengl.miscellaneous.Screenshot;
import org.terramagnetica.ressources.ExternalFilesManager;
import org.terramagnetica.ressources.ModelLoader;
import org.terramagnetica.ressources.RessourcesManager;
import org.terramagnetica.ressources.SoundManager;
import org.terramagnetica.ressources.TexturesLoader;

public class GameWindow {
	
	private static GameWindow theWindow;
	
	public static void createInstance() {
		if (theWindow != null) {
			throw new IllegalStateException("Instance déjà créée");
		}
		setInstance(new GameWindow());
	}
	
	protected static void setInstance(GameWindow instance) {
		theWindow = instance;
	}
	
	public static GameWindow getInstance() {
		return GameWindow.theWindow;
	}
	
	
	
	protected boolean gameRunning;
	private int FPS = 60;
	private long time;
	
	private GuiDialog dialog = null;
	
	/** Démarre l'application. On ne sort de cette méthode que quand l'application est terminée. */
	public synchronized void start() {
		GuiWindow window = GuiWindow.getInstance();
		
		init();
		
		this.gameRunning = true;
		
		//variables screenshot
		boolean f1KeyPressed = false;
		
		while (this.gameRunning) {
			
			refresh();
			long startTime = GuiWindow.getTimeNanos();
			
			window.update();
			
			//fermeture du dialogue
			if (this.dialog != null && this.dialog.shouldPerformClose()) {
				window.getContentPane().remove(this.dialog);
				this.dialog = null;
			}
			
			//fermeture de la fenêtre
			if (window.isCloseRequested()) {
				this.stop();
			}
			
			//screenshots
			if (window.isKeyPressed(GLFW.GLFW_KEY_F1)) {
				if (!f1KeyPressed) {
					f1KeyPressed = true;
					ExternalFilesManager.saveScreenshot(Screenshot.takeScreenshot(window));
				}
			}
			else {
				if (f1KeyPressed) {
					f1KeyPressed = false;
				}
			}
			//-----
			
			long totalTime = GuiWindow.getTimeNanos() - startTime;
			//System.out.println((float) (totalTime) / 1000000);
			//TODO profiling
			
			sync(startTime);
			//System.out.println(((float) (GuiWindow.getTimeNanos() - startTime) / 1000000) + "\n");
		}
		
		destroyApp();
	}
	
	/** @param frameDuration en nanosecondes*/
	void sync(long frameStartTime) {
		long frameDuration = GuiWindow.getTimeNanos() - frameStartTime;
		long nanosWanted = 1000000000L / this.FPS;
		
		while (nanosWanted - frameDuration > 1000000) { // 1 ms
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			frameDuration = GuiWindow.getTimeNanos() - frameStartTime;
		}
		
		while (nanosWanted > frameDuration) {
			Thread.yield();
			frameDuration = GuiWindow.getTimeNanos() - frameStartTime;
		}
	}
	
	/** arrête le jeu */
	public void stop() {
		this.gameRunning = false;
	}

	/** effectue des actions diverses qu'il faut executer à chaque
	 * tour de jeu. */
	protected void refresh() {
		time = GuiWindow.getTimeMillis();
	}
	
	/** Prépare l'affichage, charge les textures...<br>
	 * Puis joue l'animation du début. */
	protected void init() {
		GuiWindow window = GuiWindow.getInstance();
		
		try {
			window.setTitle("Terra Magnetica - V" + TerraMagnetica.VERSION);
			
			RessourcesManager.loadIcon();
			window.setIcon(RessourcesManager.tmIcon);
			
			window.createWindow();
			
			SoundManager.initialize();
			RessourcesManager.loadGameStartScreen();
		} catch (Exception e) {
			e.printStackTrace();
			TerraMagnetica.theGame.log.addErrorMessage("Impossible de lancer le jeu", e);
			throw new Error("Arrêt de l'application");
		}
		
		TitleScreen title = new TitleScreen();
		title.drawWaiting();
		
		loadRessources();
		
		setPanel(title);
	}
	
	protected void loadRessources() {
		RessourcesManager.loadRessourcesGame();
		
		//Chargement du shader terramagnetica
		
	}
	
	/** détruit l'affichage, libère les ressources de mémoires et quitte l'application. */
	protected void destroyApp() {
		TexturesLoader.destroyRessources();
		ModelLoader.unloadAll();
		SoundManager.destroy();
		
		if (TerraMagnetica.theGame != null) TerraMagnetica.theGame.onClose();
		
		GuiWindow.getInstance().destroy();
	}
	
	public void setFPS(int FPS) {
		this.FPS = FPS;
	}
	
	public int getFPS() {
		return this.FPS;
	}
	
	public void displayMessage(String msg) {
		if (this.dialog != null) {
			throw new IllegalStateException("Pas deux messages à la fois s'il te plait, c'est un peu trop.");
		}
		
		this.dialog = new GuiDialogMessage(msg);
		GuiWindow.getInstance().getContentPane().add(this.dialog);
	}
	
	public void setPanel(GameScreen newPanel) {
		newPanel.appear();
		GuiWindow.getInstance().setContentPane(newPanel);
	}
	
	public boolean isRunning() {
		return this.gameRunning;
	}
	
	public long getTime() {
		return this.time;
	}
}
