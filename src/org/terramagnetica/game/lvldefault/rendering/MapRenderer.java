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

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.LandscapeTile;
import org.terramagnetica.game.lvldefault.MapEntity;
import org.terramagnetica.game.lvldefault.MapLandscape;
import org.terramagnetica.game.lvldefault.MapUpdater;
import org.terramagnetica.game.lvldefault.MiniMap;
import org.terramagnetica.game.lvldefault.PlayerDefault;
import org.terramagnetica.opengl.engine.GLOrtho;
import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Painter.Primitive;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.gui.GuiWindow;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec2f;

public class MapRenderer extends RenderGameDefaultElement {
	
	/** Une image de 16 pixel représente une entité de taille une case.*/
	public static final int IMG_CASE_SIZE = 32;
	
	public static final float CASE_RENDERED = 0.04f;
	public static final float ENTITY_RENDERED = 0.03f;
	
	public static final float CASE_MAX_ALPHA = 0.5f;
	
	private MiniMap theMap = null;
	private MapUpdater theMapUpdater;
	
	public MapRenderer() {
		this(null, null);
	}
	
	public MapRenderer(MiniMap map, MapUpdater updater) {
		this.theMap = map;
		this.theMapUpdater = updater;
	}
	
	@Override
	public void render(GamePlayingDefault game, Painter painter) {
		if (this.theMap != null) {
			painter.ensure2D();
			painter.setPrimitive(Primitive.QUADS);
			
			GLOrtho repere = GuiWindow.getInstance().getOrtho();
			float maxDistance = this.theMapUpdater.getMaxDistance();
			float offset = (3 + maxDistance) * CASE_RENDERED;
			//origine de la carte : personnage.
			Vec2d o = new Vec2d(repere.right - offset, repere.bottom + offset);
			
			painter.setColor(new Color4f(1f, 1f, 1f));
			RectangleDouble bounds = RectangleDouble.createRectangleFromCenter(o.x, o.y,
					maxDistance * 2 * CASE_RENDERED, maxDistance * 2 * CASE_RENDERED);
			painter.setTexture(TexturesLoader.get(GameRessources.ID_MAP_BACKGROUND));
			GLUtil.drawQuad2D(bounds, painter);
			
			painter.setTexture(null);
			
			PlayerDefault player = game.getPlayer();
			Vec2f playerc = player.getCoordonnéesf();
			
			//Dessin du paysage (les cases)
			MapLandscape landMapped[] = this.theMap.getAllLandscapeMapped();
			for (MapLandscape ml : landMapped) {
				LandscapeTile l = game.getLandscapeAt(ml.getCaseX(), ml.getCaseY());
				
				double d = player.getDistancef(l);
				Vec2f lc = l.getCoordonneesf();
				float dX = lc.x - playerc.x;
				float dY = playerc.y - lc.y;
				
				Color4f color = getCaseColor(d);
				painter.setColor(color);
				painter.addVertex(o.x + (dX - 0.5) * CASE_RENDERED, o.y + (dY + 0.5) * CASE_RENDERED);
				painter.addVertex(o.x + (dX + 0.5) * CASE_RENDERED, o.y + (dY + 0.5) * CASE_RENDERED);
				painter.addVertex(o.x + (dX + 0.5) * CASE_RENDERED, o.y + (dY - 0.5) * CASE_RENDERED);
				painter.addVertex(o.x + (dX - 0.5) * CASE_RENDERED, o.y + (dY - 0.5) * CASE_RENDERED);
			}
			
			//Dessin des entités
			MapEntity entitiesMapped[] = this.theMap.getAllEntitiesMapped();
			for (MapEntity me : entitiesMapped) {
				TextureQuad tex = me.getTexture();
				painter.setTexture(tex);
				
				int imgSize;
				
				float alpha = getCaseColor(MathUtil.getDistance(me.getLocation(), playerc)).getAlphaf() / CASE_MAX_ALPHA;
				
				if (tex == TexturesLoader.TEXTURE_NULL) {
					painter.setColor(new Color4f(1f, 1f, 0f, 0.5f * alpha));
					imgSize = IMG_CASE_SIZE;
				}
				else {
					painter.setColor(new Color4f(1f, 1f, 1f, alpha));
					imgSize = tex.getWidth();
				}
				
				Vec2d eCentre = new Vec2d(
						o.x + (me.getX() - playerc.x) * CASE_RENDERED,
						o.y + (playerc.y - me.getY()) * CASE_RENDERED);
				double hsize = (tex == TexturesLoader.TEXTURE_NULL ? ENTITY_RENDERED / 2 : ENTITY_RENDERED) * imgSize / IMG_CASE_SIZE;
				
				painter.addVertex(eCentre.x - hsize, eCentre.y + hsize);
				painter.addVertex(eCentre.x + hsize, eCentre.y + hsize);
				painter.addVertex(eCentre.x + hsize, eCentre.y - hsize);
				painter.addVertex(eCentre.x - hsize, eCentre.y - hsize);
			}
		}
	}
	
	public Color4f getCaseColor(double distance) {
		float alpha;
		float maxDistance = this.theMapUpdater.getMaxDistance();
		
		if (distance > maxDistance - 1) {
			alpha = CASE_MAX_ALPHA * (float) (maxDistance - distance);
		}
		else {
			alpha = CASE_MAX_ALPHA;
		}
		return new Color4f(1f, 1f, 1f, alpha);
	}
}
