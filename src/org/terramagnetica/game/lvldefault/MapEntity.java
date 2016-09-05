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

import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.maths.geometric.Vec2f;

public class MapEntity {
	
	private float x, y;
	private TextureQuad texture = null;
	
	public MapEntity(Entity ent) {
		this(ent, 0, 0);
	}
	
	public MapEntity(Entity ent, float x, float y) {
		setLocation(x, y);
		setTexture(ent.getMinimapIcon());
	}
	
	public void setLocation(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public Vec2f getLocation() {
		return new Vec2f(this.x, this.y);
	}
	
	public void setTexture(TextureQuad tex) {
		this.texture = tex == null ? TexturesLoader.TEXTURE_NULL : tex;
	}
	
	public TextureQuad getTexture() {
		return this.texture;
	}
}
