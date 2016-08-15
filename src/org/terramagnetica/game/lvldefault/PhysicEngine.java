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

package org.terramagnetica.game.lvldefault;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.terramagnetica.game.physic.Hitbox;

public class PhysicEngine {
	
	private HashMap<Hitbox, Entity> entityMap = new HashMap<Hitbox, Entity>();
	
	public PhysicEngine() {
		
	}
	
	/** Calcul l'état du monde après un tick. */
	public void update(GamePlayingDefault game, float time) {
		
		List<Entity> entities = game.getEntities();
		ArrayList<Hitbox> hitboxes = new ArrayList<Hitbox>(entities.size());
		
		for (Entity ent : entities) {
			hitboxes.add(ent.getHitBoxf());
			this.entityMap.put(ent.getHitBoxf(), ent);
		}
		
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
			ArrayList<Hitbox> envHbs = getEnvironmentHitbox(game, hbi);
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
					collidedHitboxes.clear();
				}
				if (thisTime == lowerTime) {
					collidedHitboxes.add(hb);
				}
			}
			
			//Calcul de la collision
			for (Hitbox hb : collidedHitboxes) {
				if (hb.hasNextCollisionPoint()) {
					onCollision(game, hb, hb.getNextCollisionPoint().getOtherHitbox(hb));
					hb.doNextCollision();
				}
			}
			
			//Revérification des collisions.
			for (Hitbox hb : collidedHitboxes) {
				//entités
				for (Hitbox hb2 : hitboxes) {
					if (hb2 != hb) {
						hb.calculateNextCollisionPoint(hb2, time);
						if (hb.hasNextCollisionPoint()) isThereCollision = true;
					}
				}
				
				//environnement
				ArrayList<Hitbox> envHbs = getEnvironmentHitbox(game, hb);
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
	
	public void onCollision(GamePlayingDefault game, Hitbox hb1, Hitbox hb2) {
		Entity ent1 = this.entityMap.get(hb1);
		Entity ent2 = this.entityMap.get(hb2);
		
		if (ent1 == null) {
			if (ent2 != null) {
				ent2.addWallCollision();
			}
		}
		else if (ent2 == null) {
			if (ent1 != null) {
				ent1.addWallCollision();
			}
		}
		else {
			ent1.addEntityCollision(ent2);
			ent2.addEntityCollision(ent1);
		}
	}
	
	/** Donne la liste des hitbox susceptibles d'entrer en collision avec celle passée
	 * en paramètres. */
	public ArrayList<Hitbox> getEnvironmentHitbox(GamePlayingDefault game, Hitbox hb) {
		ArrayList<Hitbox> result = new ArrayList<Hitbox>(8);
		
		int caseX = (int) hb.getPositionX();
		int caseY = (int) hb.getPositionY();
		
		for (int x = caseX - 1 ; x <= caseX + 1 ; x++) {
			for (int y = caseY - 1 ; y <= caseY + 1 ; y++) {
				LandscapeTile tile;
				if ((x != caseX || y != caseY) && !(tile = game.getLandscapeAt(x, y)).isEnabled()) {
					result.add(tile.getHitboxf());
				}
			}
		}
		
		return result;
	}
}
