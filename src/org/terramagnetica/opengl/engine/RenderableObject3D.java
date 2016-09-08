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

import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderableObject3D extends Renderable {
	
	private ArrayList<Vec3d> points = new ArrayList<Vec3d>();
	
	private Primitive primitive = Primitive.QUADS;
	private Texture texture = new TextureQuad();
	
	public RenderableObject3D(Primitive primitive) {
		setPrimitive(primitive);
	}
	
	public void addVertex(Vec3d vertex) {
		if (vertex == null) throw new NullPointerException("vertex == null");
		this.points.add(vertex);
	}
	
	public ArrayList<Vec3d> getVertices() {
		ArrayList<Vec3d> result = new ArrayList<Vec3d>();
		
		for (Vec3d vec : this.points) {
			result.add(vec.clone());
		}
		
		return result;
	}
	
	public void setPrimitive(Primitive p) {
		this.primitive = p;
	}
	
	public Primitive getPrimitive() {
		return this.primitive;
	}
	
	public RenderableObject3D withPrimitive(Primitive p) {
		setPrimitive(p);
		return this;
	}
	
	public void setTexture(Texture texture) {
		this.texture = texture;
	}
	
	public Texture getTexture() {
		return this.texture;
	}
	
	public RenderableObject3D withTexture(Texture texture) {
		this.setTexture(texture);
		return this;
	}
	
	@Override
	public void renderAt(Vec3d position, double rotation, Vec3d up, Vec3d scale, Painter painter) {
		if (this.points.size() < primitive.getVerticeCount()) return;
		
		painter.setPrimitive(Painter.Primitive.QUADS);
		painter.setTexture(this.texture);
		painter.setColor(this.color);
		
		//Si la matrice n'est pas modifiée, pas besoin de vider le tampon du Painter
		painter.pushTransformState();
		
		//ROTATION
		if (this.rotOffset.z != 0) {
			painter.addTransform(Transform.newRotation((float) this.rotOffset.z, new Vec3d(0, 0, 1)));
		}
		if (rotation != 0) {
			if (up.isNull()) throw new IllegalArgumentException("cant rotate around a null vector");
			painter.addTransform(Transform.newRotation((float) rotation, up));
		}
		
		//SCALE
		if (!this.scaleOffset.isNull()) {
			painter.addTransform(Transform.newScale((float) this.scaleOffset.x, (float) this.scaleOffset.y, (float) this.scaleOffset.z));
		}
		if (!scale.isNull()) {
			painter.addTransform(Transform.newScale((float) scale.x, (float) scale.y, (float) scale.z));
		}
		
		//POSITION
		if (!this.posOffset.isNull()) {
			painter.addTransform(Transform.newTranslation(this.posOffset));
		}
		if (!position.isNull()) {
			painter.addTransform(Transform.newTranslation(position));
		}
		
		//DESSIN
		for (Vec3d vertex : this.points) {
			painter.addVertex(vertex);
		}
		
		painter.popTransformState();
	}
}
