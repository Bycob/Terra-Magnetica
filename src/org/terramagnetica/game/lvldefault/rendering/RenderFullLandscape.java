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

import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.LandscapeTile;
import org.terramagnetica.game.lvldefault.MapLandscape;
import org.terramagnetica.game.lvldefault.MapUpdater;
import org.terramagnetica.opengl.engine.CameraFrustum;
import org.terramagnetica.opengl.engine.GLConfiguration;
import org.terramagnetica.opengl.engine.GLConfiguration.GLProperty;
import org.terramagnetica.opengl.engine.Light;
import org.terramagnetica.opengl.engine.Light.LightColor;
import org.terramagnetica.opengl.engine.Painter;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.RectangleInt;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.maths.geometric.Vec3d;

/**
 * Dessine le décor en entier.
 * @author Louis JEAN
 */
public class RenderFullLandscape extends RenderGameDefaultElement {
	
	private CameraFrustum frustum;
	
	@Override
	public void render(GamePlayingDefault game, Painter painter) {
		//Configuration du painter
		GLConfiguration config = getDefault3DConfiguration();
		
		painter.setConfiguration(config);
		config.setPropertieEnabled(GLProperty.LIGHTING, true);
		
		Light light = painter.getLightModel().getLight0();
		light.setLightColor(LightColor.AMBIENT, new Color4f(0.5f, 0.5f, 0.5f, 0.5f));
		
		this.frustum = painter.createCameraFrustum();
		
		//Initialisation des variables
		Vec2i location = null;
		RenderLandscape landRender = null;
		
		MapUpdater miniMapManager = game.getAspect(MapUpdater.class);
		MapRenderer miniMapRenderer = miniMapManager.getRenderer();
		MapLandscape[] limVisionArray = miniMapManager.getMap().getAllLandscapeMapped();
		
		
		//Détermination des limites de la vue
		RectangleInt bounds = new RectangleInt();
		Vec3d cameraCenter = config.getCamera3D().getCenter();
		Vec2i centerCase = new Vec2i((int) cameraCenter.x, - ((int) cameraCenter.y));
		//Limite inférieure
		Vec2i testedPoint = centerCase.clone();
		while (isCaseCenterInFrustum(testedPoint)) {
			testedPoint.y ++;
		}
		bounds.ymax = testedPoint.y + 1;
		//Limite supérieure
		testedPoint = centerCase.clone();
		while (isCaseCenterInFrustum(testedPoint)) {
			testedPoint.y --;
		}
		bounds.ymin = testedPoint.y;
		//Limite gauche (on reste à la limite supérieure pour tout englober)
		testedPoint.y++;
		while (isCaseCenterInFrustum(testedPoint)) {
			testedPoint.x--;
		}
		bounds.xmin = testedPoint.x;
		//Limite droite calculée automatiquement
		bounds.xmax = 2 * centerCase.x - testedPoint.x;
		
		
		for (int x = bounds.xmin ; x <= bounds.xmax ; x++) {
			for (int y = bounds.ymin ; y < bounds.ymax ; y++) {
				
				LandscapeTile l = game.getLandscapeAt(x, y);
				if (!isInFrustum(l)) continue;
				
				//Si la vision limitée est activée, on ne dessine pas tout
				if (game.hasLimitedVision()) {
					boolean found = false;
					for (MapLandscape ml : limVisionArray) {
						if (Math.abs(ml.getCaseX() - x) <= 1 && Math.abs(ml.getCaseY() - y) <= 1) {
							found = true;
						}
					}
					
					if (!found) continue;
					else {
						float alpha = miniMapRenderer.getCaseColor(game.getPlayer().getDistancef(l) - 1).getAlphaf()
								/ MapRenderer.CASE_MAX_ALPHA;
						light.setLightColor(LightColor.DIFFUSE, new Color4f(alpha, alpha, alpha, 0.5f));
					}
				}
				
				landRender = l.getRender(game.getDecorType());
				location = l.getCoordonnéesCase();
				landRender.renderLandscape3D(location.x, location.y, painter);
			}
		}
		
		config.setPropertieEnabled(GLProperty.LIGHTING, false);
	}
	
	private boolean isInFrustum(LandscapeTile terrain) {
		CameraFrustum cf = this.frustum;
		for (Vec2d c : terrain.getBoundsf().getVertices()) {
			if (cf.containsPoint(new Vec3d(c.x, - c.y, 0))) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isCaseCenterInFrustum(Vec2i cCase) {
		return this.frustum.containsPoint(new Vec3d(cCase.x + 0.5, - cCase.y - 0.5, 0));
	}
}
