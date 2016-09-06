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

/**
 * Un déclencheur (voir {@link Triggerer}) qui change d'état lorsque
 * le joueur sort ou entre dans la zone.
 * @author Louis JEAN
 *
 */
public class ZoneStateTriggerer extends CaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	/** La zone qui déclenchera le triggerer. */
	private Zone zone;
	
	private boolean state = false;
	
	public ZoneStateTriggerer() {
		super();
		this.setZone(new RectangleZone());
		this.hitbox.setSolid(false);
	}
	
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(ImagesLoader.declencheur);
	}
	
	@Override
	public RenderEntity createRender() {
		return new RenderEntityNothing();
	}
	
	@Override
	public boolean isMapVisible() {
		return false;
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(CASE / 2, CASE / 2);
	}
	
	//----------
	
	public void setZone(Zone zone) {
		if (zone == null) throw new NullPointerException();
		this.zone = zone;
	}
	
	public Zone getZone() {
		return this.zone;
	}
	
	public boolean isInZone(Entity e) {
		return this.zone.isInZone(e.getPositionf());
	}
	
	//----------
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		boolean newState = isInZone(game.getPlayer());
		
		if (this.state != newState) {
			if (newState) onEntering(game);
			else onExiting(game);
		}
		
		this.state = newState;
	}
	
	//----------
	
	private GameEvent onEnteringEvt = null;
	private GameEvent onExitingEvt = null;
	
	public GameEvent getEnterEvent() {
		return onEnteringEvt;
	}

	public void setEnterEvent(GameEvent onEnteringEvt) {
		this.onEnteringEvt = onEnteringEvt;
	}

	public GameEvent getExitEvent() {
		return onExitingEvt;
	}

	public void setExitEvent(GameEvent onExitingEvt) {
		this.onExitingEvt = onExitingEvt;
	}
	
	
	void onEntering(GamePlayingDefault game) {
		if (this.onEnteringEvt != null) {
			game.getAspect(GameEventDispatcher.class).addEvent(this.onEnteringEvt);
		}
	}
	
	void onExiting(GamePlayingDefault game) {
		if (this.onExitingEvt != null) {
			game.getAspect(GameEventDispatcher.class).addEvent(this.onExitingEvt);
		}
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		
		out.writeBoolField(this.state, 200);
		out.writeCodableField(this.zone, 201);
		out.writeCodableField(this.onEnteringEvt, 202);
		out.writeCodableField(this.onExitingEvt, 203);
	}
	
	@Override
	public ZoneStateTriggerer decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		
		this.state = in.readBoolField(200);
		
		try {
			this.zone = in.readCodableInstanceField(Zone.class, 201);
			this.onEnteringEvt = in.readCodableInstanceField(GameEvent.class, 202);
			this.onExitingEvt = in.readCodableInstanceField(GameEvent.class, 203);
		} catch (Exception e) {
			throw new GameIOException(e);
		}
		
		return this;
	}
}
