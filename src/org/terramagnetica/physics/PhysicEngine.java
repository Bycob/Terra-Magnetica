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

package org.terramagnetica.physics;

import java.util.ArrayList;
import java.util.List;

public abstract class PhysicEngine {
	
	public PhysicEngine() {
		
	}
	
	public void step(List<Hitbox> hitboxes, float time) {
		//Application des forces
		for (Hitbox hb : hitboxes) {
			hb.applyForces(time);
		}
		
		//Détection des collisions.
		for (int i = 0 ; i < hitboxes.size() ; i++) {
			//entités
			Hitbox hbi = hitboxes.get(i);
			for (int j = i + 1 ; j < hitboxes.size() ; j++) {
				hbi.calculateNextCollisionPoint(hitboxes.get(j), time);
			}
			
			//environnement
			ArrayList<Hitbox> envHbs = getEnvironmentHitbox(hbi);
			for (Hitbox hbj : envHbs) {
				hbi.calculateNextCollisionPoint(hbj, time);
			}
		}
		
		//Application des collision, puis on revérifie derrière jusqu'à ce qu'il n'y ait plus de collision.
		boolean isThereCollision = true;
		ArrayList<Hitbox> collidedHitboxes = new ArrayList<Hitbox>(hitboxes.size());
		
		while (isThereCollision) {
			isThereCollision = false;
			collidedHitboxes.clear();
			
			//Recherche des premières collisions à effectuer
			float lowerTime = time;
			for (Hitbox hb : hitboxes) {
				if (!hb.hasNextCollisionPoint()) continue;
				float thisTime = hb.getNextCollisionPoint().getTime();
				
				if (thisTime < lowerTime) {
					lowerTime = thisTime;
					
					if (collidedHitboxes.size() != 0) {
						//On a skip des collisions, qu'il faudra tester dans un tour de boucle ultérieur
						isThereCollision = true;
						collidedHitboxes.clear();
					}
				}
				if (thisTime == lowerTime) {
					collidedHitboxes.add(hb);
				}
				else {
					//On vient juste de skip une collision
					isThereCollision = true;
				}
			}
			
			//Calcul de la collision
			for (Hitbox hb : collidedHitboxes) {
				if (hb.hasNextCollisionPoint()) {
					onCollision(hb, hb.getNextCollisionPoint().getOtherHitbox(hb));
					hb.calculateNextCollisionReaction();
				}
			}
			
			//Redétection des collisions.
			for (Hitbox hb : collidedHitboxes) {
				//entités
				for (Hitbox hb2 : hitboxes) {
					if (hb2 != hb) {
						hb.calculateNextCollisionPoint(hb2, time);
						if (hb.hasNextCollisionPoint()) isThereCollision = true;
					}
				}
				
				//environnement
				ArrayList<Hitbox> envHbs = getEnvironmentHitbox(hb);
				for (Hitbox hbe : envHbs) {
					hb.calculateNextCollisionPoint(hbe, time);
					if (hb.hasNextCollisionPoint()) isThereCollision = true;
				}
			}
		}
		
		//Finalisation du mouvement
		for (Hitbox hb : hitboxes) {
			hb.completeMove(time);
		}
	}
	
	public abstract ArrayList<Hitbox> getEnvironmentHitbox(Hitbox hb);
	
	public void onCollision(Hitbox hb1, Hitbox hb2) {
		
	}
}
