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

import java.io.Serializable;

import org.terramagnetica.ressources.io.Codable;

public abstract class Level implements Codable, Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	/** Indique l'identifiant du niveau, lorsqu'il s'agit d'un
	 * niveau du jeu : le premier niveau porte le numéro 1. Dans
	 * le cas contraire (exemple : niveau créé par le joueur) le
	 * "levelID" est 0. */
	public int levelID = 0;
	
	public abstract GameEngine createGameEngine();
	
	@Override
	public Level clone() {
		Level level = null;
		
		try {
			level = (Level) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return level;
	}
	
	public boolean isRunnable() {return true;}
}
