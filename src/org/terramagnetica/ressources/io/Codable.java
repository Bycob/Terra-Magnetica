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

/**
 * L'interface des objets que l'on peut sérialiser grâce à
 * un {@link BufferedObjectOutputStream} et désérialiser avec
 * un {@link BufferedObjectInputStream}.<p>
 * Il est impératif pour cet objet d'avoir un constructeur
 * sans paramètre, car il est possible que la désérialisation
 * instancie dynamiquement un objet lu.
 * @author Louis JEAN
 *
 */
public interface Codable {
	
	public void code(BufferedObjectOutputStream out) throws GameIOException;
	public Codable decode(BufferedObjectInputStream in) throws GameIOException;
}
