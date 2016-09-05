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
 * Cette classe gère le frustum culling. Elle permet de définir le champ de la
 * caméra, puis de tester si un point ou un autre objet est dans le champ.
 * <p>
 * {@literal <!>} Attention, l'implémentation n'est pas faite, la classe n'est
 * donc pas encore fonctionnelle.
 * 
 * @author Louis JEAN
 */
public class CameraFrustum {
	
	public CameraFrustum() {
		
	}
	
	public void set(Vec3d origin, Vec3d lookAt, Vec3d up, double nearDist,
			double farDist, double ratio, double fov) {
		
	}
	
	/** Vérifie si le champ de la caméra contient le point
	 * passé en paramètre. */
	public boolean containsPoint(Vec3d point) {
		return false;
	}
}
