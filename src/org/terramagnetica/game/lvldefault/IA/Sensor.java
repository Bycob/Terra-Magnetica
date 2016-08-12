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

package org.terramagnetica.game.lvldefault.IA;

import org.terramagnetica.game.lvldefault.GamePlayingDefault;

public abstract class Sensor implements Cloneable {
	
	protected GamePlayingDefault game;
	
	public void setGame(GamePlayingDefault game) {
		this.game = game;
	}
	
	protected void checkGameInitialized() {
		if (this.game == null) throw new UnsupportedOperationException("Ce sensor n'est lié à aucun monde.");
	}
	
	/** Cette méthode est appelée avant les traitements de l'intelligence
	 * artificielle pour mettre à jour les informations sur le jeu,
	 * recueillies par ce capteur.
	 * <p>A l'appel de cette méthode, le champs {@link #game} à déjà été initialisé. */
	public abstract void update(AIBase ai);
	
	@Override
	public Sensor clone() {
		Sensor clone = null;
		
		try {
			clone = (Sensor) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return clone;
	}
}
