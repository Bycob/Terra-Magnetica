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

import java.util.ArrayList;

import org.terramagnetica.opengl.engine.Painter.Primitive;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderableObject3D extends Renderable {
	
	private ArrayList<Vec3d> points = new ArrayList<Vec3d>();
	
	private Primitive primitive = Primitive.QUADS;
	private Texture texture = new TextureQuad();
	private Color4f color = new Color4f();
	
	private Vec3d posOffset = new Vec3d(0, 0, 0);
	private Vec3d rotOffset = new Vec3d(0, 0, 0);
	private Vec3d scaleOffset = new Vec3d(1, 1, 1);
	
	public RenderableObject3D(Primitive primitive) {
		setPrimitive(primitive);
	}
	
	public void setPrimitive(Primitive p) {
		this.primitive = p;
	}
	
	public RenderableObject3D withPrimitive(Primitive p) {
		setPrimitive(p);
		return this;
	}
	
	public void setColor(Color4f color) {
		if (color == null) throw new NullPointerException();
		this.color = color.clone();
	}
	
	public RenderableObject3D withColor(Color4f color) {
		this.setColor(color);
		return this;
	}
	
	public void setPositionOffset(double x, double y, double z) {
		this.posOffset = new Vec3d(x, y, z);
	}
	
	public void setPositionOffset(Vec3d vec) {
		setPositionOffset(vec.x, vec.y, vec.z);
	}
	
	@Override
	public void renderAt(Vec3d position, double rotation, Vec3d up, Vec3d scale, Painter painter) {
		painter.setPrimitive(Painter.Primitive.QUADS);
		painter.setTexture(this.texture);
		painter.setColor(this.color);
		
		//Si la matrice n'est pas modifiée, pas besoin de vider le tampon du Painter
		painter.pushTransformState();

		if (this.rotOffset.z != 0) {//rotation du rendu.
			painter.addTransform(Transform.newRotation((float) this.rotOffset.z, new Vec3d(0, 0, 1)));
		}
		
		if (!this.scaleOffset.isNull()) {
			painter.addTransform(Transform.newScale((float) this.scaleOffset.x, (float) this.scaleOffset.y, (float) this.scaleOffset.z));
		}
		
		if (!this.posOffset.isNull()) {
			painter.addTransform(Transform.newTranslation(this.posOffset));
		}
		
		for (Vec3d vertex : this.points) {
			painter.addVertex(vertex);
		}
		
		painter.popTransformState();
	}
}
