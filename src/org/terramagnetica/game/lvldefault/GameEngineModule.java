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

import org.terramagnetica.game.GameEngine;
import org.terramagnetica.ressources.io.Codable;

/**
 * Un module du jeu, qui peut être beaucoup de choses.
 * Il définit un aspect du jeu.
 * Il est mis à jour en même temps que le jeu correspondant.
 * <p><u>Exemples : </u>
 * <ul><li>Un objet qui, à chaque instant, détermine l'état des
 * lampes (allumés ou éteind).
 * <li>Un objet qui détermine quel est le portail qui doit être
 * indiqué au joueur.
 * <li>...
 * </ul>
 * @author Louis JEAN
 *
 */
public abstract class GameEngineModule implements Codable, Cloneable {
	
	/** Méthode appelée en début de jeu, pour réinitialiser le module.
	 * <p>On peut considérer que cette méthode est toujours appellée
	 * après {@link #setGame(GameEngine)} sur cet objet. Ainsi, les
	 * traitement necessitant un moteur de jeu peuvent être exécutés
	 * dans cette méthode. */
	public abstract void init();
	public abstract void update(long dT);
	public abstract void setGame(GameEngine game);
	
	@Override
	public GameEngineModule clone() {
		try {
			return (GameEngineModule) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/** Indique si cet aspect doit être sauvegardé lors d'une
	 * sauvegarde de partie du joueur.
	 * @return {@code true} si le module doit être sauvegardé avec
	 * la partie du joueur, {@code false} si le module peut se
	 * réinitialiser à chaque fois et n'a donc pas besoin d'être
	 * sauvegardé.*/
	public boolean shouldSave() {return true;}
}
