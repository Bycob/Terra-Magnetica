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

package org.terramagnetica.game.lvldefault.lvl2;

import org.terramagnetica.game.lvldefault.rendering.RenderEntityTexture;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Texture;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderAnimationEndOfWall extends RenderEntityTexture {
	
	private boolean left;
	
	public RenderAnimationEndOfWall() {
		super();
	}
	
	/**
	 * Le rendu créé permet de dessiner les bout de murs animés (s'applique
	 * à l'origine pour le {@link PlasmaticWall}) en fondue.
	 * @param left - {@code true} si le rendu dessine l'extrémité gauche du
	 * mur, {@code false} s'il dessine l'extrémité droite.
	 * @param am - voir {@link RenderEntityAnimatedTexture}.
	 */
	public RenderAnimationEndOfWall(boolean left, Texture texture) {
		super(texture);
		
		this.left = left;
	}
	
	@Override
	public void renderAt(Vec3d position, double rotation, Vec3d up, Vec3d scale, Painter painter) {
		updateAnimation();
		
		painter.setPrimitive(Painter.Primitive.QUADS);
		painter.setTexture(this.texture);
		Color4f color = this.getColor();
		
		painter.pushTransformState();
		applyTransforms(position, rotation, up, scale, painter);
		
		int i = 0;
		for (Vec3d vertex : this.getVertice()) {
			//application de la couleur.
			boolean leftSide = i == 0 || i == 3;
			if ((leftSide && this.left) || (!leftSide && !this.left)) {
				painter.setColor(new Color4f(1f, 1f, 1f, 0f));
			}
			else {
				painter.setColor(color);
			}
			
			painter.addVertex(vertex);
			
			i++;
		}
		
		painter.popTransformState();
	}
}
