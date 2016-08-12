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

import java.awt.Image;

import org.terramagnetica.game.lvldefault.rendering.RenderEntity;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityNothing;
import org.terramagnetica.ressources.ImagesLoader;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

public class Mark extends Entity {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Image getImage() {
		return ImagesLoader.get(ImagesLoader.marqueur);
	}

	@Override
	protected RenderEntity createRender() {
		return new RenderEntityNothing();
	}

	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(128, 128);
	}
	
	@Override
	public DimensionsInt getImgDimensions() {
		return new DimensionsInt(128, 128);
	}
	
	@Override
	public boolean isSolid() {
		return false;
	}
	
	@Override
	public boolean isMapVisible() {
		return false;
	}
	
	public void setID(String id) {
		this.skin = id;
	}
	
	public String getID() {
		return this.skin;
	}
	
	@Override
	public Mark clone() {
		return (Mark) super.clone();
	}
 }
