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

package org.terramagnetica.opengl.miscellaneous;

import org.terramagnetica.opengl.gui.GuiWindow;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.GameIOException;

public class Timer implements Codable, Cloneable {
	
	private boolean running;
	private long start;
	
	private boolean paused;
	private long lastPause;
	
	public Timer() {
		this.running = false;
		this.start = 0L;
		
		this.paused = false;
		this.lastPause = 0L;
	}
	
	/** Démarre le chronomètre, s'il ne l'est pas déjà. */
	public void start() {
		if (this.running) return;
		if (this.paused) {
			this.paused = false;
			this.lastPause = 0L;
		}
		
		this.start = GuiWindow.getTimeMillis();
		this.running = true;
	}
	
	/** Arrête et réinitialise le chronomètre. Si le chronomètre
	 * est arrêté, cette méthode ne fait rien et retourne 0.
	 * @return le temps du chronomètre au moment où il s'est
	 * arrêté, en ms. */
	public long stop() {
		long time = this.getTime();
		this.start = 0L;
		
		if (!running) {
			time = 0L;
		}
		
		this.running = false;
		this.paused = false;
		
		return time;
	}
	
	/** Réinitialise le chronomètre et le redémarre. Si le chronomètre
	 * est déjà à l'arrêt, cette méthode démarre simplement le chronomètre
	 * et retourne 0.
	 * @return le temps du chronomètre au moment où il se réinitialise. */
	public long restart() {
		long result = stop();
		
		start();
		return result;
	}
	
	/** Met en pause le chronomètre.<br>
	 * On peut connaitre le temps au moyen de la méthode {@link #getTime()} */
	public void pause() {
		if (!this.paused && this.running) {
			this.paused = true;
			this.lastPause = GuiWindow.getTimeMillis();
		}
	}
	
	/** Si le chronomètre est en pause, il reprend le chronomètrage depuis
	 * l'endroit où il s'était arrêté.<br>
	 * Dans le cas contraire, cette méthode n'a aucun effet. */
	public void resume() {
		if (this.paused && this.running) {
			this.start += GuiWindow.getTimeMillis() - lastPause;
			this.paused = false;
		}
	}
	
	/** @return Le temps actuel du chronomètre, en ms. */
	public long getTime() {
		if (!this.running) return 0L;
		
		if (this.paused) {
			return this.lastPause - this.start;
		}
		
		long time = GuiWindow.getTimeMillis() - this.start;
		
		return time;
	}
	/**
	 * @return {@code true} si le chronomètre est lancé. S'il est
	 * en pause, {@code true} est aussi renvoyé.
	 */
	public boolean isRunning() {
		return this.running;
	}
	
	/**
	 * @return {@code true} si le chronomètre est actuelement en train
	 * de chronomètrer. Cela exclue les moment où il est en pause.
	 */
	public boolean isTiming() {
		return this.running && !this.paused;
	}
	
	@Override
	public Timer clone() {
		Timer result = null;
		try {
			result = (Timer) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		out.writeBoolField(this.running, 0);
		out.writeBoolField(this.paused, 1);
		out.writeLongField(this.start, 2);
		out.writeLongField(this.lastPause, 3);
	}

	@Override
	public Timer decode(BufferedObjectInputStream in) throws GameIOException {
		this.running = in.readBoolField(0);
		this.paused = in.readBoolField(1);
		this.start = in.readLongField(2);
		this.lastPause = in.readLongField(3);
		return this;
	}
}
