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

package org.terramagnetica.game.lvldefault;

import java.util.Random;

import org.terramagnetica.game.GameEngine;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

public class LampState extends GameAspect {
	
	private GamePlayingDefault game;
	private boolean lampState;
	/** Le mode de changement d'état.
	 * <ul><li>0 pour un changement aléatoire
	 * <li>1 pour aucun changement
	 * <li>2 pour un changement après un temps fixé. Après changement,
	 * le mode revient à 0 (aléatoire)
	 * <li>3 pour mode "apocalyptique" : les lampes restent jaunes 5 sec
	 * à l'entrée dans la salle, puis virent au rouge jusqu'à ce que le
	 * joueur change de salle.
	 * </ul>*/
	private int mode = 0;
	
	
	private transient long timeStaying;
	private transient long lastChangingTime;
	private transient Random lampRand = new Random();
	
	/** Le temps moyen qu'une lampe met à changer d'état, en secondes. */
	public static final int TEMPS_MOYEN_LAMPE = 10;
	/** Le temps minimum qu'une lampe doit passer dans un état avant de
	 * changer, en secondes. */
	public static final int TEMPS_MINIMUM_LAMPE = 5;
	
	public LampState() {
		this(null);
	}
	
	public LampState(GamePlayingDefault game) {
		this.game = game;
		init();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>Ici, redémarre le chrono et place les lampes dans l'état "désactivé"
	 * (les lampes attirent les cristaux et sont jaunes).
	 */
	@Override
	public void init() {
		this.lampState = false;
		this.mode = -1;//la méthode setMode(...) ne fait rien si this.mode == 0;
		setMode(0);
	}

	@Override
	public void update(long dT) {
		switch (this.mode) {
		case 0 :
			updateMode0Random(dT);
			break;
		case 1 :
			updateMode1Permanent(dT);
			break;
		case 2 :
			updateMode2Timed(dT);
			break;
		case 3 :
			updateMode3Hell(dT);
			break;
		}
	}
	
	@Override
	public void setGame(GameEngine game) {
		if (game instanceof GamePlayingDefault) {
			this.game = (GamePlayingDefault) game;
		}
		else throw new IllegalArgumentException("Mauvais type de jeu");
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		out.writeBoolField(this.lampState, 100);
		out.writeLongField(this.timeStaying, 101);
		out.writeLongField(this.lastChangingTime, 102);
		
		out.writeIntField(this.mode, 103);
	}
	
	@Override
	public LampState decode(BufferedObjectInputStream in) throws GameIOException {
		this.lampState = in.readBoolField(100);
		this.timeStaying = in.readLongField(101);
		this.lastChangingTime = in.readLongField(102);
		this.mode = in.readIntFieldWithDefaultValue(103, 0);
		return this;
	}
	
	@Override
	public LampState clone() {
		LampState result = null;
		result = (LampState) super.clone();
		
		result.lampRand = new Random();
		
		return result;
	}
	
	public boolean getLampState() {
		return this.lampState;
	}
	
	
	/** Cette méthode ne change pas l'état des lampes. Ce changement
	 * doit être effectué avant l'appel de {@link #setMode(int)}. */
	private void setMode(int mode) {
		if (this.mode == mode) return;
		
		this.mode = mode;
		switch (this.mode) {
		case 0 :
		case 3 :
			this.timeStaying = TEMPS_MOYEN_LAMPE * 1000 / 2;
		case 2 :
			this.lastChangingTime = this.game == null ? 0l : this.game.getTime();
			break;
		case 1 :
			break;
		}
	}
	
	
	
	//MODE 0 : ALEATOIRE
	
	private void updateMode0Random(long time) {
		
		if (this.game.getTime() - this.lastChangingTime > this.timeStaying) {
			//Définition du temps restant
			final int ecartType = TEMPS_MOYEN_LAMPE * 1000 / 2;
			this.timeStaying = (long) (this.lampRand.nextGaussian() * ecartType + TEMPS_MOYEN_LAMPE * 1000);
			this.timeStaying = this.timeStaying < TEMPS_MINIMUM_LAMPE * 1000 ? TEMPS_MINIMUM_LAMPE * 1000 : this.timeStaying;
			//Définition du temps de début
			this.lastChangingTime = this.game.getTime();
			
			//Changement d'état.
			this.lampState = !this.lampState;
		}
	}
	
	public void setLampStateRandom() {
		this.lampState = !this.lampState;
		this.setMode(0);
	}
	
	
	
	//MODE 1 : PERMANENT
	
	/**
	 * Force les lampes à rester dans un état de façon permanente.
	 * @param state - L'état que les lampes doivent désormais adopter
	 * jusqu'à indication contraire :
	 * <ul><li> {@code true} pour l'état "allumé" (= les lampes sont rouges)
	 * <li> {@code false} pour l'état "éteint" (= les lampes sont jaunes)
	 * </ul>
	 */
	public void setPermanentState(boolean state) {
		this.lampState = state;
		this.setMode(1);
	}
	
	public boolean isLampStatePermanent() {
		return this.mode == 1;
	}
	
	private void updateMode1Permanent(long time) {
		
	}
	
	
	
	//MODE 2 : PROVISOIRE
	
	public void setLampState(boolean state) {
		this.setLampState(state, 5000);
	}
	
	public void setLampState(boolean state, long ms) {
		this.lampState = state;
		this.timeStaying = ms;
		
		this.setMode(2);
	}
	
	private void updateMode2Timed(long time) {
		
		if (this.game.getTime() - this.lastChangingTime > this.timeStaying) {
			this.lampState = !this.lampState;
			this.setMode(0);
		}
	}
	
	
	
	
	//MODE 3 : APOCALYPTIQUE (utilisé pour la première fois dans le niveau bonus : gorge des enfers 1)
	
	/**
	 * Les lampes restent jaunes 5 sec, puis virent au rouge pour le reste
	 * du temps passé dans la salle.
	 */
	public void setLampState_LikeHell() {
		this.lampState = false;
		setMode(3);
	}
	
	private void updateMode3Hell(long time) {
		if (this.game.getTime() - this.lastChangingTime > this.timeStaying) {
			this.lampState = true;
		}
		else {
			this.lampState = false;
		}
	}
}
