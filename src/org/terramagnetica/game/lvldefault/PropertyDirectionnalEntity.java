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

import net.bynaryscode.util.Boussole;
import net.bynaryscode.util.maths.MathUtil;

public class PropertyDirectionnalEntity {
	
	private float direction;
	
	/**
	 * La valeur prise par défaut pour la direction est un
	 * angle de 0, soit plein Est.
	 */
	public PropertyDirectionnalEntity() {
		this(0);
	}
	
	/**
	 * @param direction - l'angle de direction en radian
	 */
	public PropertyDirectionnalEntity(float direction) {
		this.setDirection(direction);
	}
	
	public void setDirection(float direction) {
		this.direction = (float) MathUtil.angleMainValue(direction);
	}
	
	public float getDirection() {
		return this.direction;
	}
	
	/**
	 * Donne le point cardinal qui se rapproche le plus de la
	 * direction de cet objet.
	 * @return L'un des 4 points cardinaux (NORD, EST, SUD, OUEST),
	 * le plus proche possible de la direction de cet objet (obtenue
	 * avec la méthode {@link #getDirection()})
	 */
	public Boussole getAverageDirection() {
		return Boussole.getPointCardinalPourAngle(this.direction);
	}
}
