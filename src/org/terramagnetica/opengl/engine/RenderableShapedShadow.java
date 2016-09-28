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

import net.bynaryscode.util.maths.geometric.Circle;
import net.bynaryscode.util.maths.geometric.Shape;

public class RenderableShapedShadow extends RenderableObject3D {
	
	protected Shape shape;
	
	/** La taille absolue du flou sur les bords. (unité openGL) */
	private float fadeout = 0.1f;
	
	public RenderableShapedShadow() {
		this(new Circle(0,0,0));
	}
	
	public RenderableShapedShadow(Shape shape) {
		this.setShape(shape);
	}
	
	public void setShape(Shape shape) {
		this.shape = shape;
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
	}
	
	public RenderableShapedShadow withFadeout(float fadeout) {
		this.setFadeout(fadeout);
		return this;
	}
	
	public float getFadeout() {
		return this.fadeout;
	}
}
