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

import org.terramagnetica.game.GameEngine;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

public class PortalNameFinder extends GameAspect {
	
	private GamePlayingDefault game;
	
	public PortalNameFinder(GamePlayingDefault game) {
		this.setGame(game);
		this.init();
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		
	}

	@Override
	public PortalNameFinder decode(BufferedObjectInputStream in) throws GameIOException {
		return this;
	}

	@Override
	public void init() {
		update(0);
	}

	@Override
	public void update(long dT) {
		Portal nearestPortal = null;
		PlayerDefault player = this.game.getPlayer();
		
		for (Entity entity : this.game.getEntities()) {
			
			if (entity instanceof Portal) {
				double df = entity.getDistancef(player);
				if (df < 5) {
					if (nearestPortal == null) nearestPortal = (Portal) entity;
					else if (nearestPortal.getDistancef(player) > df) {
						nearestPortal = (Portal) entity;
					}
				}
			}
		}
		
		if (nearestPortal != null) {
			this.game.render.displayPortalName(nearestPortal.getName());
		}
		else {
			this.game.render.displayPortalName("");
		}
	}

	@Override
	public void setGame(GameEngine game) {
		if (game == null) throw new NullPointerException("game == null");
		if (game instanceof GamePlayingDefault) {
			this.game = (GamePlayingDefault) game;
		}
		else throw new IllegalArgumentException("Seuls les " + GamePlayingDefault.class.getSimpleName() + " sont acceptés.");
	}
	
	@Override
	public boolean shouldSave() {
		return false;
	}
}
