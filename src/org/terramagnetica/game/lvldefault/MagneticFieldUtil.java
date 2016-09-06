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

package org.terramagnetica.game.lvldefault;

import net.bynaryscode.util.Boussole;
import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Vec2;
import net.bynaryscode.util.maths.geometric.Vec2f;

public class MagneticFieldUtil {
	
	/**
	 * Fait tourner l'entité passée en paramètre autour du point
	 * passé en paramètre, en direction d'un objectif.
	 * @param object - l'entité qui tourne.
	 * @param centre - le centre du cercle sur lequel doit rester
	 * l'entité.
	 * @param rayon - le rayon du cercle sur lequel doit rester
	 * l'entité.
	 * @param objectif - la direction à atteindre par rapport au
	 * centre. La direction est utilisé de la même façon que la
	 * {@link Boussole}
	 * @param rotationSpeed - la rotation effectuée en une seconde, en radians.
	 * @param dT - le temps qui s'est écoulé depuis la dernière mise
	 * à jour.
	 * @return {@code true} si l'entité est bien arrivée à son objectif,
	 * {@code false} si elle n'y est pas encore.
	 */
	public static boolean rotateTo(Entity object, Vec2 centre, double rayon, double objectif,
			double rotationSpeed, long dT) {
		
		Vec2f c = centre.asFloat();
		Vec2f invertCentre = new Vec2f(c.x, - c.y);//coordonnées inversées
		Vec2f oc = object.getPositionf(); oc.y = -oc.y;//coordonnées inversées
		double dirBut = objectif;
		double dirNow = Boussole.getDirection(invertCentre, oc);
		double addedAngle = MathUtil.angleMainValue(dirBut - dirNow);
		
		if (addedAngle > Math.PI) {
			addedAngle -= Math.PI * 2;
		}
		
		boolean returned = false;
		
		if (Math.abs(addedAngle) > rotationSpeed * dT / 1000) {
			addedAngle = Math.signum(addedAngle) * (rotationSpeed * dT / 1000);
		}
		else {
			returned = true;
		}
		
		dirNow = MathUtil.angleMainValue(dirNow + addedAngle);
		object.setPositionf((float) (c.x + Math.cos(dirNow) * rayon), (float) (c.y - Math.sin(dirNow) * rayon));
		
		return returned;
	}
	
	/**
	 * Fait tourner l'entité passée en paramètre autour du point indiqué.
	 * <p>{@literal <!>} Toutes les longueurs et coordonnées sont en cases.
	 * @param objet - l'entité effectuant la rotation.
	 * @param centre - le centre de la rotation.
	 * @param rayon - le rayon de la rotation.
	 * @param rotationSpeed - la vitesse de rotation en radians par secondes.
	 * Si cette valeur est positive, l'entité tournera dans le sens inverse
	 * des aiguilles d'une montre, et dans le sens des aiguilles d'une montre
	 * si cette valeur est négative.
	 * @param dT - le temps écoulé depuis la dernière mise à jour, utilisé dans
	 * le calcul de vitesse.
	 */
	public static void rotate(Entity object, Vec2 centre, double rayon, double rotationSpeed, long dT) {
		Vec2f c = centre.asFloat();
		Vec2f entityCoordinates = object.getPositionf();
		double oldDir = Boussole.getDirection(c, entityCoordinates);
		double newDir = MathUtil.angleMainValue(oldDir + rotationSpeed * dT / 1000);
		
		entityCoordinates = new Vec2f();
		entityCoordinates.x = (float) (c.x + Math.cos(newDir) * rayon);
		entityCoordinates.y = (float) (c.y + Math.sin(newDir) * rayon);
		
		object.setPositionf(entityCoordinates);
	}
}
