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

import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.geometric.Vec2i;

public class LandscapeInfos implements Codable {
	
	private String skin;
	private Vec2i location;
	
	public LandscapeInfos() {
		this("", new Vec2i(0, 0));
	}
	
	public LandscapeInfos(String skin, Vec2i loc) {
		this.skin = skin;
		this.location = loc.clone();
	}
	
	public Vec2i getLocation() {
		return this.location.clone();
	}
	
	public String getSkin() {
		return this.skin;
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		out.writeIntField(location.x, 0);
		out.writeIntField(location.y, 1);
		out.writeStringField(this.skin, 2);
	}

	@Override
	public LandscapeInfos decode(BufferedObjectInputStream in) throws GameIOException {
		this.location = new Vec2i(in.readIntField(0), in.readIntField(1));
		this.skin = in.readStringField(2);
		return this;
	}
}
