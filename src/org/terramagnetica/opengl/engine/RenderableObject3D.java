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
import net.bynaryscode.util.maths.geometric.AxisAlignedBox3D;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderableObject3D extends Renderable {
	
	private class VertexData {
		Color4f color;
		Vec2f texCoord;
		Vec3d normal;
		
		VertexData(Vec3d normal, Vec2f texCoord, Color4f color) {
			this.color = color;
			this.texCoord = texCoord;
			this.normal = normal;
		}
	}
	
	private ArrayList<Vec3d> points = new ArrayList<Vec3d>();
	private ArrayList<VertexData> pointsData = new ArrayList<VertexData>();
	
	protected Primitive primitive;
	protected Texture texture = new TextureQuad();
	
	public RenderableObject3D() {
		this(Primitive.QUADS);
	}
	
	public RenderableObject3D(Primitive primitive) {
		setPrimitive(primitive);
	}
	
	private void onStructuralChanges() {
		this.boundingBox = null;
	}
	
	public void addVertex(Vec3d vertex) {
		addVertex(vertex, null, null, null);
	}
	
	public void addVertex(Vec3d vertex, Color4f color) {
		addVertex(vertex, null, null, color);
	}
	
	public void addVertex(Vec3d vertex, Vec3d normal) {
		addVertex(vertex, normal, null, null);
	}
	
	public void addVertex(Vec3d vertex, Vec2f texCoord) {
		addVertex(vertex, null, texCoord, null);
	}
	
	public void addVertex(Vec3d vertex, Vec3d normal, Vec2f texCoord, Color4f color) {
		if (vertex == null) throw new NullPointerException("vertex == null");
		this.points.add(vertex);
		this.pointsData.add(new VertexData(normal, texCoord, color));
		onStructuralChanges();
	}
	
	public void removeAllVertices() {
		this.points.clear();
		this.pointsData.clear();
		onStructuralChanges();
	}
	
	public ArrayList<Vec3d> getVertices() {
		ArrayList<Vec3d> result = new ArrayList<Vec3d>();
		
		for (Vec3d vec : this.points) {
			result.add(vec.clone());
		}
		
		return result;
	}
	
	public void setPrimitive(Primitive p) {
		if (p == null) throw new NullPointerException("p == null");
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
	
	protected AxisAlignedBox3D boundingBox;
	@Override
	public AxisAlignedBox3D getRenderBoundingBox(float x, float y, float z) {
		if (this.boundingBox == null) {
			this.boundingBox = AxisAlignedBox3D.createBoxFromList(this.points);
		}
		AxisAlignedBox3D box = this.boundingBox.clone();
		box.translate(x, y, z);
		applyTransformsToBoundingBox(box);
		return box;
	}
	
	@Override
	public void renderAt(Vec3d position, double rotation, Vec3d up, Vec3d scale, Painter painter) {
		if (this.points.size() < primitive.getVerticeCount() || this.points.size() == 0) return;
		
		painter.setPrimitive(this.primitive);
		painter.setTexture(this.texture);
		painter.setColor(this.color);
		
		//Si la matrice n'est pas modifiée, pas besoin de vider le tampon du Painter
		applyTransforms(position, rotation, up, scale, painter);
		
		//DESSIN
		for (int i = 0 ; i < this.points.size() ; i++) {
			Vec3d vertex = this.points.get(i);
			VertexData data = this.pointsData.get(i);
			
			//couleur
			if (data.color != null) {
				painter.setColor(data.color);
			}
			else {
				painter.setColor(this.color);
			}
			
			//normale et texcoord
			if (data.normal != null && data.texCoord != null) {
				painter.addVertex(vertex, data.normal, data.texCoord.x, data.texCoord.y);
			}
			else if (data.normal == null && data.texCoord != null) {
				painter.addVertex(vertex, data.texCoord.x, data.texCoord.y);
			}
			else if (data.normal != null && data.texCoord == null) {
				painter.addVertex(vertex, data.normal);
			}
			else {
				painter.addVertex(vertex);
			}
		}
	}
	
	@Override
	public void start() {
		if (this.texture instanceof AnimatedTexture) {
			((AnimatedTexture) this.texture).start();
		}
	}
	
	@Override
	public void stop() {
		if (this.texture instanceof AnimatedTexture) {
			((AnimatedTexture) this.texture).stop();
		}
	}
	
	@Override
	public void reset() {
		if (this.texture instanceof AnimatedTexture) {
			((AnimatedTexture) this.texture).reset();
		}
	}
	
	@Override
	public RenderableObject3D clone() {
		RenderableObject3D clone = (RenderableObject3D) super.clone();
		
		clone.points = new ArrayList<Vec3d>();
		for (Vec3d point : this.points) {
			clone.points.add(point.clone());
		}
		
		return clone;
	}
}
