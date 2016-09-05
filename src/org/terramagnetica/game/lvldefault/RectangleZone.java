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

import net.bynaryscode.util.maths.geometric.Vec2;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Rectangle;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class RectangleZone implements Zone {
	
	private RectangleDouble zoneRect;
	
	public RectangleZone() {
		this(new RectangleDouble());
	}
	
	public RectangleZone(Rectangle rect) {
		this.setRectangleZone(rect);
	}
	
	public void setRectangleZone(Rectangle zoneRect) {
		this.zoneRect = zoneRect.asDouble();
	}
	
	public RectangleDouble getRectangleZone() {
		return this.zoneRect;
	}
	
	@Override
	public boolean isInZone(Vec2 point) {
		Vec2d cdble = point.asDouble();
		return this.zoneRect.contains(cdble);
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		out.writeDoubleField(this.zoneRect.xmin, 200);
		out.writeDoubleField(this.zoneRect.xmax, 201);
		out.writeDoubleField(this.zoneRect.ymin, 202);
		out.writeDoubleField(this.zoneRect.ymax, 203);
	}
	
	@Override
	public RectangleZone decode(BufferedObjectInputStream in) throws GameIOException {
		this.zoneRect = new RectangleDouble();
		
		this.zoneRect.xmin = in.readDoubleField(200);
		this.zoneRect.xmax = in.readDoubleField(201);
		this.zoneRect.ymin = in.readDoubleField(202);
		this.zoneRect.ymax = in.readDoubleField(203);
		return this;
	}
}
