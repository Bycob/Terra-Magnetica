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

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.game.GameBuffer;
import org.terramagnetica.game.GameInputBuffer.InputKey;
import org.terramagnetica.game.GameInterruption;
import org.terramagnetica.game.GameEngine;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.openal.MusicStreaming;
import org.terramagnetica.opengl.gui.GuiActionEvent;
import org.terramagnetica.opengl.gui.GuiButtonText1;
import org.terramagnetica.opengl.gui.GuiLabel;
import org.terramagnetica.ressources.SoundManager;
import org.terramagnetica.utile.GameException;

public class ScreenGamePlaying extends GameScreen {

	public GameEngine game;
	public GameBuffer buf;
	
	private boolean modeAuto = false;
	private boolean isTest = false;
	private boolean interrupted = false;
	private GameInterruption interruption;
	
	private GameRendering renderGame;
	
	
	private TransluscentScreen pausedScreen;
	private GuiButtonText1 resum;
	private GuiButtonText1 back;
	private GuiButtonText1 tryAgain;
	private GuiButtonText1 restart;
	private GuiLabel pauselbl;
	
	private TransluscentScreen gameOverScreen;
	private GuiButtonText1 tryAgain2;
	private GuiButtonText1 restart2;
	private GuiButtonText1 back2;
	private GuiLabel gameoverlbl;
	
	public ScreenGamePlaying() {
		this(TerraMagnetica.theGame.engine);
	}
	
	public ScreenGamePlaying(GameEngine game) {
		this.setSoundPlayed("");
		
		this.game = game;
		
		if (this.game != null) {
			this.buf = this.game.getBuffer();
			this.renderGame = this.game.getRender();
		}
		
		//ECRAN DE PAUSE
		this.pausedScreen = new TransluscentScreen();
		
		this.pauselbl = new GuiLabel("Pause", 20, 0, 0.9);
		this.resum = new GuiButtonText1(-0.9, 0.5, 0.9, 0.3, "Reprendre la partie");
		this.tryAgain = new GuiButtonText1(-0.9, 0.1, 0.9, -0.1, "Réapparaître au dernier checkpoint");
		this.restart = new GuiButtonText1(0.1, 0.1, 0.9, -0.1, "Recommencer le niveau");this.restart.setVisible(false);
		this.back = new GuiButtonText1(-0.9, -0.3, 0.9, -0.5, "Menu");
		
		this.pauselbl.setColor(GuiConstants.TEXT_COLOR_DEFAULT);
		
		this.pausedScreen.add(this.pauselbl);
		this.pausedScreen.add(this.back);
		this.pausedScreen.add(this.resum);
		this.pausedScreen.add(this.tryAgain);
		//this.pausedScreen.add(this.restart);
		
		this.pausedScreen.setVisible(false);
		
		//ECRAN DE GAME OVER
		this.gameOverScreen = new TransluscentScreen();
		
		this.gameoverlbl = new GuiLabel("Vous êtes mort", 20, 0, 0.9);
		this.tryAgain2 = new GuiButtonText1(-0.9, 0.4, 0.9, 0.2, "Réapparaître au dernier checkpoint");
		this.back2 = new GuiButtonText1(-0.9, - 0.2, 0.9, - 0.4, "Menu");
		
		this.gameoverlbl.setColor(GuiConstants.TEXT_COLOR_DEFAULT);
		
		this.gameOverScreen.add(this.back2);
		this.gameOverScreen.add(this.tryAgain2);
		this.gameOverScreen.add(this.gameoverlbl);
		
		this.gameOverScreen.setVisible(false);
		
		//---------------------------------------
		this.add(this.renderGame);
		this.add(this.pausedScreen);
		this.add(this.gameOverScreen);
		
		//---------------------------------------
		this.setModeAuto(false);
	}
	
	@Override
	public GuiActionEvent processLogic() {
		if (getState() != VISIBLE) {
			return super.processLogic();
		}
		
		GameInterruption i;
		if ((i = this.renderGame.nextInterruption()) != null) {
			this.interruptGame(i);
		}
		
		//Mise à jour des entrées clavier
		this.game.getInput().listenInput();
		
		if (!this.interrupted) {//Si le jeu n'est pas interrompu.
			if (!this.game.isRunning()) {//Au démarrage
				this.runGame();
			}
			else {//Mise à jour du moteur de jeu
				if (!this.modeAuto) {
					this.game.update();
				}
			}
			
			if (this.game.isGameOver()) {
				this.interruptGame(new GameOver());
			}
			
			if (this.game.hasWon() && !this.isTest) {
				
				if (this.game.history) {
					TerraMagnetica.theGame.save.story.nextLevel();
					this.nextPanel(new ScreenYouWonH());
				}
				else {
					this.nextPanel(new ScreenFreeGame());
				}
			}
			
			int escKey = Keyboard.KEY_ESCAPE;
			if (TerraMagnetica.theGame != null) {
				escKey = TerraMagnetica.theGame.options.getInputID(InputKey.KEY_ECHAP);
			}
			if (Keyboard.isKeyDown(escKey) || !(Display.isActive())) {
				this.interruptGame(new Pause());
			}
			
			updateMouseGrabbing(true);
		}
		else {//si le jeu est interrompu
			this.interruption.update();
			
			if (this.interruption.isFinished()) {
				
				this.interruption.onEnd();
				this.interruption = null;
				this.interrupted = false;
				
				this.game.resume();
			}
			
			updateMouseGrabbing(false);
		}
		
		return GuiActionEvent.NULL_EVENT;
	}
	
	/**
	 * Met à jour l'état de la souris.
	 * @param flag - {@code true} si la souris peut être masquée, {@code false}
	 * sinon.
	 */
	private void updateMouseGrabbing(boolean flag) {
		Mouse.setGrabbed(flag && GameWindow.getSystemNanos() - Mouse.getEventNanoseconds() > 1000000000l);// 1s
	}
	
	/** Démarre le jeu et la musique. */
	public void runGame() {
		this.game.startGame();
		
		MusicStreaming music = this.game.getMusic();
		SoundManager.setMusic(music);
		SoundManager.setMusicLooped(true);
		SoundManager.playMusic();
	}
	
	/** Fait respawn le joueur, par exemple lorsqu'il vient de mourrir. */
	public void respawn() {
		boolean paused = this.game.isPaused();
		
		game.stop();
		game.respawn();
		game.startGame();
		
		if (paused) {
			game.pause();
		}
	}
	
	/** A la différence de la méthode {@link #respawn()}, cette
	 * méthode redémarre le niveau au lieu de seulement faire respawn
	 * le joueur. */
	public void restartLevel() {
		game.stop();
		
		if (game.history) {
			TerraMagnetica.theGame.runHistory();
		}
		else {
			try {
				TerraMagnetica.theGame.runLevel(game.getLevel());
			} catch (GameException e) {
				//Impossible car le niveau a déjà été lancé avec succès
				//au moins une fois, il n'est donc pas corrompu.
			}
		}
		
		nextPanel(new ScreenGamePlaying());
	}
	
	/** déclenche le retour à l'écran de sélection des niveaux, ou
	 * au menu si le jeu était en mode histoire. */
	public void goBack() {
		game.stop();
		
		if (this.isTest) return;
		
		if (game.history) {
			nextPanel(new ScreenInGameMenu());
		}
		else {
			nextPanel(new ScreenFreeGame());
		}
	}
	
	public void interruptGame(GameInterruption i) {
		if (i == null) return;
		
		if (this.interruption != null) {
			this.interruption.onEnd();
			this.interruption = null;
		}
		
		this.interrupted = true;
		this.game.pause();
		this.interruption = i;
		this.interruption.start();
	}
	
	@Override
	public void onDestroy() {
		if (this.game.history) {
			TerraMagnetica.theGame.exitHistory(this.game);
		}
		else {
			TerraMagnetica.theGame.exitGamePlaying();
		}
		
		SoundManager.stopMusic();
		
		updateMouseGrabbing(false);
	}
	
	/** définit le mode de mise à jour du jeu : automatique, ou manuel.
	 * @param flag - <code>true</code> si automatique, <code>false</code>
	 * si manuel.
	 * @see GamePlayingDefault#setAutomaticUpdate(boolean) */
	public void setModeAuto(boolean flag) {
		this.game.setAutomaticUpdate(flag);
		this.modeAuto = flag;
		if (flag) {
			this.buf = this.game.getBuffer();
		}
		this.renderGame.setModeAuto(flag);
	}
	
	/** Définit la propriété <tt>isTest</tt>. Si celle-ci est à <tt>true</tt>
	 * alors le niveau est en test, ce qui signifie qu'il n'a pas été lancé
	 * depuis un des panneaux du jeu, mais par l'éditeur de niveau. Dans ce
	 * cas, cliquer sur le bouton "menu" ne fera pas revenir au menu et sera
	 * sans effet. */
	public void setTest(boolean isTest) {
		this.isTest = isTest;
		
		this.back.setVisible(!isTest);
		this.back2.setVisible(!isTest);
	}
	
	@Override
	public int timeToAppear() {
		return 0;
	}
	
	@Override
	public int timeToDestroy() {
		return 0;
	}
	
	public class Pause extends GameInterruption {
		
		@Override
		public void start() {
			pausedScreen.setVisible(true);
			renderGame.pause();
		}
		
		@Override
		public void update() {
			if (resum.processLogic() == GuiActionEvent.CLICK_EVENT) {
				this.finished = true;
			}
			
			if (back.processLogic() == GuiActionEvent.CLICK_EVENT) {
				goBack();
			}
			
			if (tryAgain.processLogic() == GuiActionEvent.CLICK_EVENT) {
				respawn();
				
				this.finished = true;
			}
			
			if (restart.processLogic() == GuiActionEvent.CLICK_EVENT) {
				restartLevel();
			}
		}
		
		@Override
		public void onEnd() {
			renderGame.resume();
			ScreenGamePlaying.this.pausedScreen.setVisible(false);
		}
	}
	
	public class GameOver extends GameInterruption {
		
		@Override
		public void start() {
			gameOverScreen.setVisible(true);
			renderGame.pause();
		}
		
		@Override
		public void update() {
			if (back2.processLogic() == GuiActionEvent.CLICK_EVENT) {
				goBack();
			}
			
			if (tryAgain2.processLogic() == GuiActionEvent.CLICK_EVENT) {
				respawn();
				
				this.finished = true;
			}
		}
		
		@Override
		public void onEnd() {
			gameOverScreen.setVisible(false);
			renderGame.resume();
		}
	}
}
