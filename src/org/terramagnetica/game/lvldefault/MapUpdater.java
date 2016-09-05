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

import org.terramagnetica.game.GameEngine;
import org.terramagnetica.game.lvldefault.rendering.MapRenderer;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec2i;

public class MapUpdater extends GameAspect {
	
	private GamePlayingDefault game;
	private MiniMap theMap = new MiniMap();
	private MapRenderer theRenderer;

	public static final float MAX_DISTANCE_NOT_LIMITED = 11;
	public static final float MAX_DISTANCE_LIMITED = 5.5f;
	
	private float maxDistance = 11;
	
	public MapUpdater() {
		this(null);
	}
	
	public MapUpdater(GamePlayingDefault game) {
		this.setGame(game);
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		
	}

	@Override
	public MapUpdater decode(BufferedObjectInputStream in) throws GameIOException {
		return this;
	}

	@Override
	public void init() {
		
	}

	@Override
	public void update(long dT) {
		if (this.game != null) {
			//Cet algorithme trouve les cases à afficher sur la minimap.
			ArrayList<Vec2i> toVisit = new ArrayList<Vec2i>();
			ArrayList<Vec2i> visiting = new ArrayList<Vec2i>();
			ArrayList<Vec2i> visited = new ArrayList<Vec2i>();
			
			Vec2i ref = this.game.getPlayer().getCoordonnéesCase();
			toVisit.add(ref);
			
			while (toVisit.size() != 0) {
				visiting.clear();
				visiting.addAll(toVisit);
				toVisit.clear();
				
				for (Vec2i c : visiting) {
					Vec2i[] near = new Vec2i[] {
						new Vec2i(c.x + 1, c.y),
						new Vec2i(c.x, c.y - 1),
						new Vec2i(c.x - 1, c.y),
						new Vec2i(c.x, c.y + 1)
					};
					
					for (Vec2i near0 : near) {
						if (!visited.contains(near0) && !toVisit.contains(near0)
								&& MathUtil.getDistance(near0.asDouble(), ref.asDouble()) <= maxDistance
								&& this.game.getLandscapeAt(near0.x, near0.y).isEnabled()) {
							
							toVisit.add(near0);
						}
					}
					
					visited.add(c);
				}
			}
			
			this.theMap.reset();
			
			for (Vec2i c : visited) {
				this.theMap.addMapLandscape(new MapLandscape(c.x, c.y));
			}
			
			for (Entity e : this.game.getEntities()) {
				Vec2i cCase = e.getCoordonnéesCase();
				
				for (Vec2i c : visited) {
					if (cCase.equals(c) && e.isMapVisible()) {
						Vec2f ce = e.getCoordonnéesf();
						MapEntity me = new MapEntity(e, ce.x, ce.y);
						this.theMap.addMapEntity(me);
						break;
					}
				}
			}
		}
	}
	
	public float getMaxDistance() {
		return this.maxDistance;
	}
	
	public void setMaxDistance(float d) {
		this.maxDistance = d;
	}
	
	public MiniMap getMap() {
		return this.theMap;
	}
	
	public MapRenderer getRenderer() {
		return this.theRenderer;
	}
	
	@Override
	public void setGame(GameEngine game) {
		if (game instanceof GamePlayingDefault) {
			this.game = (GamePlayingDefault) game;
		}
		else {
			this.game = null;
		}
		
		if (this.game != null) {
			this.theRenderer = new MapRenderer(this.theMap, this);
			this.game.render.addRendering(new MapRenderer(this.theMap, this));
			this.setMaxDistance(this.game.hasLimitedVision() ? MAX_DISTANCE_LIMITED : MAX_DISTANCE_NOT_LIMITED);
		}
	}
	
	@Override
	public boolean shouldSave() {
		return false;
	}
}
