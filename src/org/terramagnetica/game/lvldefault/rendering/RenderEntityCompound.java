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

package org.terramagnetica.game.lvldefault.rendering;

import java.util.ArrayList;
import java.util.List;

import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Renderable;

import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.Vec2f;

/**
 * Cette classe représente un objet de rendu composé de plusieurs
 * autres objets de rendu distincts. Il est utilisé dans le cas où
 * une seule entité possède plusieurs éléments qui ont chacuns leur
 * propre rendu : en effet la métode {@link Entity#getRender()} doit
 * retourner un seul et même objet.
 * @author Louis JEAN
 *
 */
public class RenderEntityCompound extends RenderObject {
	
	private List<Entity> entities = new ArrayList<Entity>();
	
	public RenderEntityCompound() {
		
	}
	
	public RenderEntityCompound(Entity... entities) {
		this.entities = Util.createList(entities);
	}
	
	/**
	 * Ajoute une entité qu'il faudra rendre en même temps que
	 * le reste. Si l'entité a déjà été ajoutée, elle ne sera
	 * pas ajoutée de nouveau.
	 * @param e - L'entité à dessiner en même temps que cet objet
	 * de rendu.
	 */
	public void addEntityToRender(Entity e) {
		if (e == null) throw new NullPointerException();
		if (!this.entities.contains(e)) this.entities.add(e);
	}
	
	public ArrayList<Entity> getEntitiesToRender() {
		ArrayList<Entity> list = new ArrayList<Entity>();
		list.addAll(this.entities);
		return list;
	}
	
	public void removeEntityToRender(Entity toDelete) {
		this.entities.remove(toDelete);
	}

	@Override
	public void renderEntity3D(float x, float y, Painter painter) {
		for (Entity e : this.entities) {
			Vec2f c = e.getPositionf();
			Renderable r = e.getRender();
			if (r != null) {
				r.renderAt(c.x, c.y, 0, painter);
			}
		}
	}
	
	//TODO [RENDU]
	
	/**
	 * Crée un {@link RenderEntityCompound} composé d'un même rendu d'une case,
	 * répété un certain nombre de fois. Cela peut servir par exemple pour
	 * les entités prenant la forme d'un mur à longueur variable.
	 * <p>L'objet de rendu obtenu sera centré sur les coordonnées de l'entité.
	 * Lorsqu'il sera dessiné, le rendu répété situé au centre du dessin sera
	 * représenté aux coordonnées de l'entité.
	 * @param render - Le rendu à répeter.
	 * @param size - La quantité de rendu à concatener.
	 * @param horizontal - {@code true} si les rendus sont rangés de gauche à
	 * droite, {@code false} s'ils sont rangés de haut en bas.
	 * @return Un {@link RenderEntityCompound} correspondant à la description ci-dessus.
	 */
	public static RenderEntityCompound createCaseArrayRender(RenderEntityTexture render,
			int size, boolean horizontal) {
		
		RenderEntityCompound r = new RenderEntityCompound();
		
		int startIndex = - (size / 2);
		
		for (int i = startIndex ; i < startIndex + size ; i++) {
			r.addRender(render.clone().withTranslation(horizontal ? i : 0, horizontal ? 0 : i));
		}
		
		return r;
	}
}
