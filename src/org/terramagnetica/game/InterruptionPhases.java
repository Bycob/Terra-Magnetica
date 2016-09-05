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

import java.util.ArrayList;

import org.terramagnetica.opengl.miscellaneous.Timer;

/**
 * Un type d'interruption utilis� surtout lors des animations
 * � l'int�rieur d'un niveau.
 * <p>Cette interruption fonctionne par phases successives.
 * Voir {@link Phase} pour plus de d�tails.
 * @author Louis JEAN
 */
public class InterruptionPhases extends GameInterruption {
	
	/**
	 * Une {@link Phase} repr�sente un �v�nement qui se d�roule
	 * dans une {@link GameInterruption}. Cette classe est surtout
	 * utilis�e dans le cas d'animations � l'interieur du niveau,
	 * qui vont se d�rouler en plusieurs phases successives : scrolling,
	 * animation d'un �l�ment, puis scrolling � nouveau... Chacune de
	 * ces �tapes est g�r�e par une {@link Phase}.
	 * @author Louis JEAN
	 */
	public abstract class Phase {
		
		/** Met � jour la phase.
		 * @param ms - Le nombre de millisecondes �coul�es depuis le
		 * d�but de la phase.*/
		public abstract void update(long ms);
		/** @return Le nombre de millisecondes que dure la phase. Cette m�thode peut
		 * aussi renvoyer -1, dans ce cas la dur�e de la phase est infinie (le r�sultat
		 * de cette m�thode peut alors changer au cours de la r�alisation de la phase). */
		public long duration() {
			return 0;
		}
		/** Effectue les actions n�cessaires pour d�marrer la phase. */
		public void onStart() {}
		/** Effectue les actions n�cessaires pour clore la phase. */
		public void onEnd() {}
	}
	
	private ArrayList<Phase> phaseList = new ArrayList<Phase>();
	private int phase = 0;
	
	private Timer chronoPhase = new Timer();
	
	@Override
	public void start() {
		this.phase = 0;
		if (this.phaseList.size() != 0) this.phaseList.get(0).onStart();
	}
	
	@Override
	public void update() {
		if (this.phase >= this.phaseList.size()) {
			this.chronoPhase.stop();
			this.finished = true;
		}
		else {
			Phase current = this.phaseList.get(this.phase);
			long duration = current.duration();
			
			if (!this.chronoPhase.isRunning()) {
				this.chronoPhase.start();
			}
			
			long ms = this.chronoPhase.getTime();
			
			if (duration != -1) {
				//La phase a une dur�e d�termin�e.
				current.update(Math.min(duration, ms));
				
				if (ms >= duration) {
					nextPhase(current);
				}
			}
			else {
				//On est dans le cas o� la phase a une dur�e ind�finie.
				current.update(ms);
				
				if (current.duration() == 0) {
					nextPhase(current);
				}
			}
		}
	}
	
	private void nextPhase(Phase current) {
		this.chronoPhase.restart();
		current.onEnd();
		
		//changement de phase
		this.phase++;
		if (this.phase < this.phaseList.size()) {
			this.phaseList.get(this.phase).onStart();
		}
	}
	
	@Override
	public void onEnd() {
		
	}
	
	public void addPhase(Phase phase) {
		if (phase == null) throw new NullPointerException();
		this.phaseList.add(phase);
	}
}
