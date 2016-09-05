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
import org.terramagnetica.ressources.io.GameIOException;

public abstract class AbstractLamp extends CaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	/** <tt>true</tt> : activé (rouge), <tt>false</tt> : désactivé (jaune) */
	protected boolean state = false;
	protected boolean didStateChanged = true;
	protected boolean inverted = false;
	protected boolean locked = false;
	
	protected AbstractLamp() {
		super();
	}
	
	protected AbstractLamp(int x, int y) {
		super(x, y);
	}
	
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}
	
	public boolean isInverted() {
		return this.inverted;
	}
	
	public boolean isLocked() {
		return this.locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public boolean getState() {
		return this.state;
	}
	
	@Override
	public void updateLogic(long delta, GamePlayingDefault game) {
		super.updateLogic(delta, game);
		
		boolean oldState = this.state;
		
		if (this.locked) {
			this.state = this.inverted;
		}
		else {
			this.state = game.getAspect(LampState.class).getLampState();
			if (this.inverted) this.state = !this.state;
		}
		
		this.didStateChanged = oldState != this.state;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other) || !(other instanceof AbstractLamp))
			return false;
		AbstractLamp lamp = (AbstractLamp) other;
		
		if (lamp.inverted != this.inverted) {
			return false;
		}
		if (lamp.locked != this.locked) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		
		out.writeBoolField(this.inverted, 200);
		out.writeBoolField(this.locked, 201);
	}
	
	@Override
	public AbstractLamp decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		
		this.inverted = in.readBoolFieldWithDefaultValue(200, this.inverted);
		this.locked = in.readBoolFieldWithDefaultValue(201, this.locked);
		
		return this;
	}
}
