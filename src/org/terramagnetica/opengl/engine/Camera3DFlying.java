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

import net.bynaryscode.util.maths.geometric.Vec3d;

/**
 * Caméra disposant de méthode pour déplacer la caméra entière
 * à une position donnée.
 * @author Louis JEAN
 *
 */
public class Camera3DFlying extends Camera3D {
	
	public Camera3DFlying() {
		
	}
	
	public Camera3DFlying(Vec3d eye, Vec3d center, Vec3d up) {
		super(eye, center, up);
		
	}
	
	public Camera3DFlying(Vec3d eye, Vec3d center, Verticale up) {
		super(eye, center, up);
		
	}
	
	public Camera3DFlying(double eyeX, double eyeY, double eyeZ, double centerX,
			double centerY, double centerZ, double upX, double upY, double upZ) {
		super(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		
	}
	
	public Camera3DFlying(double eyeX, double eyeY, double eyeZ, double centerX,
			double centerY, double centerZ, Verticale up) {
		super(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, up);
		
	}
	
	public void moveCenterPoint(Vec3d center) {
		Vec3d oldCenter = this.getCenter();
		this.setCenter(center);
		
		double difX = center.x - oldCenter.x;
		double difY = center.y - oldCenter.y;
		double difZ = center.z - oldCenter.z;
		
		eye.x += difX;
		eye.y += difY;
		eye.z += difZ;
	}
	
	public void moveCenterPoint(double x, double y, double z) {
		this.moveCenterPoint(new Vec3d(x, y, z));
	}
	
	public void moveEyePoint(Vec3d eye) {
		Vec3d oldEye = this.getEye();
		this.setEye(eye);
		
		double difX = eye.x - oldEye.x;
		double difY = eye.y - oldEye.y;
		double difZ = eye.z - oldEye.z;
		
		center.x += difX;
		center.y += difY;
		center.z += difZ;
	}
	
	public void moveEyePoint(double x, double y, double z) {
		this.moveEyePoint(new Vec3d(x, y, z));
	}
}
