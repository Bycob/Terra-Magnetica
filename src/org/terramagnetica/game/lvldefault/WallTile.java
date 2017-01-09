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

import static org.terramagnetica.game.GameRessources.*;

import java.awt.Image;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityTexture;
import org.terramagnetica.opengl.engine.Model3D;
import org.terramagnetica.opengl.engine.Renderable;
import org.terramagnetica.opengl.engine.RenderableModel3D;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.ModelLoader;

import net.bynaryscode.util.Util;

public class WallTile extends OrientableLandscapeTile {
	
	private static final long serialVersionUID = 1L;
	
	public WallTile(){
		super();
		orientation = PLANE;
	}
	
	public WallTile(int orientation) {
		super();
		this.setOrientation(orientation);
	}
	
	public WallTile(int x, int y, int type){
		super(x, y);
		this.setOrientation(type);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof WallTile)) {
			return false;
		}
		return true;
	}
	
	@Override
	public Image getImage(DecorType type){
		Image result = ImagesLoader.get(
			pathTerrainArray[type.ordinal()] + Util.formatDecimal(SPEC_TEX_MUR, this.orientation));
		if (result != null) return result;
		return ImagesLoader.get(pathTerrainArray[type.ordinal()] + TEX_INACCESSIBLE);
	}
	
	@Override
	public Renderable createRender(DecorType type, RenderRegistry renders) {
		float height;
		boolean orientationChanges;//Indique si le mur est différent suivant son orientation.
		
		//Choix des propriétés :
		switch (type) {
		case MONTS : 
			height = 0.5f;
			orientationChanges = true;
			break;
		case GROTTE :
			height = 0.0f;
			orientationChanges = true;
			break;
		default :
			height = 0.5f;
			orientationChanges = true;
		}

		//Rendu du mur central
		if (this.orientation == PLANE) {

			String id = pathTerrainArray[type.ordinal()] + TEX_INACCESSIBLE;

			Renderable render = renders.getRender(id);
			if (render == null) {
				RenderEntityTexture ret = new RenderEntityTexture(id).setOnGround(true);
				ret.setPositionOffset(0, 0, height);
				ret.setOnGroundOffset(0);
				ret.setMaterial(getMaterial(type));
				
				render = ret;
				renders.registerRender(id, render);
			}
			
			return render;
		}
		
		//Détermination du modèle :
		float rotation = getRotation(this.orientation);
		String modelID = "";
		
		switch (this.orientation) {
		
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
		
		int decorTypeID = type.getIndex() + 1;
		Model3D modelMur;
		if ((modelMur = ModelLoader.getNotNull(Util.formatDecimal(modelID, decorTypeID))) == null) {
			modelMur = ModelLoader.getNotNull(Util.formatDecimal(GameRessources.SPEC_PATH_MODEL_MUR_DROIT, decorTypeID));
		}
		
		return new RenderableModel3D(modelMur).withRotationOffset(0, 0, orientationChanges ? rotation : 0f);
	}
	
	@Override
	public boolean isEnabled(){
		return false;
	}
}
