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

package org.terramagnetica.game.lvldefault.lvl3;

import java.awt.Image;

import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.rendering.RenderObject;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityModel3D;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

public class Tree extends Entity {
	
	private static final long serialVersionUID = 1L;
	
	private String treeModelID;
	
	public Tree() {
		this(/* TODO le modèle par défaut */ "");
	}
	
	public Tree(String model) {
		this.treeModelID = model;
	}
	
	@Override
	public Image getImage() {
		return null;
	}

	@Override
	protected RenderObject createRender() {
		return new RenderEntityModel3D(this.treeModelID);
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt();
	}
	
	@Override
	public DimensionsInt getImgDimensions() {
		return new DimensionsInt();
	}
	
	@Override
	public boolean equals(Object other) {
		if (!super.equals(other) || !(other instanceof Tree)) return false;
		
		
		return true;
	}
	
	@Override
	public Tree clone() {
		Tree clone = (Tree) super.clone();
		
		return clone;
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
	}
	
	@Override
	public Tree decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		
		return this;
	}
}
