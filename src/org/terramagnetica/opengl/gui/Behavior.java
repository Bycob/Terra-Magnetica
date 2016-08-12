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
 * Classe permettant d'associer � un composant graphique
 * un comportement, qui peut prendre des formes variables.
 * Comme cet objet est mis � jour � chaque fois que le composant
 * associ� est d�ssin�, il est particuli�rment adapt� aux
 * comportements variant dans le temps.
 * @author Louis JEAN
 *
 */
public interface Behavior {
	
	/** A appeller � chaque fois que l'affichage est rafraichi,
	 * pour mettre � jour le comportement et effectuer les changements
	 * (de dimensions, de couleurs, etc) impos�s par ce comportement. */
	public void update();
}
