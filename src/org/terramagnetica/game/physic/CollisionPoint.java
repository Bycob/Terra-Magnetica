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

package org.terramagnetica.game.physic;

public class CollisionPoint {
	
	private float time;
	
	private Hitbox hitbox1, hitbox2;
	
	public CollisionPoint(float time, Hitbox hb1, Hitbox hb2) {
		this.setTime(time);
	}
	
	public float getTime() {
		return this.time;
	}
	
	public void setTime(float time) {
		this.time = time;
	}
	
	public void setHitboxes(Hitbox hitbox1, Hitbox hitbox2) {
		this.hitbox1 = hitbox1;
		this.hitbox2 = hitbox2;
	}
	
	public Hitbox[] getHitboxes() {
		return new Hitbox[] {this.hitbox1, this.hitbox2};
	}
	
	public Hitbox getOtherHitbox(Hitbox me) {
		if (me == this.hitbox1) {
			return this.hitbox2;
		}
		else {
			return this.hitbox1;
		}
	}
	
	void goToPoint() {
		this.hitbox1.applyVelocity(this.time - this.hitbox1.timeOffset);
		this.hitbox2.applyVelocity(this.time - this.hitbox2.timeOffset);
	}
}
