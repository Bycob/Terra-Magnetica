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

import java.awt.Image;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.rendering.RenderEntity;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityTexture;
import org.terramagnetica.ressources.ImagesLoader;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

/**
 * Des points verdâtres au sol, qui perturbent légèrement le
 * trajet des aimants autour des lampes.
 * @author Louis JEAN
 *
 */
public class MagneticFieldPerturbateur extends Entity {
	
	private static final long serialVersionUID = 1L;
	
	private DirectionVariable direction = new DirectionVariable();
	
	public MagneticFieldPerturbateur() {
		this.hitbox.setSolid(false);
	}
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(GameRessources.ID_PERTURBATEUR);
	}
	
	@Override
	public RenderEntity createRender() {
		return new RenderEntityTexture(GameRessources.ID_PERTURBATEUR).setOnGround(true);
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(0, 0);
	}
	
	@Override
	public DimensionsInt getImgDimensions() {
		return new DimensionsInt(128, 128);
	}
	
	@Override
	public boolean isMapVisible() {
		return false;
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		this.direction.update(dT);
	}
}
