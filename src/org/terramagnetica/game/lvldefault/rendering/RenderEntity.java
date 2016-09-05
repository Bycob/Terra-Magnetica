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

import org.terramagnetica.opengl.engine.Painter;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.AxisAlignedBox3D;

public abstract class RenderEntity {
	
	protected Color4f color = new Color4f(1f, 1f, 1f, 1f);//blanc
	
	/** Rend l'entité avec ce rendu, aux coordonnées indiquées,
	 * avec le {@link Painter} indiqué.
	 * @param x - L'abscisse de l'entité, en cases.
	 * @param y - L'ordonnée de l'entité, en cases.
	 * @param painter - L'objet {@link Painter} utilisé pour rendre
	 * les entités.*/
	public abstract void renderEntity3D(float x, float y, Painter painter);
	
	public AxisAlignedBox3D getRenderBoundingBox(float x, float y) {
		return new AxisAlignedBox3D(x, -y, 0, 0, 0, 0);
	}

	public Color4f getColor() {
		return this.color == null ? null : this.color.clone();
	}
	
	public void setColor(Color4f color) {
		this.color = color;
	}
}
