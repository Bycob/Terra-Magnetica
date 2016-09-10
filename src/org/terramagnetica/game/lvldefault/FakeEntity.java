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

import org.terramagnetica.physics.Hitbox;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

/**
 * Cette entitée est une entité factice dont les paramètres
 * sont modulables à l'envie pour pouvoir appliquer des
 * traitements habituellement propres aux entités, entre des
 * objet plus simple ne représentant qu'une seule facette de
 * l'entité (Hitbox, dimensions, état solide ou fantôme...).
 * @author Louis JEAN
 */
public class FakeEntity extends Entity {
	
	private static final long serialVersionUID = 1L;
	
	

	private DimensionsInt dimensions = new DimensionsInt();

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	protected void createRender() {}
	
	public void setDimensions(DimensionsInt dims) {
		if (dims == null) throw new NullPointerException();
		this.dimensions = dims;
	}
	
	@Override
	public DimensionsInt getDimensions() {
		if (this.dimensions == null) return new DimensionsInt();//Cela peut arriver à l'initialisation.
		return this.dimensions;
	}
	
	public void setHitBoxf(Hitbox hb) {
		this.hitbox = hb;
	}
	
	@Override
	public FakeEntity clone() {
		FakeEntity clone = (FakeEntity) super.clone();
		
		clone.dimensions = this.dimensions.clone();
		
		return clone;
	}
}
