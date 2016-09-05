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

import org.terramagnetica.game.lvldefault.rendering.RenderEntity;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityDefault;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

public class DecorationEntity extends Entity {
	
	private static final long serialVersionUID = 1L;
	
	private boolean model = false;
	private boolean onGround = false;
	
	public DecorationEntity() {
		super();
		init();
	}
	
	public DecorationEntity(int x, int y) {
		super(x, y);
		init();
	}
	
	public DecorationEntity(float x, float y) {
		init();
		this.setCoordonnéesf(x, y);
	}
	
	public DecorationEntity(int x, int y, String decoID) {
		super(x, y);
		init();
		this.skin = decoID;
	}
	
	public DecorationEntity(float x, float y, String decoID) {
		this(x, y);
		this.skin = decoID;
	}
	
	private void init() {
		this.hitbox.setSolid(false);
	}
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(ImagesLoader.decoration);
	}
	
	@Override
	public RenderEntity createRender() {
		return this.onGround ? new RenderEntityDefault(this.skin).setOnGround(true) : new RenderEntityDefault(this.skin);
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(32, 32);
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
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		out.writeStringField(this.skin, 100);
		out.writeBoolField(this.onGround, 101);
	}
	
	@Override
	public DecorationEntity decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		this.skin = in.readStringField(100);
		this.onGround = in.readBoolFieldWithDefaultValue(101, this.onGround);
		
		return this;
	}
}
