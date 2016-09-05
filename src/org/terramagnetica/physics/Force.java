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

package org.terramagnetica.physics;

import net.bynaryscode.util.maths.geometric.Vec2f;

public class Force implements Cloneable {
	private float forceX, forceY;
	
	public Force(float forceX, float forceY) {
		this.setForce(forceX, forceY);
	}
	
	public void setForce(float forceX, float forceY) {
		this.setForceX(forceX);
		this.setForceY(forceY);
	}
	
	public Vec2f getForce() {
		return new Vec2f(this.forceX, this.forceY);
	}
	
	public void setForceX(float forceX) {
		this.forceX = forceX;
	}
	
	public float getForceX() {
		return this.forceX;
	}
	
	public void setForceY(float forceY) {
		this.forceY = forceY;
	}
	
	public float getForceY() {
		return this.forceY;
	}
	
	@Override
	public Force clone() {
		Force result = null;
		
		try {
			result = (Force) super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(forceX);
		result = prime * result + Float.floatToIntBits(forceY);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Force)) {
			return false;
		}
		Force other = (Force) obj;
		if (Float.floatToIntBits(forceX) != Float.floatToIntBits(other.forceX)) {
			return false;
		}
		if (Float.floatToIntBits(forceY) != Float.floatToIntBits(other.forceY)) {
			return false;
		}
		return true;
	}
}
