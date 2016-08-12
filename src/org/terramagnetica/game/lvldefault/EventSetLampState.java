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

public class EventSetLampState extends GameEvent {
	
	private boolean lampState;
	private long timeStaying = -1;
	
	public EventSetLampState() {
		this(false);
	}
	
	public EventSetLampState(boolean state) {
		this.setLampState(state);
	}
	
	public void setLampState(boolean flag) {
		this.lampState = flag;
	}
	
	public boolean getLampState() {
		return this.lampState;
	}
	
	public void setTimeStaying(long time) {
		if (time < 0) throw new IllegalArgumentException("le temps ne peut pas être négatif !");
		this.timeStaying = time;
	}
	
	public void setNoTimeRestrictions() {
		this.timeStaying = -1;
	}
	
	public long getTimeStaying() {
		return this.timeStaying;
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		
		out.writeBoolField(this.lampState, 100);
		out.writeLongField(this.timeStaying, 101);
	}

	@Override
	public EventSetLampState decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		
		this.lampState = in.readBoolField(100);
		this.timeStaying = in.readLongField(101);
		return this;
	}
	
	@Override
	public void trigger(GamePlayingDefault game) {
		if (this.timeStaying == -1) {
			game.getAspect(LampState.class).setLampState(this.lampState);
		}
		else {
			game.getAspect(LampState.class).setLampState(this.lampState, this.timeStaying);
		}
	}
	
	@Override
	public boolean overrides(GameEvent other) {
		if (other instanceof EventForceLampState) {
			if (((EventForceLampState) other).getEventType() == EventForceLampState.FORCE_NOTHING) {
				return true;
			}
		}
		
		return false;
	}
}
