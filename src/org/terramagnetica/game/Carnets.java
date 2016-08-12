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

/** Les carnets sont toutes les indications données au joueur au
 * cours de son aventure dans le monde de Terra Magnetica, sous
 * forme de papier à carte sur lequel sont inscrits les notes et
 * schémas de l'aventurier.
 * <p>La classe {@link Carnets} possède les fonctionnalités suivantes :
 * <ul><li>Charger et donner la texture d'un feuillet correspondant
 * à l'identifiant d'une indication spécifique (méthode statique) ;
 * <li>Dire si une indication particulière (une page) à déjà été consulté
 * par le joueur, dans ce cas elle ne s'activera plus en jeu mais
 * pourra être reconsultée dans la section spécifique du jeu (menu 
 * {@literal ->} Carnets)  */
public class Carnets implements Codable {
	
	private static HashMap<String, TextureQuad> loadedPagesMap = new HashMap<String, TextureQuad>();
	
	public static TextureQuad loadPage(String id) {
		return null;
	}
	
	//IDENTIFIANTS DES FEUILLETS
	
	
	/** Cette liste répertorie tous les feuillets déjà consultés
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
