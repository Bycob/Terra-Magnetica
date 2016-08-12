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

public class MapLandscape {
	
	private int caseX, caseY;
	
	public MapLandscape() {
		this(0, 0);
	}
	
	public MapLandscape(int caseX, int caseY) {
		this.caseX = caseX;
		this.caseY = caseY;
	}
	
	public int getCaseX() {
		return caseX;
	}
	
	public void setCaseX(int caseX) {
		this.caseX = caseX;
	}
	
	public int getCaseY() {
		return caseY;
	}
	
	public void setCaseY(int caseY) {
		this.caseY = caseY;
	}
	
	public Vec2i getLocation() {
		return new Vec2i(this.caseX, this.caseY);
	}
	
	public void setLocation(int caseX, int caseY) {
		this.caseX = caseX;
		this.caseY = caseY;
	}
}
