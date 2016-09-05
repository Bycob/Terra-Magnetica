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

import java.util.ArrayList;
import java.util.List;

import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxInfo;
import org.terramagnetica.physics.PhysicEngine;

public class LevelDefaultPhysicEngine extends PhysicEngine {
	
	public static final boolean DEBUG_PHYSIC = true;
	
	private GamePlayingDefault game;
	
	public LevelDefaultPhysicEngine() {
		
	}
	
	/** Calcul l'état du monde après un tick. */
	public void update(GamePlayingDefault game, float time) {
		
		this.game = game;
		
		List<Entity> entities = game.getEntities();
		ArrayList<Hitbox> hitboxes = new ArrayList<Hitbox>(entities.size());
		
		for (Entity ent : entities) {
			hitboxes.add(ent.getHitbox());
			ent.getHitbox().infos = new LevelDefaultHitboxInfo(ent);
		}
		
		step(hitboxes, time);
		
		for (Hitbox hb : hitboxes) {
			hb.clearForces();
		}
	}
	
	@Override
	public void onCollision(Hitbox hb1, Hitbox hb2) {
		if (this.game == null) {
			throw new NullPointerException("Aucun environnement n'a été défini");
		}
		
		Entity ent1 = hb1.infos instanceof LevelDefaultHitboxInfo ? ((LevelDefaultHitboxInfo) hb1.infos).myEntity : null;
		Entity ent2 = hb2.infos instanceof LevelDefaultHitboxInfo ? ((LevelDefaultHitboxInfo) hb2.infos).myEntity : null;
		
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
	@Override
	public ArrayList<Hitbox> getEnvironmentHitbox(Hitbox hb) {
		if (this.game == null) {
			throw new NullPointerException("Aucun environement n'a été défini");
		}
		
		ArrayList<Hitbox> result = new ArrayList<Hitbox>(8);
		
		int caseX = (int) hb.getPositionX();
		int caseY = (int) hb.getPositionY();
		
		LandscapeTile thisCase = game.getLandscapeAt(caseX, caseY);
		if (!thisCase.isEnabled()) {
			result.add(thisCase.getHitboxf());
			return result;
		}
		
		for (int x = caseX - 1 ; x <= caseX + 1 ; x++) {
			for (int y = caseY - 1 ; y <= caseY + 1 ; y++) {
				LandscapeTile tile = game.getLandscapeAt(x, y);
				if ((x != caseX || y != caseY) && !tile.isEnabled()) {
					if (x == caseX || y == caseY) {
						result.add(tile.getHitboxf());
					}
					else {
						LandscapeTile tile1 = game.getLandscapeAt(x, caseX);
						LandscapeTile tile2 = game.getLandscapeAt(caseY, y);
						if (tile1.isEnabled() && tile2.isEnabled()) {
							result.add(tile.getHitboxf());
						}
					}
				}
			}
		}
		
		return result;
	}
	
	/** Retourne un objet {@link LevelDefaultHitboxInfo} contenant les informations
	 * sur la hitbox passée en paramètres. */
	private LevelDefaultHitboxInfo info(Hitbox hb) {
		return hb.infos instanceof LevelDefaultHitboxInfo ? (LevelDefaultHitboxInfo) hb.infos : new LevelDefaultHitboxInfo();
	}
	
	public static class LevelDefaultHitboxInfo extends HitboxInfo {
		
		private Entity myEntity;
		
		public LevelDefaultHitboxInfo() {
			this(null);
		}
		
		public LevelDefaultHitboxInfo(Entity ent) {
			this.myEntity = ent;
		}
		
		public Entity getEntity() {
			return this.myEntity;
		}
	}
}
