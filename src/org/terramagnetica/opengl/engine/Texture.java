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

package org.terramagnetica.opengl.engine;

import net.bynaryscode.util.maths.geometric.Vec2d;

public interface Texture {
	
	/** @return L'ID de la texture openGL. */
	int getGLTextureID();
	
	/** Pareil que {@link Texture#setTextureID(int)}.
	 * @return <code>this</code>. */
	Texture withTextureID(int texID);
	
	/** Indique l'ID de la texture openGL.
	 * @param texID - Le nouvel ID. */
	void setTextureID(int texID);
	
	/** @return Chaque point devant être appelé, via glTexCoord(int, int),
	 * pour afficher la texture en entier. */
	Vec2d[] getSTSommets();
	
	/** @return Le nombre de sommets qu'a la texture. */
	int getNbSommets();
	
	/** Une texture doit implémenter la méthode {@code clone()} héritée de
	 * {@link Object} */
	Texture clone();
}
