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

import org.terramagnetica.game.GameBuffer;
import org.terramagnetica.game.GameEngine;

public class GameBufferDefault extends GameBuffer {
	
	private GamePlayingDefault game;
	private Object lock = new Object();
	
	@Override
	public void write(GameEngine game) {
		GamePlayingDefault game1 = null;
		
		if (game instanceof GamePlayingDefault) {
			game1 = (GamePlayingDefault) game;
		}
		else {
			throw new IllegalArgumentException("Le type de buffer n'est pas respecté !");
		}
		
		synchronized (this.lock) {
			this.game = (game1 != null) ? game1.clone() : null;
			this.hasChanged = true;
		}
	}
	
	@Override
	public GamePlayingDefault read() {
		GamePlayingDefault result = null;
		
		synchronized (this.lock) {
			result = (this.game != null) ? this.game.clone() : null;
			this.hasChanged = false;
		}
		
		return result;
	}
}
