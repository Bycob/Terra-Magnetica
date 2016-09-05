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

import java.util.HashMap;
import java.util.Map;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.DecorType;
import org.terramagnetica.game.lvldefault.WallTile;
import org.terramagnetica.opengl.engine.Light;
import org.terramagnetica.opengl.engine.Model3D;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Transform;
import org.terramagnetica.ressources.ModelLoader;

import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderLandscapeMur3D extends RenderLandscape {
	
	/** angle de rotation (en degrés) en fonction du type de mur. */
	private static final Map<Integer, Float> rotationMap = new HashMap<Integer, Float>();
	/** transformation du {@link Float} en {@code float} */
	private static float get0(int key) {
		Float f = rotationMap.get(key);
		return f == null ? 0f : f;
	}
	
	static {
		rotationMap.put(WallTile.GAUCHE, 0f);
		rotationMap.put(WallTile.BAS, 90f);
		rotationMap.put(WallTile.DROITE, 180f);
		rotationMap.put(WallTile.HAUT, 270f);
		rotationMap.put(WallTile.COIN_GAUCHE_HAUT, 0f);
		rotationMap.put(WallTile.COIN_GAUCHE_BAS, 90f);
		rotationMap.put(WallTile.COIN_DROIT_BAS, 180f);
		rotationMap.put(WallTile.COIN_DROIT_HAUT, 270f);
		rotationMap.put(WallTile.ANGLE_DROIT_BAS, 0f);
		rotationMap.put(WallTile.ANGLE_DROIT_HAUT, 90f);
		rotationMap.put(WallTile.ANGLE_GAUCHE_HAUT, 180f);
		rotationMap.put(WallTile.ANGLE_GAUCHE_BAS, 270f);
	}
	
	private Model3D modelMur;
	private int type;
	private float rotation;
	
	private DecorType decor;
	
	public RenderLandscapeMur3D() {
		this(WallTile.DROITE, DecorType.MONTS);
	}
	
	public RenderLandscapeMur3D(int type, DecorType decor) {
		this.decor = decor;
		setType(type);
	}
	
	public void setType(int type) {
		this.type = type;
		this.rotation = get0(type);
		String modelID = "";
		
		switch (this.type) {
		
		case WallTile.DROITE :
		case WallTile.HAUT :
		case WallTile.GAUCHE :
		case WallTile.BAS :
			modelID = GameRessources.SPEC_PATH_MODEL_MUR_DROIT;
			break;
		case WallTile.COIN_GAUCHE_HAUT :
		case WallTile.COIN_DROIT_HAUT :
		case WallTile.COIN_DROIT_BAS :
		case WallTile.COIN_GAUCHE_BAS :
			modelID = GameRessources.SPEC_PATH_MODEL_MUR_COIN;
			break;
		case WallTile.ANGLE_DROIT_BAS :
		case WallTile.ANGLE_DROIT_HAUT :
		case WallTile.ANGLE_GAUCHE_BAS :
		case WallTile.ANGLE_GAUCHE_HAUT :
			modelID = GameRessources.SPEC_PATH_MODEL_MUR_ANGLE;
			break;
		}
		
		int decorTypeID = this.decor.getIndex() + 1;
		if ((this.modelMur = ModelLoader.get(Util.formatDecimal(modelID, decorTypeID))) == null) {
			this.modelMur = ModelLoader.get(Util.formatDecimal(GameRessources.SPEC_PATH_MODEL_MUR_DROIT, decorTypeID));
		}
	}
	
	@Override
	public void renderLandscape3D(int x, int y, Painter painter) {
		Light light = painter.getLightModel().getLight0();
		
		painter.pushTransformState();
		
		painter.addTransform(Transform.newTranslation(x + 0.5f, -y - 0.5f, 0));
		painter.addTransform(Transform.newRotation(this.rotation, new Vec3d(0, 0, 1)));
		
		light.setPosition(0, 0, 1);
		
		this.modelMur.draw(painter);
		
		painter.popTransformState();
	}
}
