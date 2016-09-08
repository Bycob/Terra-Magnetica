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

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public abstract class Renderable {
	protected Color4f color = new Color4f();
	
	protected Vec3d posOffset = new Vec3d(0, 0, 0);
	protected Vec3d rotOffset = new Vec3d(0, 0, 0);
	protected Vec3d scaleOffset = new Vec3d(1, 1, 1);
	
	public void setColor(Color4f color) {
		if (color == null) throw new NullPointerException("color == null");
		this.color = color.clone();
	}
	
	public Color4f getColor() {
		return this.color.clone();
	}
	
	public Renderable withColor(Color4f color) {
		this.setColor(color);
		return this;
	}
	
	public void setPositionOffset(double x, double y, double z) {
		this.posOffset = new Vec3d(x, y, z);
	}
	
	public void setPositionOffset(Vec3d vec) {
		setPositionOffset(vec.x, vec.y, vec.z);
	}
	
	public Vec3d getPositionOffset() {
		return this.posOffset.clone();
	}
	
	public void setRotationOffset(double rotX, double rotY, double rotZ) {
		this.rotOffset = new Vec3d(rotX, rotY, rotZ);
	}
	
	public Vec3d getRotationOffset() {
		return this.rotOffset.clone();
	}
	
	public void setScaleOffset(double scaleX, double scaleY, double scaleZ) {
		this.scaleOffset = new Vec3d(scaleX, scaleY, scaleZ);
	}
	
	public void renderAt(double x, double y, double z, Painter painter) {
		renderAt(new Vec3d(x, y, z), 0, new Vec3d(0, 0, 1), new Vec3d(1, 1, 1), painter);
	}
	
	public abstract void renderAt(Vec3d position, double rotation, Vec3d up, Vec3d scale, Painter painter);
}
