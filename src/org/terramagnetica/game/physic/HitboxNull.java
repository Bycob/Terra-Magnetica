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

import net.bynaryscode.util.maths.geometric.Vec2;

public class HitboxNull extends Hitbox {
	
	private static final long serialVersionUID = 1L;

	public HitboxNull() {
		
	}
	
	@Override
	protected CollisionPoint getCurrentCollisionPoint(Hitbox other, float time) {
		return null;
	}

	@Override
	public void doNextCollision() {
		
	}

	@Override
	public boolean contains(Vec2 point) {
		return false;
	}

	@Override
	public boolean intersects(Hitbox other) {
		return false;
	}

}
