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

import static org.terramagnetica.game.GameRessources.*;

import java.awt.Image;

import org.terramagnetica.game.lvldefault.rendering.RenderLandscape;
import org.terramagnetica.game.lvldefault.rendering.RenderLandscapeMur3D;
import org.terramagnetica.ressources.ImagesLoader;

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
	public RenderLandscape createRender(DecorType type) {
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
		
		
		if (this.orientation == PLANE) {//Rendu du mur central
			return new RenderLandscape(pathTerrainArray[type.ordinal()] + TEX_INACCESSIBLE, height);
		}
		
		return new RenderLandscapeMur3D(orientationChanges ? this.orientation : WallTile.GAUCHE, type);
	}
	
	@Override
	public boolean isEnabled(){
		return false;
	}
}
