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
import org.terramagnetica.opengl.engine.GLConfiguration;
import org.terramagnetica.opengl.engine.GLConfiguration.GLProperty;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Renderable;
import org.terramagnetica.opengl.engine.RenderableCompound;
import org.terramagnetica.opengl.engine.RenderableModel3D;
import org.terramagnetica.opengl.engine.RenderableShapedShadow;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderEntities extends RenderGameDefaultElement {
	
	/** Ordre de priorité des rendus, par classes. Plus un rendu possède un
	 * ordre de priorité élevé, plus il sera rendu tard. */
	private static HashMap<Class<? extends Renderable>, Integer> priorityOrder =
			new HashMap<Class<? extends Renderable>, Integer>();
	
	static {
		int i = -1;
		priorityOrder.put(RenderableShapedShadow.class, ++i);
		priorityOrder.put(RenderableModel3D.class, ++i);
		priorityOrder.put(RenderEntityTexture.class, ++i);
	}
	
	private static int getPriorityOrder(Class<? extends Renderable> clazz) {
		Integer value = priorityOrder.get(clazz);
		return value == null ? -1 : value;
	}
	
	
	private CameraFrustum frustum;
	
	
	private class RenderEntityUnit implements Comparable<RenderEntityUnit> {
		private float x;
		private float y;
		
		private Renderable render;
		
		private RenderEntityUnit(Renderable render, float x, float y) {
			this.render = render;
			this.x = x;
			this.y = y;
		}
		
		public void render(Painter painter) {
			if (isInFrustum(painter)) {
				this.render.renderAt(this.x, this.y, 0, painter);
			}
		}

		@Override
		public int compareTo(RenderEntityUnit o) {
			if (this.render.getClass() != o.render.getClass()) {
				int compareClass = getPriorityOrder(this.render.getClass()) - getPriorityOrder(o.render.getClass());
				if (compareClass != 0) return (int) Math.signum(compareClass);
			}
			
			float thisY = this.y;
			float oY = o.y;
			
			if (this.render instanceof RenderEntityTexture) {
				thisY = ((RenderEntityTexture) this.render).getRealPositionY(this.x, this.y);
			}
			
			if (o.render instanceof RenderEntityTexture) {
				oY = ((RenderEntityTexture) o.render).getRealPositionY(o.x, o.y);
			}
			
			//Comparaison des angles
			/*if (this.render instanceof RenderEntityTexture && o.render instanceof RenderEntityTexture) {
				int radDif = (int) Math.signum(((RenderEntityTexture) o.render).getRadius()
						- ((RenderEntityTexture) this.render).getRadius());
				if (radDif != 0) return radDif;
			}*/
			
			return (int) Math.signum(oY - thisY);
		}
		
		private boolean isInFrustum(Painter painter) {
			CameraFrustum camFrustum = RenderEntities.this.frustum;
			Vec3d[] points = this.render.getRenderBoundingBox(this.x, this.y, 0).getPoints();
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
		
		GLConfiguration config = this.getDefault3DConfiguration();
		painter.setConfiguration(config);
		this.frustum = painter.createCameraFrustum();
		
		// Définition des variables nécessaires au rendu
		ArrayList<RenderEntityUnit> renderList = getRenderList(game.getEntities());
		MapUpdater miniMapManager = game.getAspect(MapUpdater.class);
		MapRenderer miniMapRenderer = miniMapManager.getRenderer();
		MapLandscape[] limVisionCaseArray = miniMapManager.getMap().getAllLandscapeMapped();
		
		for (RenderEntityUnit r : renderList) {
			
			Color4f initColor = r.render.getColor();
			
			//Si la vision limitée est activée, on ne dessine pas tout
			if (game.hasLimitedVision() && !game.render.isScrolling()) {
				boolean found = false;
				
				for (MapLandscape ml : limVisionCaseArray) {
					if (Math.abs(ml.getCaseX() - Math.floor(r.x)) <= 1 && Math.abs(ml.getCaseY() - Math.floor(- r.y)) <= 1) {
						found = true;
						break;
					}
				}
				
				if (!found) continue;
				else {
					if (initColor != null) {
						double distance = MathUtil.getLength(game.getPlayer().getPositionf(), new Vec2f(r.x, -r.y)) -1;
						float coef = miniMapRenderer.getCaseColor(distance).getAlphaf() / MapRenderer.CASE_MAX_ALPHA;
						
						r.render.setColor(initColor.multiply(coef, coef, coef, 1));
					}
				}
			}
			
			// Dessin du rendu
			if (r.render instanceof RenderableModel3D) {
				config.setPropertieEnabled(GLProperty.LIGHTING, true);
			}
			else {
				config.setPropertieEnabled(GLProperty.LIGHTING, false);
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
			 * Il est possible que l'animation du joueur bug. */
			e.printStackTrace();
		}
		
		return list;
	}
	
	private void addRenderFromEntity(Entity e, List<RenderEntityUnit> renderList) {
		Vec2f loc = e.getPositionf();
		Renderable render = e.getRender();
		
		addRenderToRenderList(render, renderList, loc);
	}

	private void addRenderToRenderList(Renderable render, List<RenderEntityUnit> renderList, Vec2f loc) {
		
		if (render instanceof RenderableCompound) {
			addRendersFromCompoundOne((RenderableCompound) render, renderList, loc);
		}
		else if (render instanceof RenderEntityCompound) {
			for (Entity entity : ((RenderEntityCompound) render).getEntitiesToRender()) {
				addRenderFromEntity(entity, renderList);
			}
		}
		else {
			renderList.add(new RenderEntityUnit(render, loc.x, - loc.y));
		}
	}
	
	private void addRendersFromCompoundOne(RenderableCompound render,
			List<RenderEntityUnit> renderList, Vec2f loc) {
		
		for (Renderable r : render.getRenders()) {
			addRenderToRenderList(r, renderList, loc);
		}
	}
}
