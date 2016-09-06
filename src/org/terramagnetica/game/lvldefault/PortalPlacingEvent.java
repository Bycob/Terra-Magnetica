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
import org.terramagnetica.ressources.io.GameIOException;

public class PortalPlacingEvent extends GameEvent {
	
	private int caseX, caseY;
	
	public PortalPlacingEvent(int caseX, int caseY) {
		this.caseX = caseX;
		this.caseY = caseY;
	}
	
	@Override
	public void trigger(GamePlayingDefault game) {
		PlayerDefault p = game.getPlayer();
		if (game.getLandscapeAt(this.caseX, this.caseY).isEnabled()) {
			p.setCasePosition(this.caseX, this.caseY);
		}
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
	}
	
	@Override
	public PortalPlacingEvent decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		return this;
	}
}
