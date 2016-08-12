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

import net.bynaryscode.util.Color4f;

/**
 * L'entit� impl�mentant cette interface est un objectif � atteindre
 * par le joueur. Elle est donc indiqu�e dans l'interface graphique par
 * une fl�che de couleur orient� vers elle.
 * @author Louis JEAN */
public interface IGoal {
	
	/**
	 * @return La couleur de la fl�che indiquant cette entit�.
	 */
	public Color4f getIndicationColor();
}
