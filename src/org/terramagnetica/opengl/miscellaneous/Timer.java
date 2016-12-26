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
	
	/** D�marre le chronom�tre, s'il ne l'est pas d�j�. */
	public void start() {
		if (this.running) return;
		if (this.paused) {
			this.paused = false;
			this.lastPause = 0L;
		}
		
		this.start = GuiWindow.getTimeMillis();
		this.running = true;
	}
	
	/** Arr�te et r�initialise le chronom�tre. Si le chronom�tre
	 * est arr�t�, cette m�thode ne fait rien et retourne 0.
	 * @return le temps du chronom�tre au moment o� il s'est
	 * arr�t�, en ms. */
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
	
	/** R�initialise le chronom�tre et le red�marre. Si le chronom�tre
	 * est d�j� � l'arr�t, cette m�thode d�marre simplement le chronom�tre
	 * et retourne 0.
	 * @return le temps du chronom�tre au moment o� il se r�initialise. */
	public long restart() {
		long result = stop();
		
		start();
		return result;
	}
	
	/** Met en pause le chronom�tre.<br>
	 * On peut connaitre le temps au moyen de la m�thode {@link #getTime()} */
	public void pause() {
		if (!this.paused && this.running) {
			this.paused = true;
			this.lastPause = GuiWindow.getTimeMillis();
		}
	}
	
	/** Si le chronom�tre est en pause, il reprend le chronom�trage depuis
	 * l'endroit o� il s'�tait arr�t�.<br>
	 * Dans le cas contraire, cette m�thode n'a aucun effet. */
	public void resume() {
		if (this.paused && this.running) {
			this.start += GuiWindow.getTimeMillis() - lastPause;
			this.paused = false;
		}
	}
	
	/** @return Le temps actuel du chronom�tre, en ms. */
	public long getTime() {
		if (!this.running) return 0L;
		
		if (this.paused) {
			return this.lastPause - this.start;
		}
		
		long time = GuiWindow.getTimeMillis() - this.start;
		
		return time;
	}
	/**
	 * @return {@code true} si le chronom�tre est lanc�. S'il est
	 * en pause, {@code true} est aussi renvoy�.
	 */
	public boolean isRunning() {
		return this.running;
	}
	
	/**
	 * @return {@code true} si le chronom�tre est actuelement en train
	 * de chronom�trer. Cela exclue les moment o� il est en pause.
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
