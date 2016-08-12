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

package org.terramagnetica.game.lvldefault;

/**
 * Cette classe est la classe m�re de toutes les caract�ristiques
 * qui seront ajout�es au personnage de base. Ces caract�ristiques
 * sont repr�sent�es par des entit�s, que l'ont peut g�n�ralement
 * trouver dans les niveaux sous la forme d'objet ramassables. Par
 * d�faut il suffit de marcher sur l'objet pour le ramasser.
 * <p>
 * Le Bonus joue un double role, celui d'entit� et celui
 * de caract�ristiques sp�ciales associ�es au joueur. Elle a donc un
 * double rendu : le rendu de l'entit� que l'ont peut obtenir de mani�re
 * classique par la m�thode {@link #getRender()}, et le rendu du bonus
 * affich� dans l'interface graphique en jeu, en 2D, avec la minimap,
 * le cercle des directions, etc...
 * @author Louis JEAN
 */
public abstract class Bonus extends Entity {
	
	private static final long serialVersionUID = 1L;
	
	protected PlayerDefault myPlayer;
	
	public Bonus() {
		
	}
	
	public void setPlayer(PlayerDefault player) {
		this.myPlayer = player;
	}
	
	/** Inflige les d�gats au bonus. Le bonus peut ne pas r�agir, absorber
	 * ou amplifier les d�gats subis par le joueur.
	 * @param dmg - Le nombre de d�gats subits par le joueur avant application
	 * des bonus
	 * @return Le nouveau nombre de d�gats subis par le joueur, apr�s application
	 * du bonus.*/
	public float takeDamages(float dmg) {
		return dmg;
	}
	
	public void onPlayerWalkingOn(GamePlayingDefault game) {
		game.getPlayer().addBonus(this);
		game.removeEntity(this);
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		PlayerDefault player = game.getPlayer();
		if (getDistancef(player) < .45f) {
			onPlayerWalkingOn(game);
		}
	}
	
	public void updateBonus(long dT, GamePlayingDefault game) {}
}
