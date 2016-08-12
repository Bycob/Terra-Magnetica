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

package org.terramagnetica.ressources.io;

import java.util.HashMap;
import java.util.Map.Entry;

/** Permet de donner des num�ros aux classes lors du codage de fichiers,
 * au lieu de juste �crire le nom de la classe (qui ne peut ainsi �tre
 * sujette de refactoring) */
public class ClassIndexer {
	
	private HashMap<Class<? extends Codable>, String> IDMapping = 
			new HashMap<Class<? extends Codable>, String>();
	
	public ClassIndexer() {
		
	}
	
	public void addID(Class<? extends Codable> clazz, String id) {
		
	}
	
	public String getID(Class<? extends Codable> clazz) {
		String id = this.IDMapping.get(clazz);
		if (id == null) id = clazz.getName();
		return id;
	}
	
	/**
	 * Donne la classe repr�sent�e par l'identifiant pass� en param�tres.
	 * Si aucune classe n'est trouv�e, retourne {@code null}.
	 * @param id
	 * @return
	 */
	public Class<? extends Codable> getClass(String id) {
		if (id == null) throw new NullPointerException("id == null");
		
		for (Entry<Class<? extends Codable>, String> e : this.IDMapping.entrySet()) {
			if (id.equals(e.getValue())) {
				return e.getKey();
			}
		}
		
		return null;
	}
}
