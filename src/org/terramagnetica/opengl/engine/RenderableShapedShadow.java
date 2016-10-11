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

import org.terramagnetica.opengl.engine.Painter.Primitive;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Circle;
import net.bynaryscode.util.maths.geometric.Shape;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderableShapedShadow extends RenderableObject3D implements IShadowObject {
	/** L'ombre, toujours au dessus :-) */
	public static final float Z_INDEX = 0.001f;
	
	protected Shape shape;
	
	/** La taille absolue du flou sur les bords. (unité openGL) */
	private float fadeout = 1f;
	
	public RenderableShapedShadow() {
		this(new Circle(0,0,0.001f));
	}
	
	public RenderableShapedShadow(Shape shape) {
		super(Primitive.TRIANGLES);
		
		this.setShape(shape);
		
		this.setColor(new Color4f(0, 0, 0, 0.5f));
		this.setTexture(null);
	}
	
	private void recalculate() {
		//Calcul du dessin à effectuer
		this.removeAllVertices();
		
		final float z = Z_INDEX;
		Color4f color = this.color.clone().withAlphaf(0);
		Vec2d center = this.shape.center();
		Vec3d center3 = new Vec3d(center.x, center.y, z);
		Vec2d[] vertices = this.shape.getVertices();
		
		for (int i = 0 ; i < vertices.length ; i++) {
			int j = (i + 1) % vertices.length;

			//Cercle sombre
			Vec3d firstPoint = new Vec3d(vertices[i].x, vertices[i].y, z);
			Vec3d secondPoint = new Vec3d(vertices[j].x, vertices[j].y, z);
			
			addVertex(firstPoint);
			addVertex(secondPoint);
			addVertex(center3);
			
			//Flou
			Vec3d firstVect = firstPoint.substract(center3); double firstVectLen = firstVect.length();
			Vec3d secondVect = secondPoint.substract(center3); double secondVectLen = secondVect.length();
			
			firstVect = firstVect.multiply((firstVectLen + this.fadeout) / firstVectLen);
			secondVect = secondVect.multiply((secondVectLen + this.fadeout) / secondVectLen);
			
			Vec3d thirdPoint = center3.add(firstVect);
			Vec3d fourthPoint = center3.add(secondVect);
			
			addVertex(firstPoint);
			addVertex(secondPoint);
			addVertex(thirdPoint, color);
			
			addVertex(thirdPoint, color);
			addVertex(fourthPoint, color);
			addVertex(secondPoint);
		}
		
	}
	
	public void setShape(Shape shape) {
		this.shape = shape;
		
		//Cas des cercles : pour un rendu lissé, il faut augmenter le nombre de vertices
		if (this.shape instanceof Circle) {
			Circle circle = (Circle) this.shape;
			circle.setNbSommets(32);
		}
		
		recalculate();
	}
	
	public RenderableShapedShadow withShape(Shape shape) {
		this.setShape(shape);
		return this;
	}
	
	public Shape getShape() {
		return this.shape;
	}
	
	public void setFadeout(float fadeout) {
		this.fadeout = fadeout;
		
		recalculate();
	}
	
	public RenderableShapedShadow withFadeout(float fadeout) {
		this.setFadeout(fadeout);
		return this;
	}
	
	public float getFadeout() {
		return this.fadeout;
	}
	
	@Override
	public void setColor(Color4f color) {
		super.setColor(color);
		recalculate();
	}
}
