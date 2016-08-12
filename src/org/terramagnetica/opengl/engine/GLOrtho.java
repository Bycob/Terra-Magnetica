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
along with BynarysCode. If not, see <http://www.gnu.org/licenses/>.
 </LICENSE> */

package org.terramagnetica.opengl.engine;

import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class GLOrtho {
	
	public double left, right, bottom, top, near, far;

	public GLOrtho(double left, double right, double bottom, double top,
			double near, double far) {
		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;
		this.near = near;
		this.far = far;
	}
	
	public GLOrtho(){
		left = -1;
		right = 1;
		bottom = -1;
		top = 1;
		near = 1;
		far = -1;
	}
	
	public double getHeight() {
		return top - bottom;
	}
	
	public double getWidth() {
		return right - left;
	}
	
	/** Donne les limites du repère openGL représenté par cet objet
	 * (left, top, right, bottom) */
	public RectangleDouble getBounds2D() {
		return new RectangleDouble(left, top, right, bottom);
	}
}
