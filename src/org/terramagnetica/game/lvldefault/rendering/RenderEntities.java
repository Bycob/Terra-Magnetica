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

package org.terramagnetica.game.lvldefault.rendering;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.MapLandscape;
import org.terramagnetica.game.lvldefault.MapUpdater;
import org.terramagnetica.opengl.engine.CameraFrustum;
import org.terramagnetica.opengl.engine.Painter;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderEntities extends RenderGameDefaultElement {
	
	private static HashMap<Class<? extends RenderEntity>, Integer> priorityOrder =
			new HashMap<Class<? extends RenderEntity>, Integer>();
	
	static {
		int i = -1;
		priorityOrder.put(RenderEntityModel3D.class, ++i);
		priorityOrder.put(RenderEntityDefault.class, ++i);
		priorityOrder.put(RenderEntityDefaultAnimation.class, i);
	}
	
	private static int getPriorityOrder(Class<? extends RenderEntity> clazz) {
		Integer value = priorityOrder.get(clazz);
		return value == null ? -1 : value;
	}
	
	
	private CameraFrustum frustum;
	
	
	private class RenderEntityUnit implements Comparable<RenderEntityUnit> {
		private float x;
		private float y;
		
		private RenderEntity render;
		
		private RenderEntityUnit(RenderEntity render, float x, float y) {
			this.render = render;
			this.x = x;
			this.y = y;
		}
		
		public void render(Painter painter) {
			if (isInFrustum(painter)) {
				this.render.renderEntity3D(this.x, this.y, painter);
			}
		}

		@Override
		public int compareTo(RenderEntityUnit o) {
			if (this.render.getClass() != o.render.getClass()) {
				int compareClass = getPriorityOrder(this.render.getClass()) - getPriorityOrder(o.render.getClass());
				if (compareClass != 0) return compareClass;
			}
			
			float thisY = this.y;
			float oY = o.y;
			
			if (this.render instanceof RenderEntityDefault) {
				thisY = ((RenderEntityDefault) this.render).getRealPositionY(this.x, this.y);
			}
			
			if (o.render instanceof RenderEntityDefault) {
				oY = ((RenderEntityDefault) o.render).getRealPositionY(o.x, o.y);
			}
			
			//Comparaison des angles
			if (this.render instanceof RenderEntityDefault && o.render instanceof RenderEntityDefault) {
				int radDif = (int) Math.signum(((RenderEntityDefault) this.render).getRadius()
						- ((RenderEntityDefault) o.render).getRadius());
				if (radDif != 0) return radDif;
			}
			
			return (int) Math.signum(thisY - oY);
		}
		
		private boolean isInFrustum(Painter painter) {
			CameraFrustum camFrustum = RenderEntities.this.frustum;
			Vec3d[] points = this.render.getRenderBoundingBox(this.x, this.y).getPoints();
			for (Vec3d p : points) {
				if (camFrustum.containsPoint(p)) {
					return true;
				}
			}
			
			return false;
		}
	}
	
	
	
	@Override
	public void render(GamePlayingDefault game, Painter painter) {
		
		painter.setConfiguration(this.getDefault3DConfiguration());
		this.frustum = painter.createCameraFrustum();
		
		ArrayList<RenderEntityUnit> renderList = getRenderList(game.getEntities());
		MapUpdater miniMapManager = game.getAspect(MapUpdater.class);
		MapRenderer miniMapRenderer = miniMapManager.getRenderer();
		MapLandscape[] limVisionCaseArray = miniMapManager.getMap().getAllLandscapeMapped();
		
		for (RenderEntityUnit r : renderList) {
			
			Color4f initColor = r.render.getColor();
			
			//Si la vision limitée est activée, on ne dessine pas tout
			if (game.hasLimitedVision()) {
				boolean found = false;
				
				for (MapLandscape ml : limVisionCaseArray) {
					if (Math.abs(ml.getCaseX() - Math.floor(r.x)) <= 1 && Math.abs(ml.getCaseY() - Math.floor(r.y)) <= 1) {
						found = true;
						break;
					}
				}
				
				if (!found) continue;
				else {
					if (initColor != null) {
						double distance = MathUtil.getDistance(game.getPlayer().getCoordonnéesf(), new Vec2f(r.x, r.y)) -1;
						float coef = miniMapRenderer.getCaseColor(distance).getAlphaf() / MapRenderer.CASE_MAX_ALPHA;
						
						r.render.setColor(initColor.multiply(coef, coef, coef, 1));
					}
				}
			}
			
			r.render(painter);
			r.render.setColor(initColor);
		}
	}
	
	private ArrayList<RenderEntityUnit> getRenderList(List<Entity> entities) {
		ArrayList<RenderEntityUnit> list = new ArrayList<RenderEntityUnit>();
		
		for (Entity e : entities) {
			addRenderFromEntity(e, list);
		}
		
		try {
			Collections.sort(list);
		} catch (IllegalArgumentException e) {
			/* Problème de contrat de comparaison : provient de la méthode RenderEntityUnit.compareTo().
			 * Voir la javadoc de compareTo() pour plus de détail.
			 * Il est possible que l'animation du joueur bugge. */
			e.printStackTrace();
		}
		
		return list;
	}
	
	private void addRenderFromEntity(Entity e, List<RenderEntityUnit> renderList) {
		Vec2f loc = e.getCoordonnéesf();
		RenderEntity render = e.getRender();
		
		if (render instanceof RenderCompound) {
			addRendersFromCompoundOne((RenderCompound) render, renderList, loc);
		}
		else {
			renderList.add(new RenderEntityUnit(render, loc.x, loc.y));
		}
	}
	
	private void addRendersFromCompoundOne(RenderCompound render,
			List<RenderEntityUnit> renderList, Vec2f loc) {
		
		for (RenderEntity r : render.getRenders()) {
			if (r instanceof RenderCompound) {
				addRendersFromCompoundOne((RenderCompound) r, renderList, loc);
			}
			else {
				renderList.add(new RenderEntityUnit(r, loc.x, loc.y));
			}
		}

		for (Entity e0 : render.getEntitiesToRender()) {
			addRenderFromEntity(e0, renderList);
		}
	}
}
