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

package org.terramagnetica.opengl.engine;

import net.bynaryscode.util.maths.geometric.Vec2d;

public class TextureImpl implements Texture, Cloneable {
	
	private int id;
	
	public TextureImpl() {
		this(0);
	}
	
	public TextureImpl(int texID) {
		this.id = texID;
	}
	
	@Override
	public int getGLTextureID() {
		return this.id;
	}

	@Override
	public Texture withTextureID(int texID) {
		this.setTextureID(texID);
		return this;
	}

	@Override
	public void setTextureID(int texID) {
		this.id = texID;
	}

	@Override
	public Vec2d[] getSTSommets() {
		return new Vec2d[0];
	}

	@Override
	public int getNbSommets() {
		return 0;
	}
	
	@Override
	public TextureImpl clone() {
		TextureImpl clone = null;
		try {
			clone = (TextureImpl) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return clone;
	}
}
