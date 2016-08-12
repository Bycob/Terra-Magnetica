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

package org.terramagnetica.game.lvldefault;

import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.GameIOException;

public abstract class GameEvent implements Cloneable, Codable {
	
	protected int delay = 0;
	
	public abstract void trigger(GamePlayingDefault game);
	
	public int getDelay() {
		return this.delay;
	}
	
	@Override
	public GameEvent clone() {
		GameEvent clone = null;
		
		try {
			clone = (GameEvent) super.clone();
		} catch (CloneNotSupportedException e) {}
		
		return clone;
	}
	
	/** Certains évènements annulent d'autres qui se passent dans le même tour de boucle
	 * du moteur de jeu.
	 * @return {@code true} si l'évènement passé en paramètre est annulé par celui-ci. */
	public boolean overrides(GameEvent other) {
		return false;
	}
	
	public void setEventLocation(int x, int y) {
		
	}
	
	private boolean repetable = true;
	/** Indique si cet évènement à déjà été déclenché. */
	protected boolean called = false;
	
	public void setEventRepetable(boolean repetable) {
		this.repetable = repetable;
	}
	
	public boolean isEventRepetable() {
		return this.repetable;
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		out.writeIntField(this.delay, 0);
		out.writeBoolField(this.repetable, 1);
		out.writeBoolField(this.called, 2);
	}
	
	@Override
	public GameEvent decode(BufferedObjectInputStream in) throws GameIOException {
		this.delay = in.readIntFieldWithDefaultValue(0, this.delay);
		this.repetable = in.readBoolFieldWithDefaultValue(1, this.repetable);
		this.called = in.readBoolFieldWithDefaultValue(2, this.called);
		return this;
	}
}
