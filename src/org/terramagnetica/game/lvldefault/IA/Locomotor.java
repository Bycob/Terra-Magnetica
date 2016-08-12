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

package org.terramagnetica.game.lvldefault.IA;

import org.terramagnetica.game.lvldefault.EntityMoving;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;

import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec2i;

/** Cette classe gère les déplacements des créatures intelligentes, leur
 * donnant notament des méthodes pour suivre des chemins prédéterminés. */
public class Locomotor {
	
	private EntityMoving movedEntity;
	
	public Locomotor(EntityMoving movedEntity) {
		this.movedEntity = movedEntity;
		if (movedEntity == null) {
			throw new NullPointerException();
		}
	}

	/** Définit les mouvements de l'entité pour qu'elle se déplace vers la case
	 * indiquée. La case doit être adjacente à celle sur laquelle se situe l'entité.
	 * @param vel - la vitesse de déplacement de cet entité*/
	public void moveTo(GamePlayingDefault game, Vec2i cCase, float vel) {
		//Détermination du point vers lequel va se diriger la créature : le plus proche parmi ces quatres
		Vec2f[] pointsToMoveTo = new Vec2f[] {
			new Vec2f(cCase.x + 0.5f, cCase.y),//NORD
			new Vec2f(cCase.x + 1, cCase.y + 0.5f),//EST
			new Vec2f(cCase.x + 0.5f, cCase.y + 1),//SUD
			new Vec2f(cCase.x, cCase.y + 0.5f)//OUEST
		};
		
		Vec2f loc = this.movedEntity.getCoordonnéesf();
		Vec2f nearestPoint = null;
		
		for (Vec2f point : pointsToMoveTo) {
			if (nearestPoint == null) {
				nearestPoint = point;
			}
			else {
				nearestPoint = MathUtil.getDistance(loc, point) < MathUtil.getDistance(loc, nearestPoint) ?
						point : nearestPoint;
			}
		}
		System.out.println(nearestPoint);
		System.out.println(loc);
		
		//Mouvement effectif.
		this.movedEntity.setMovement(nearestPoint.x - loc.x, nearestPoint.y - loc.y);
		this.movedEntity.setVelocity(vel);
	}
}
