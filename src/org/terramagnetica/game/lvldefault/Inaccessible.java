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

import org.terramagnetica.game.lvldefault.rendering.RenderLandscape;
import org.terramagnetica.ressources.ImagesLoader;

public class Inaccessible extends LandscapeTile {
	
	private static final long serialVersionUID = 1L;

	public Inaccessible() {
		super();
	}

	public Inaccessible(int x, int y, boolean isCased){
		super(x, y);
	}
	
	@Override
	public Image getImage(DecorType type) {
		return ImagesLoader.get(pathTerrainArray[type.ordinal()] + TEX_INACCESSIBLE);
	}
	
	@Override
	public RenderLandscape createRender(DecorType type) {
		float height;
		
		switch (type) {
		case MONTS : height = 0.5f; break;
		case GROTTE : height = 0.0f; break;
		default : height = 0.5f;
		}
		
		return new RenderLandscape(pathTerrainArray[type.ordinal()] + TEX_INACCESSIBLE, height);
	}
	
	@Override
	public boolean isEnabled(){
		return false;
	}
}
