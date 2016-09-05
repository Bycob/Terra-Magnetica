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

package org.terramagnetica.game.lvldefault;

/**
 * Cette interface définit les entités ayant une influence sur
 * les éléments magnétiques mobiles du jeu.
 * <p>Chaque élément magnétique ne peut être controllé que par
 * <u>une seule</u> influence majeure, généralement la plus proche.
 * @author Louis JEAN
 */
public interface InfluenceMagnetiqueMajeure {
	
	float MAXIMUM_DISTANCE = (float) Entity.MAX_DISTANCE;
	
	/**
	 * Cette méthode contrôle magnétiquement l'entité passée en
	 * paramètre.
	 * @param game - Le jeu de référence, dans lequel doivent normalement
	 * être situés influence magnétique et entité passée en paramètre.
	 * @param delta - Le temps qui s'est écoulé depuis la dernière mise
	 * à jour.
	 * @param controlled - L'entité contrôlée.
	 */
	void controls(GamePlayingDefault game, long delta, EntityMoving controlled);
	
	/**
	 * Indique si l'entité passée en paramètre est autorisée à avoir des
	 * collisions avec les autres entités. Peut s'utiliser, par exemple,
	 * dans le cas de la lampe magnétique classique : l'élément magnétique
	 * sera interdit de collision si il est placé autour de la lampe, il ne
	 * pourra donc pas être poussé par le joueur.
	 * @param e - L'entité concernée.
	 * @return {@code true} si les collisions doivent se dérouler normalement,
	 * {@code false} sinon.
	 */
	boolean hasPermissionForCollision(EntityMoving e);
	
	/**
	 * Indique si l'influence a la possibilité d'exercer son influence sur
	 * la trajectoire des objets magnétiques.
	 * @param e - L'entité sur laquelle exercer l'influence.
	 * @return 
	 */
	boolean isAvailableFor(EntityMoving e);
}
