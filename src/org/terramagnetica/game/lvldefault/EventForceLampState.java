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

import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

public class EventForceLampState extends GameEvent {
	
	private int evt_type;
	
	public static final int FORCE_ON = 0;
	public static final int FORCE_OFF = 1;
	public static final int FORCE_NOTHING = 2;
	
	public EventForceLampState() {
		this(FORCE_NOTHING);
	}
	
	public EventForceLampState(int evtType) {
		this.setEventType(evtType);
	}
	
	public void setEventType(int evtType) {
		if (evtType < 0 || evtType > 2) throw new IllegalArgumentException("type d'évennement inconnu.");
		this.evt_type = evtType;
	}
	
	public int getEventType() {
		return this.evt_type;
	}
	
	@Override
	public boolean overrides(GameEvent other) {
		if (other instanceof EventForceLampState) {
			EventForceLampState e = (EventForceLampState) other;
			if (e.evt_type == FORCE_NOTHING && this.evt_type != FORCE_NOTHING) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void trigger(GamePlayingDefault game) {
		switch (this.evt_type) {
		case FORCE_ON :
			game.getAspect(LampState.class).setPermanentState(true);
			break;
		case FORCE_OFF :
			game.getAspect(LampState.class).setPermanentState(false);
			break;
		case FORCE_NOTHING :
			game.getAspect(LampState.class).setLampStateRandom();
			break;
		}
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		out.writeIntField(this.evt_type, 100);
	}
	
	@Override
	public EventForceLampState decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		this.evt_type = in.readIntField(100);
		return this;
	}
}
