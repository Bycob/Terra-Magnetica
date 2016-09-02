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

package org.terramagnetica.game;

import java.io.Serializable;

import org.terramagnetica.ressources.RessourcesManager;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.GameIOException;

public class Sauvegarde_Histoire implements Serializable, Codable {
	
	private static final long serialVersionUID = 1L;
	
	public Sauvegarde parent = null;
	
	/** L'identifiant du niveau. Commence à 0 pour le niveau 1. */
	protected int levelID;
	protected Level level;
	protected GameEngine game;
	
	/** Cette variable indique que l'histoire est terminée par
	 * le joueur. */
	protected boolean ended;
	
	public Sauvegarde_Histoire(Sauvegarde parent) {
		this.setLevel(0);
		this.parent = parent;
	}
	
	public int getCurrentLevelID() {
		return levelID;
	}
	
	public Level getLevel() {
		return level;
	}
	
	/** Définit le niveau actuel. */
	public void setLevel(int level) {
		this.levelID = level;
		this.level = null;
		this.game = null;
	}
	
	private void readLevelAndCreateGamePlaying() {
		if (this.level == null) {
			this.level = RessourcesManager.getLevel(this.levelID);
		}
		if (this.level != null) {
			this.game = this.level.createGameEngine();
			this.game.history = true;
		}
		else {
			this.game = null;
		}
	}
	
	public GameEngine getGamePlaying() {
		if (this.game == null) {
			readLevelAndCreateGamePlaying();
		}
		
		return this.game;
	}
	
	/**
	 * Le moteur de jeu passé en paramètre est intégré à la
	 * sauvegarde.
	 * @param toSave
	 */
	public void saveGamePlaying(GameEngine toSave) {
		if (this.level == null) {
			readLevelAndCreateGamePlaying();
		}
		
		if (toSave == null) return;
		if (toSave.hasWon()) {
			return;
		}
		if (toSave.isGameOver()) {
			this.resetGamePlaying();
			return;
		}
		
		this.game = toSave;
		this.game.history = true;
	}
	
	public void resetGamePlaying() {
		if (this.level == null) {
			readLevelAndCreateGamePlaying();
		}
		else {
			this.game = this.level.createGameEngine();
			this.game.history = true;
		}
	}
	
	public void nextLevel() {
		if (!this.ended) this.parent.setAvailable(this.levelID);
		this.levelID++;
		
		if (this.levelID >= RessourcesManager.NB_LEVEL) {
			this.levelID = RessourcesManager.NB_LEVEL;
			this.ended = true;
		}
		
		setLevel(this.levelID);
	}
	
	/** retourne {@code true} si l'histoire a été terminée par
	 * le joueur, {@code false} sinon. */
	public boolean isEnded() {
		return this.ended;
	}
	
	/** Met à jour la sauvegarde dans le cas ou le nombre de
	 * niveaux aurait été modifié. */
	public void update() {
		if (RessourcesManager.NB_LEVEL > this.levelID) {
			if (this.ended) {
				this.ended = false;
			}
		}
		else if (RessourcesManager.NB_LEVEL < this.levelID) {
			this.ended = true;
			this.levelID = RessourcesManager.NB_LEVEL;
		}
		
		setLevel(this.levelID);
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		out.writeIntField(this.levelID, 0);
		out.writeBoolField(this.ended, 1);
		if (this.game != null) out.writeCodableField(this.game, 2);
	}
	
	@Override
	public Sauvegarde_Histoire decode(BufferedObjectInputStream in) throws GameIOException {
		this.setLevel(in.readIntField(0));
		this.ended = in.readBoolField(1);
		
		if (this.levelID >= 0 && this.levelID < RessourcesManager.NB_LEVEL) {
			try {
				this.game = in.readCodableInstanceField(GameEngine.class, 2);
				this.level = this.game.getLevel();
				this.level.levelID = this.levelID + 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return this;
	}
}
