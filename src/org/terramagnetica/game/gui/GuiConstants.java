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

package org.terramagnetica.game.gui;

import net.bynaryscode.util.Color4f;

/**
 * Cette interface contient toutes les constantes de l'interface
 * graphique.
 * @author Louis JEAN
 *
 */
public interface GuiConstants {
	
	/** Temps d'apparition par défaut d'une {@link GuiDefaultTransition}. */
	int MILLIS_APPEAR_DEFAULT = 390;
	
	/** Temps de disparition par défaut d'une {@link GuiDefaultTransition}. */
	int MILLIS_DESTROY_DEFAULT = 390;
	
	/** Vitesse par défaut d'une {@link GuiDefaultTransition} */
	double SPEED_DEFAULT = 2;
	
	/** La couleur par défaut de tous les textes du jeu. */
	Color4f TEXT_COLOR_DEFAULT = new Color4f(255, 234, 0);
	
	/** L'espacement standart entre deux éléments, pour aérer l'interface graphique. */
	double STANDART_GAP = 0.3;
}
