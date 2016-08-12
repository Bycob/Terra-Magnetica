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

import java.util.HashMap;

import org.terramagnetica.game.GameRessources;

import net.bynaryscode.util.maths.geometric.Vec2f;

public class MapEntity {
	
	private static final HashMap<Class<? extends Entity>, String> mapEntityTextureMap = new HashMap<Class<? extends Entity>, String>();
	
	static {
		mapEntityTextureMap.put(Aimant.class, GameRessources.ID_MAP_CRYSTAL);
		mapEntityTextureMap.put(PlayerDefault.class, GameRessources.ID_MAP_PLAYER);
		mapEntityTextureMap.put(Lampe.class, GameRessources.ID_MAP_LAMP);
		mapEntityTextureMap.put(LampePerturbatrice.class, GameRessources.ID_MAP_LAMP_RANDOM);
		mapEntityTextureMap.put(MagneticFieldGenerator.class, GameRessources.ID_MAP_GENERATOR);
	}
	
	
	
	private float x, y;
	private String texture = "";
	
	public MapEntity() {
		this(0, 0);
	}
	
	public MapEntity(float x, float y) {
		this.x = x;
		this.y = y;
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
	
	public void setTexture(String tex) {
		this.texture = tex == null ? "" : tex;
	}
	
	public void setTexture(Class<? extends Entity> clazz) {
		setTexture(mapEntityTextureMap.get(clazz));
	}
	
	public String getTexture() {
		return this.texture;
	}
}
