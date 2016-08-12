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

import java.util.ArrayList;
import java.util.HashMap;

import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.GameIOException;

/** Les carnets sont toutes les indications donn�es au joueur au
 * cours de son aventure dans le monde de Terra Magnetica, sous
 * forme de papier � carte sur lequel sont inscrits les notes et
 * sch�mas de l'aventurier.
 * <p>La classe {@link Carnets} poss�de les fonctionnalit�s suivantes :
 * <ul><li>Charger et donner la texture d'un feuillet correspondant
 * � l'identifiant d'une indication sp�cifique (m�thode statique) ;
 * <li>Dire si une indication particuli�re (une page) � d�j� �t� consult�
 * par le joueur, dans ce cas elle ne s'activera plus en jeu mais
 * pourra �tre reconsult�e dans la section sp�cifique du jeu (menu 
 * {@literal ->} Carnets)  */
public class Carnets implements Codable {
	
	private static HashMap<String, TextureQuad> loadedPagesMap = new HashMap<String, TextureQuad>();
	
	public static TextureQuad loadPage(String id) {
		return null;
	}
	
	//IDENTIFIANTS DES FEUILLETS
	
	
	/** Cette liste r�pertorie tous les feuillets d�j� consult�s
	 * par le joueur. */
	public ArrayList<String> readenList = new ArrayList<String>();
	
	public Carnets() {
		
	}
	
	public boolean isReaden(String id) {
		return this.readenList.contains(id);
	}

	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		
	}

	@Override
	public Carnets decode(BufferedObjectInputStream in) throws GameIOException {
		return this;
	}
}
