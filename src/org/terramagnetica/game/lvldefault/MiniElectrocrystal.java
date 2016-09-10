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
import org.terramagnetica.game.lvldefault.rendering.RenderEntityTexture;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

/**
 * Un petit cristal électrifié, étant controllé par une autre entité,
 * comme par exemple un générateur de champs.
 * @author Louis JEAN
 *
 */
public class MiniElectrocrystal extends Entity {
	
	private static final long serialVersionUID = 1L;
	
	public MiniElectrocrystal() {
		super();
	}
	
	MiniElectrocrystal(float x, float y) {
		this.hitbox.setPosition(x, y);
	}
	
	@Override
	public Image getImage() {
		return null;
	}
	
	@Override
	public void createRender() {
		this.renderManager.putRender("default", new RenderEntityTexture(GameRessources.ID_MINI_ELECTROCRYSTAL));
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(64, 64);
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		PlayerDefault thePlayer = game.getPlayer();
		
		if (hasCollision(thePlayer)) {
			game.setDead();
		}
	}
}
