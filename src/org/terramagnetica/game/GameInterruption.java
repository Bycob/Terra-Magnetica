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

package org.terramagnetica.game;

/**
 * Une interruption de jeu. Cela se traduit par un arr�t des
 * mises � jour du moteur de jeu, dans le cas d'une cin�matique
 * � l'interieur d'un niveau, ou encore d'une mise en pause
 * du jeu, par exemple.
 * @author Louis JEAN
 *
 */
public abstract class GameInterruption {
	
	/** Ce champ indique si l'interruption est arriv�e � son terme */
	protected boolean finished = false;
	
	public void start() {}
	/**
	 * Cette m�thode est appel�e � chaque boucle du jeu. Elle
	 * doit contenir les actions et les v�rifications que fait
	 * cette interruption au cours d'une boucle (soit toutes les
	 * mises � jour de l'affichage et des entr�es) et c'est elle 
	 * qui normalement met � jour l'�tat de l'interruption
	 * (termin�e ou non-termin�e).
	 */
	public abstract void update();
	/**
	 * Est apell�e pour connaitre l'�tat de l'interruption de jeu
	 * (termin�e ou non-termin�e).
	 * @return {@code true} si l'interruption doit repasser la main
	 * au jeu, {@code false} si elle continue de garder la main.
	 */
	public boolean isFinished() {
		return this.finished;
	}
	
	/** Cette m�thode est appel�e lorsque l'interruption rend la main
	 * au moteur de jeu, pour effectuer les actions n�cessaires. */
	public void onEnd() {}
}
