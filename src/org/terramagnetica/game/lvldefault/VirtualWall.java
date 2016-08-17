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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.terramagnetica.game.lvldefault.rendering.RenderEntity;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityNothing;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

/**
 * Mur virtuel permettant d'empêcher les divers objets
 * (aimants, entités spécifique) de se mouvoir à l'exterieur
 * d'une zone précise.
 * @author Louis JEAN
 *
 */
public class VirtualWall extends CaseEntity {
	
	private static BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
	
	static {
		Graphics2D g = img.createGraphics();
		g.setColor(new Color(255, 0, 0, 32));
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		g.dispose();
	}
	
	private static final long serialVersionUID = 1L;
	
	public VirtualWall() {
		this(0, 0);
	}
	
	public VirtualWall(int x, int y) {
		super(x, y);
		this.hitbox.setSolid(false);
	}
	
	@Override
	public Image getImage() {
		return img;
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(256, 256);
	}
	
	@Override
	public boolean isMapVisible() {
		return false;
	}
	
	@Override
	protected RenderEntity createRender() {
		return new RenderEntityNothing();
	}
}
