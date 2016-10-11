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

package org.terramagnetica.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.terramagnetica.game.gui.GameRendering;
import org.terramagnetica.game.gui.GameWindow;
import org.terramagnetica.openal.MusicStreaming;
import org.terramagnetica.ressources.SoundManager;
import org.terramagnetica.ressources.TextureSet;
import org.terramagnetica.ressources.io.Codable;

public abstract class GameEngine implements Runnable, Codable, Cloneable, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public GameInputBuffer input = new GameInputBuffer();
	public boolean history = false;
	
	protected boolean isGameOver = false;
	protected boolean hasWon = false;
	
	protected boolean automatic = false;
	
	/** true si le jeu tourne. */
	protected boolean running = false;
	
	protected long lastUpdateTime;
	
	public static final int TIME_TO_SLEEP = 20;
	public static final int TICK_TIME = 20;
	/** Le nombre maximum de ticks qui peuvent s'�couler en une seule
	 * mise � jour. */
	public static final int MAX_TICKS = 3;
	
	/** 
	 * Indique si les mises � jour doivent se faire manuellement
	 * ou automatiquement.
	 * <p>Les mises � jour peuvent s'effectuer dans un processus �
	 * part, s�par� du reste du programme. Il est alors recommand�
	 * d'utiliser un tampon s�curis� pour acc�der au jeu, que l'on 
	 * peut obtenir via la m�thode {@link #getBuffer()}.
	 * @param auto - {@code true} si les mises � jour doivent se faire
	 * automatiquement, {@code false} sinon.
	 * @throws IllegalStateException Si le jeu est en cours.
	 * @see #getBuffer()
	 * @see GameBuffer
	 */
	public void setAutomaticUpdate(boolean auto) {
		if (running) {
			this.gameRunningError();
			return;
		}
		
		this.automatic = auto;
	}
	
	public void startGame() {
		
		if (this.running) {
			System.err.println(this.getClass().getName() + ".startGame() : Already running ! Stack trace :");
			Thread.dumpStack();
			return;
		}
		
		if (this.hasWon) return;
		
		if (this.automatic) {
			(new Thread(this)).start();
		}
		else {
			this.running = true;
			this.update(0);
			this.lastUpdateTime = System.currentTimeMillis();
		}
	}

	public void stop(){
		this.running = false;
	}
	
	public void pause() {
		this.stop();
	}
	
	public void resume() {
		this.startGame();
	}
	
	public void interruptGame(GameInterruption i) {
		this.getRender().interruptGame(i);
	}
	
	/** Lorsque le joueur est mort, cette m�thode le fait r�apparaitre */
	public abstract void respawn();
	
	public boolean isRunning() {
		return this.running;
	}
	
	public boolean isPaused() {
		return isRunning();
	}
	
	public boolean isGameOver() {
		return this.isGameOver;
	}
	
	public boolean hasWon() {
		return this.hasWon;
	}
	
	@Override
	public void run() {
		if (!this.automatic) return;
		
		this.running = true;
		long startTime = GameWindow.getSystemTime();
		this.lastUpdateTime = startTime;
		long stopTime;
		long timeToSleep;
		
		while(running){
			startTime = GameWindow.getSystemTime();
			
			update();
			
			this.getBuffer().write(this);
			
			stopTime = GameWindow.getSystemTime();
			timeToSleep = Math.max(0, TIME_TO_SLEEP - stopTime + startTime);
			
			try {
				Thread.sleep(timeToSleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Met � jour le jeu, lorsque le mode de mises � jour automatiques
	 * est d�sactiv�.
	 * <p>Les mises � jour fonctionnent selon un syst�me de ticks : une
	 * mise � jour est effectu�e toutes les 20 ms du moteur de jeu. Chaque
	 * mise � jour sera pr�cis�ment de 20 ms. Si plus de 20 ms se sont
	 * �coul�es depuis l'appel de cette m�thode, il est alors possible que
	 * plusieurs mises � jour de 20 ms chacune se fassent simultan�ment.
	 * <p>Ainsi, le temps est granulaire et chaque tick est s�par� exactement
	 * de 20 ms ce qui �vite les updates trop courtes ou trop longues qui
	 * perturbent le syst�me.
	 */
	public void update() {
		if (!this.running) return;
		
		long time = System.currentTimeMillis();
		long dT = time - this.lastUpdateTime;
		int tickCount = 0;
		final int tickTime = TICK_TIME;
		
		while (dT > tickTime) {
			if (tickCount < MAX_TICKS) {
				this.update(tickTime);
			}
			dT -= tickTime;
			tickCount++;
		}
		
		this.lastUpdateTime = time - dT;
	}
	
	/**
	 * Met � jour le jeu.
	 * @param delta - Le temps qui s'est �coul� depuis la derni�re mise � jour.
	 */
	protected abstract void update(long delta);
	
	/** L�ve une exception indiquant que le jeu est en cours. */
	protected void gameRunningError() {
		throw new IllegalStateException("Le jeu est en cours, modifications impossible");
	}
	
	public abstract GameBuffer getBuffer();
	public abstract GameRendering getRender();
	
	public synchronized GameInputBuffer getInput() {
		return input;
	}
	
	public abstract Level getLevel();
	
	@Override
	public GameEngine clone() {
		GameEngine result = null;
		
		try {
			result = (GameEngine) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		result.input = this.input;
		
		return result;
	}
	
	public List<TextureSet> getTextures() {
		return new ArrayList<TextureSet>();
	}
	
	public List<String> getModels() {
		return new ArrayList<String>();
	}
	
	public MusicStreaming getMusic() {
		int levelID = getLevel().levelID;
		if (levelID == 0) {
			return null;
		}
		MusicStreaming music = SoundManager.loadMusic("level" + levelID + ".ogg");
		return music;
	}
	
	/**
	 * D�truit tous les objets de rendus des diff�rentes composantes du
	 * moteur de jeu (�l�ments de d�cors, entit�s...)
	 */
	public void destroyRenders() {}
	
	/** recr�e tous les objets de rendus des diff�rentes composantes du
	 * moteur de jeu (�l�ments de d�cors, entit�s...) */
	public void recreateRenders() {
		destroyRenders();
	}
}
