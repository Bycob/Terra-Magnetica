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

import org.joml.Matrix4f;
import org.terramagnetica.opengl.gui.GuiWindow;

import net.bynaryscode.util.maths.geometric.Vec3d;

/**
 * Classe permettant de gérer la caméra d'openGL lors
 * de dessin en trois dimensions. Elle contient des informations
 * sur la position des yeux, le point vers lequel est orienté
 * la caméra et l'orientation de la verticale.
 * <p>La définition de cette camera comme vue sur un dessin se 
 * fait en appelant la méthode {@link #pushCamera()}. Cela ne 
 * change pas le dessin d'origine, mais applique le nouveau
 * repère pour les dessins futurs.
 * @author Louis JEAN
 *
 */
public class Camera3D implements Camera {
	protected Vec3d eye;
	protected Vec3d center;
	protected Vec3d up;
	
	private float fov = 70;
	
	private float near = 0.5f;
	private float far = 100f;
	
	public static enum Verticale {
		X_AXIS(1, 0, 0),
		Y_AXIS(0, 1, 0),
		Z_AXIS(0, 0, 1);
		
		private Vec3d verticale;
		
		Verticale() {
			this.verticale = new Vec3d(0, 0, 1);
		}
		
		Verticale(Vec3d verticale) {
			this.verticale = verticale;
		}
		
		Verticale(double x, double y, double z) {
			this(new Vec3d(x, y, z));
		}
	}
	
	public Camera3D() {
		this(new Vec3d(0, 0, 0), new Vec3d(1, 0, 0), new Vec3d(0, 0, 1));
	}
	
	public Camera3D(Vec3d eye, Vec3d center, Vec3d up) {
		this.eye = eye;
		this.center = center;
		this.up = up;
	}
	
	public Camera3D(Vec3d eye, Vec3d center, Verticale up) {
		this(eye, center, up.verticale);
	}
	
	public Camera3D(double eyeX, double eyeY, double eyeZ, 
			double centerX, double centerY, double centerZ,
			double upX, double upY, double upZ) {
		
		this(new Vec3d(eyeX, eyeY, eyeZ), 
				new Vec3d(centerX, centerY, centerZ),
				new Vec3d(upX, upY, upZ));
	}
	
	public Camera3D(double eyeX, double eyeY, double eyeZ, 
			double centerX, double centerY, double centerZ,
			Verticale up) {
		
		this(new Vec3d(eyeX, eyeY, eyeZ), 
				new Vec3d(centerX, centerY, centerZ),
				up.verticale);
	}
	
	public Vec3d getEye() {
		return eye;
	}

	public void setEye(Vec3d eye) {
		this.eye = eye;
	}
	
	public void setEye(double x, double y, double z) {
		this.eye = new Vec3d(x, y, z);
	}

	public Vec3d getCenter() {
		return center;
	}

	public void setCenter(Vec3d center) {
		this.center = center;
	}
	
	public void setCenter(double x, double y, double z) {
		this.center = new Vec3d(x, y, z);
	}

	public Vec3d getUp() {
		return up;
	}

	public void setUp(Vec3d up) {
		this.up = up;
	}
	
	public void setUp(double x, double y, double z) {
		this.up = new Vec3d(x, y, z);
	}
	
	public void setUp(Verticale up) {
		this.up = up.verticale;
	}
	
	public void setFOV(float fov) {
		this.fov = fov;
	}
	
	public float getFOV() {
		return this.fov;
	}
	
	public void setUpFrustum(Painter painter, CameraFrustum frustum) {
		GuiWindow window = painter.getWindow();
		frustum.set(this.getEye(),
				this.getCenter(),
				this.getUp(),
				near, far, (float) window.getWidth() / (float) window.getHeight(), this.fov);
	}
	
	@Override
	public void pushCamera(Painter painter) {
		
		GuiWindow window = painter.getWindow();
		Program currentProgram = painter.getCurrentProgram();
		
		Matrix4f projection = new Matrix4f().setPerspective(fov, (float) window.getWidth() / (float) window.getHeight(), this.near, this.far);
		Matrix4f camera = new Matrix4f().lookAt((float) eye.x, (float) eye.y, (float) eye.z, 
				(float) center.x, (float) center.y, (float) center.z, 
				(float) up.x, (float) up.y, (float) up.z);
		
		currentProgram.setUniformMatrix4f(StdUniform.View.PROJECTION_MATRIX, projection);
		currentProgram.setUniformMatrix4f(StdUniform.View.CAMERA_MATRIX, camera);
		currentProgram.setUniform3f(StdUniform.View.CAMERA_POSITION, (float) eye.x, (float) eye.y, (float) eye.z);
	}
}
