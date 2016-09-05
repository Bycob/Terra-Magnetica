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
 * Cette interface d�finit les entit�s ayant une influence sur
 * les �l�ments magn�tiques mobiles du jeu.
 * <p>Chaque �l�ment magn�tique ne peut �tre controll� que par
 * <u>une seule</u> influence majeure, g�n�ralement la plus proche.
 * @author Louis JEAN
 */
public interface InfluenceMagnetiqueMajeure {
	
	float MAXIMUM_DISTANCE = (float) Entity.MAX_DISTANCE;
	
	/**
	 * Cette m�thode contr�le magn�tiquement l'entit� pass�e en
	 * param�tre.
	 * @param game - Le jeu de r�f�rence, dans lequel doivent normalement
	 * �tre situ�s influence magn�tique et entit� pass�e en param�tre.
	 * @param delta - Le temps qui s'est �coul� depuis la derni�re mise
	 * � jour.
	 * @param controlled - L'entit� contr�l�e.
	 */
	void controls(GamePlayingDefault game, long delta, EntityMoving controlled);
	
	/**
	 * Indique si l'entit� pass�e en param�tre est autoris�e � avoir des
	 * collisions avec les autres entit�s. Peut s'utiliser, par exemple,
	 * dans le cas de la lampe magn�tique classique : l'�l�ment magn�tique
	 * sera interdit de collision si il est plac� autour de la lampe, il ne
	 * pourra donc pas �tre pouss� par le joueur.
	 * @param e - L'entit� concern�e.
	 * @return {@code true} si les collisions doivent se d�rouler normalement,
	 * {@code false} sinon.
	 */
	boolean hasPermissionForCollision(EntityMoving e);
	
	/**
	 * Indique si l'influence a la possibilit� d'exercer son influence sur
	 * la trajectoire des objets magn�tiques.
	 * @param e - L'entit� sur laquelle exercer l'influence.
	 * @return 
	 */
	boolean isAvailableFor(EntityMoving e);
}
