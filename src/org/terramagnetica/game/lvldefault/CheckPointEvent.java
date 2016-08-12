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

import net.bynaryscode.util.maths.geometric.Vec2i;

/**
 * L'évènement qui, à son déclenchement, modifie le checkpoint actuel
 * de la salle.
 * @author Louis JEAN
 */
public class CheckPointEvent extends GameEvent {
	
	private CheckPoint checkPoint;
	
	public CheckPointEvent() {
		this.checkPoint = null;
	}
	
	public CheckPointEvent(int locationX, int locationY, int roomID) {
		this.checkPoint = new CheckPoint(locationX, locationY, roomID);
	}
	
	@Override
	public void setEventLocation(int x, int y) {
		this.checkPoint.setLocation(x, y);
	}
	
	@Override
	public void trigger(GamePlayingDefault game) {
		if (game != null) {
			game.setCheckPoint(this.checkPoint);
		}
	}
	

	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		
		if (this.checkPoint != null) {
			out.writeIntField(this.checkPoint.getRoomID(), 100);
			Vec2i loc = this.checkPoint.getLocation();
			out.writeIntField(loc.x, 101); out.writeIntField(loc.y, 102);
		}
	}
	
	@Override
	public CheckPointEvent decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		if (in.hasField(100)) {
			this.checkPoint = new CheckPoint(in.readIntField(101), in.readIntField(102), in.readIntField(100));
		}
		return this;
	}
}
