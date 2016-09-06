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
import org.terramagnetica.game.lvldefault.rendering.RenderEntityNothing;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

public class Triggerer extends Entity {
	
	private static final long serialVersionUID = 1L;
	
	private GameEvent evt;
	
	/** Indique si le joueur est en train de marcher dans la zone
	 * activant le triggerer. Si cette variable passe à {@code true},
	 * cela déclenchera l'evènement une unique fois. */
	private boolean triggered = false;

	public Triggerer() {
		this(null);
	}
	
	public Triggerer(GameEvent evt) {
		this.evt = evt;
		
		this.hitbox.setSolid(false);
	}
	
	@Override
	public void setPositionf(float x, float y) {
		super.setPositionf(x, y);
		
		if (this.evt != null) {
			this.evt.setEventLocation(this.getCasePosition().x, this.getCasePosition().y);
		}
	}
	
	@Override
	public RenderEntity createRender() {
		return new RenderEntityNothing();
	}
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(ImagesLoader.declencheur);
	}

	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(32, 32);
	}
	
	@Override
	public DimensionsInt getImgDimensions() {
		return new DimensionsInt(CASE / 2, CASE / 2);
	}
	
	@Override
	public boolean isMapVisible() {
		return false;
	}
	
	public void setEvent(GameEvent evt) {
		this.evt = evt;
	}
	
	@Override
	public void updateLogic(long dt, GamePlayingDefault game) {
		
		boolean oldTriggered = this.triggered;
		this.triggered = getDistancef(game.getPlayer()) <= 1f && this.evt != null;
		
		if (this.triggered && ! oldTriggered) {
			game.getAspect(GameEventDispatcher.class).addEvent(this.evt);
		}
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		out.writeBoolField(this.triggered, 100);
		out.writeCodableField(this.evt, 101);
	}
	
	@Override
	public Triggerer decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		this.triggered = in.readBoolFieldWithDefaultValue(100, this.triggered);
		try {
			this.evt = in.readCodableInstanceField(GameEvent.class, 101);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
	@Override
	public Triggerer clone() {
		Triggerer clone = (Triggerer) super.clone();
		
		clone.evt = this.evt.clone();
		
		return clone;
	}
}
