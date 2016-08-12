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

package org.terramagnetica.opengl.gui;

/**
 * Classe permettant d'associer à un composant graphique
 * un comportement, qui peut prendre des formes variables.
 * Comme cet objet est mis à jour à chaque fois que le composant
 * associé est déssiné, il est particulièrment adapté aux
 * comportements variant dans le temps.
 * @author Louis JEAN
 *
 */
public interface Behavior {
	
	/** A appeller à chaque fois que l'affichage est rafraichi,
	 * pour mettre à jour le comportement et effectuer les changements
	 * (de dimensions, de couleurs, etc) imposés par ce comportement. */
	public void update();
}
