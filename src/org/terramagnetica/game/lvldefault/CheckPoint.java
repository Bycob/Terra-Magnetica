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

import net.bynaryscode.util.maths.geometric.Vec2i;

public class CheckPoint {
	
	private Vec2i location;
	private int roomID;
	
	public CheckPoint(int locationX, int locationY, int roomID) {
		this(new Vec2i(locationX, locationY), roomID);
	}
	
	public CheckPoint(Vec2i location, int roomID) {
		this.location = location.clone();
		this.roomID = roomID;
	}
	
	public Vec2i getLocation() {
		return location.clone();
	}
	
	public void setLocation(int locationX, int locationY) {
		this.setLocation(new Vec2i(locationX, locationY));
	}
	
	public void setLocation(Vec2i location) {
		this.location = location.clone();
	}
	
	public int getRoomID() {
		return roomID;
	}
	
	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}
}
