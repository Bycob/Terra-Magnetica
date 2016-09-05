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

import net.bynaryscode.util.Color4f;

public interface BarrierStateListener {
	
	/**
	 * Donne la couleur du {@link BarrierStateListener}. Celle-ci
	 * permet de l'identifier pour savoir � quel panneau de contr�le
	 * il est affili�.
	 * @return La couleur du {@link BarrierStateListener}. Celle-ci
	 * sera parfois repr�sent�e sur le rendu.
	 */
	Color4f getColor();
	
	/**
	 * D�finit l'�tat "allum�" ou "�teint" du {@link BarrierStateListener}.
	 * Cette m�thode est appell�e quand un panneau est d�sactiv� par le joueur.
	 * L'int�gralit� des objets affili�s � ce panneau passe alors de "allum�"
	 * � "�teint" via cette m�thode.
	 * @param state - Le nouvel �tat du {@link BarrierStateListener} :
	 * {@code true} pour "allum�", {@code false} pour "�teint".
	 */
	void setState(boolean state);
}
