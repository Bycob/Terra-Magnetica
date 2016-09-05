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

package org.terramagnetica.game.lvldefault.lvl2;

import org.terramagnetica.game.lvldefault.IA.AIMovable;
import org.terramagnetica.ressources.io.Codable;

import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.maths.geometric.RectangleInt;

public interface Level2 {

	
	//SALLE 2
	RectangleInt ROOM2_BOUNDS = new RectangleInt(0, 0, 60, 27);
	Vec2i ROOM2_EXIT_CASE = new Vec2i(33, 63);
	String ROOM2_EXIT_CASE_MARK_ID = "lvl2.exit";
	
	public static abstract class CreatureAI extends AIMovable<TheCreature> implements Codable {

		public CreatureAI(TheCreature entity) {
			super(entity);
		}
		
		@Override
		public CreatureAI clone() {
			return (CreatureAI) super.clone();
		}
	}
}
