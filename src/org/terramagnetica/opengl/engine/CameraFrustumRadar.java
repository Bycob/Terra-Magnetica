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
 * Une implémentation plus simple du frustum, qui utilise la méthode "radar"
 * pour tester les points, dans le cadre du frustum culling.
 * 
 * @author Louis JEAN
 */
public class CameraFrustumRadar extends CameraFrustum {
	
	//REPERE
	private Vec3d origin = new Vec3d(0, 0, 0);
	private Vec3d vecUnitX = new Vec3d(1, 0, 0),
			vecUnitY = new Vec3d(0, 1, 0),
			vecUnitZ = new Vec3d(0, 0, 1);
	
	//DONNEES SUR LE CHAMP.
	private double nearDist = 0, farDist = 1;
	private double hAngle = Math.PI / 2;
	private double ratio = 1;
	
	public CameraFrustumRadar() {
		
	}
	
	@Override
	public void set(Vec3d origin, Vec3d lookAt, Vec3d up, double nearDist,
			double farDist, double ratio, double fov) {
		
		this.origin = origin.clone();
		this.vecUnitZ = lookAt.substract(origin); this.vecUnitZ.normalize();
		this.vecUnitX = this.vecUnitZ.crossProduct(up); this.vecUnitX.normalize();
		this.vecUnitY = this.vecUnitX.crossProduct(this.vecUnitZ); this.vecUnitY.normalize();
		
		this.nearDist = nearDist;
		this.farDist = farDist;
		
		this.hAngle = fov;
		this.ratio = ratio;
	}
	
	@Override
	public boolean containsPoint(Vec3d point) {
		//Vecteur : origine de la caméra -> point
		Vec3d pointBis = point.substract(this.origin);
		
		//TEST DU Z
		double ptZ = this.vecUnitZ.dotProduct(pointBis);
		if (ptZ < this.nearDist || ptZ > this.farDist) return false;
		

		//TEST DU Y
		double ptY = this.vecUnitY.dotProduct(pointBis);
		double h = ptZ * 2 * Math.tan(Math.toRadians(this.hAngle) / 2);
		if (ptY < - h / 2 || ptY > h / 2) return false;
		
		//TEST DU X
		double ptX = this.vecUnitX.dotProduct(pointBis);
		double w = h * ratio;
		if (ptX < - w / 2 || ptX > w / 2) return false;
		
		return true;
	}
}
