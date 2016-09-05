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
	 * permet de l'identifier pour savoir à quel panneau de contrôle
	 * il est affilié.
	 * @return La couleur du {@link BarrierStateListener}. Celle-ci
	 * sera parfois représentée sur le rendu.
	 */
	Color4f getColor();
	
	/**
	 * Définit l'état "allumé" ou "éteint" du {@link BarrierStateListener}.
	 * Cette méthode est appellée quand un panneau est désactivé par le joueur.
	 * L'intégralité des objets affiliés à ce panneau passe alors de "allumé"
	 * à "éteint" via cette méthode.
	 * @param state - Le nouvel état du {@link BarrierStateListener} :
	 * {@code true} pour "allumé", {@code false} pour "éteint".
	 */
	void setState(boolean state);
}
