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

public final class RenderableFactory {

	/**
	 * Crée un {@link RenderableCompound} composé d'un même rendu d'une case,
	 * répété un certain nombre de fois. Cela peut servir par exemple pour
	 * des objets représentant une petite partie d'un mur à longueur variable.
	 * <p>L'objet de rendu obtenu sera centré sur les coordonnées de l'entité.
	 * Le Renderable situé au centre du dessin sera en position (0, 0).
	 * @param render - Le rendu à répeter.
	 * @param size - La quantité de rendu à concatener.
	 * @param horizontal - {@code true} si les rendus sont rangés de gauche à
	 * droite, {@code false} s'ils sont rangés de haut en bas.
	 * @return Un {@link RenderableCompound} correspondant à la description ci-dessus.
	 */
	public static RenderableCompound createCaseArrayRender(Renderable render, int size, boolean horizontal) {
		
		RenderableCompound r = new RenderableCompound();
		
		int startIndex = - (size / 2);
		
		for (int i = startIndex ; i < startIndex + size ; i++) {
			Renderable unit = render.clone();
			unit.setPositionOffset(horizontal ? i : 0, horizontal ? 0 : i, 0);
			
			r.addRenders(unit);
		}
		
		return r;
	}
}
