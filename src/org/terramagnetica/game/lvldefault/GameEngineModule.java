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
 * Un module du jeu, qui peut �tre beaucoup de choses.
 * Il d�finit un aspect du jeu.
 * Il est mis � jour en m�me temps que le jeu correspondant.
 * <p><u>Exemples : </u>
 * <ul><li>Un objet qui, � chaque instant, d�termine l'�tat des
 * lampes (allum�s ou �teind).
 * <li>Un objet qui d�termine quel est le portail qui doit �tre
 * indiqu� au joueur.
 * <li>...
 * </ul>
 * @author Louis JEAN
 *
 */
public abstract class GameEngineModule implements Codable, Cloneable {
	
	/** M�thode appel�e en d�but de jeu, pour r�initialiser le module.
	 * <p>On peut consid�rer que cette m�thode est toujours appell�e
	 * apr�s {@link #setGame(GameEngine)} sur cet objet. Ainsi, les
	 * traitement necessitant un moteur de jeu peuvent �tre ex�cut�s
	 * dans cette m�thode. */
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
	
	/** Indique si cet aspect doit �tre sauvegard� lors d'une
	 * sauvegarde de partie du joueur.
	 * @return {@code true} si le module doit �tre sauvegard� avec
	 * la partie du joueur, {@code false} si le module peut se
	 * r�initialiser � chaque fois et n'a donc pas besoin d'�tre
	 * sauvegard�.*/
	public boolean shouldSave() {return true;}
}
