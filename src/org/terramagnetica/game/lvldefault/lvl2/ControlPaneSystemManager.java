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

package org.terramagnetica.game.lvldefault.lvl2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.terramagnetica.game.GameEngine;
import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.GameEngineModule;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec2i;

/**
 * Dans le niveau 2, le joueur doit desactiver trois panneaux de contrôle
 * pour pouvoir débloquer le passage et passer à la salle 2.
 * <p>Cette classe gère les désactivations de panneaux.
 * @author Louis JEAN
 *
 */
public class ControlPaneSystemManager extends GameEngineModule {
	
	/*
	 * Les couleurs ci-dessous sont éditables.
	 */
	
	public static final Color4f BLUE = new Color4f(81, 145, 242);
	public static final Color4f GREEN = new Color4f(92, 203, 102);
	public static final Color4f RED = new Color4f(255, 101, 76);
	
	private Map<Color4f, BarrierHandle> handleMap = new HashMap<Color4f, BarrierHandle>();
	
	public ControlPaneSystemManager() {
		this.handleMap.put(BLUE, new BarrierHandle(BLUE));
		this.handleMap.put(GREEN, new BarrierHandle(GREEN));
		this.handleMap.put(RED, new BarrierHandle(RED));
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		BarrierHandle[] handleArray = new BarrierHandle[this.handleMap.size()];
		int i = 0;
		for (Color4f color : this.handleMap.keySet()) {
			handleArray[i] = this.handleMap.get(color);
			i++;
		}
		out.writeArrayField(handleArray, 100);
	}
	
	@Override
	public Codable decode(BufferedObjectInputStream in) throws GameIOException {
		ArrayList<BarrierHandle> handleList = new ArrayList<BarrierHandle>();
		try {
			in.readListField(handleList, 100);
			for (BarrierHandle h : handleList) {
				this.handleMap.put(h.getColor(), h);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	private boolean handleInitialized = false;
	
	@Override
	public void init() {
		for (Color4f key : this.handleMap.keySet()) {
			initHandle(this.handleMap.get(key));
		}
		
		this.handleInitialized = true;
	}
	
	private void initHandle(BarrierHandle handle) {
		handle.removeAllListeners();
		
		for (Entity e : this.game.getEntities()) {
			
			if (e instanceof BarrierStateListener) {
				BarrierStateListener l = (BarrierStateListener) e;
				
				if (handle.getColor().equals(l.getColor())) {
					handle.addListener(l);
				}
			}
		}
	}
	
	@Override
	public void update(long dT) {
		if (!this.handleInitialized) init();
	}
	
	/**
	 * Désactive la barrière correspondant à la couleur passée en paramètre.
	 * @param barrierColor - La couleur du mur à desactiver.
	 */
	public void desactivate(Color4f barrierColor) {
		BarrierHandle handle = this.handleMap.get(barrierColor);
		
		if (handle != null) {
			this.game.interruptGame(new InterruptionChangeState(handle, false, this.game));
		}
	}
	
	/** @return Les coordonnées du pentagramme dans la salle 1. Tous
	 * les portails rammènent sur ce pentagramme. */
	public Vec2i getCenterOfRoom1() {
		return new Vec2i(61, 43);
	}
	
	private GamePlayingDefault game;
	
	@Override
	public void setGame(GameEngine game) {
		if (this.game == game) return;
		
		if (game instanceof GamePlayingDefault) {
			this.game = (GamePlayingDefault) game;
			this.handleInitialized = false;
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public boolean shouldSave() {
		return true;
	}
}
