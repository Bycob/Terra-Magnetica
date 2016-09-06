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

import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.Vec2f;

/**
 * Cette classe repr�sente un objet de rendu compos� de plusieurs
 * autres objets de rendu distincts. Il est utilis� dans le cas o�
 * une seule entit� poss�de plusieurs �l�ments qui ont chacuns leur
 * propre rendu : en effet la m�tode {@link Entity#getRender()} doit
 * retourner un seul et m�me objet.
 * @author Louis JEAN
 *
 */
public class RenderCompound extends RenderEntity {
	
	private List<Entity> entities = new ArrayList<Entity>();
	private List<RenderEntity> renders = new ArrayList<RenderEntity>();
	
	public RenderCompound() {
		
	}
	
	public RenderCompound(RenderEntity... renders) {
		this.renders = Util.createList(renders);
	}
	
	public RenderCompound(Entity... entities) {
		this.entities = Util.createList(entities);
	}
	
	/**
	 * Ajoute une entit� qu'il faudra rendre en m�me temps que
	 * le reste. Si l'entit� a d�j� �t� ajout�e, elle ne sera
	 * pas ajout�e de nouveau.
	 * @param e - L'entit� � dessiner en m�me temps que cet objet
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
	
	/**
	 * Ajoute un rendu � dessiner dans cet objet. Si le rendu pass� en
	 * param�tre a d�j� �t� ajout� pr�cedemment, il ne sera pas ajout�
	 * de nouveau.
	 * @param r - Le rendu ajout�.
	 */
	public void addRender(RenderEntity r) {
		if (r == null) throw new NullPointerException();
		if (!this.renders.contains(r)) this.renders.add(r);
	}
	
	public void removeRender(RenderEntity r) {
		this.renders.remove(r);
	}
	
	public void removeEntityToRender(Entity toDelete) {
		this.entities.remove(toDelete);
	}

	public ArrayList<RenderEntity> getRenders() {
		ArrayList<RenderEntity> list = new ArrayList<RenderEntity>();
		list.addAll(renders);
		return list;
	}
	
	@Override
	public void renderEntity3D(float x, float y, Painter painter) {
		for (RenderEntity render : this.renders) {
			render.renderEntity3D(x, y, painter);
		}
		for (Entity e : this.entities) {
			Vec2f c = e.getPositionf();
			RenderEntity r = e.getRender();
			if (r != null) {
				r.renderEntity3D(c.x, c.y, painter);
			}
		}
	}
	
	/**
	 * Cr�e un {@link RenderCompound} compos� d'un m�me rendu d'une case,
	 * r�p�t� un certain nombre de fois. Cela peut servir par exemple pour
	 * les entit�s prenant la forme d'un mur � longueur variable.
	 * <p>L'objet de rendu obtenu sera centr� sur les coordonn�es de l'entit�.
	 * Lorsqu'il sera dessin�, le rendu r�p�t� situ� au centre du dessin sera
	 * repr�sent� aux coordonn�es de l'entit�.
	 * @param render - Le rendu � r�peter.
	 * @param size - La quantit� de rendu � concatener.
	 * @param horizontal - {@code true} si les rendus sont rang�s de gauche �
	 * droite, {@code false} s'ils sont rang�s de haut en bas.
	 * @return Un {@link RenderCompound} correspondant � la description ci-dessus.
	 */
	public static RenderCompound createCaseArrayRender(RenderEntityDefault render,
			int size, boolean horizontal) {
		
		RenderCompound r = new RenderCompound();
		
		int startIndex = - (size / 2);
		
		for (int i = startIndex ; i < startIndex + size ; i++) {
			r.addRender(render.clone().withTranslation(horizontal ? i : 0, horizontal ? 0 : i));
		}
		
		return r;
	}
}
