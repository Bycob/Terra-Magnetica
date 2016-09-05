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

import java.util.ArrayList;

import org.terramagnetica.game.GameEngine;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

public class GameEventDispatcher extends GameAspect {
	
	private ArrayList<GameEventInfos> evtList = new ArrayList<GameEventInfos>();
	private GamePlayingDefault game;
	
	public GameEventDispatcher(GamePlayingDefault game) {
		setGame(game);
	}
	
	@Override
	public void init() {
		this.evtList.clear();
	}
	
	@Override
	public void update(long dT) {
		
		ArrayList<GameEventInfos> triggered = new ArrayList<GameEventInfos>();
		
		for (GameEventInfos geI : this.evtList) {
			if (geI.evt.getDelay() <= this.game.getTime() - geI.evtTriggeredTime) {
				triggered.add(geI);
			}
		}
		
		//Les évènements incompatibles s'annulent.
		for (int i = 0 ; i < triggered.size() ; i++) {
			
			GameEventInfos evtInfo = triggered.get(i);
			for (int j = triggered.size() - 1 ; j > i ; j--) {
				
				GameEventInfos evtInfo2 = triggered.get(j);
				//Si deux évènements prétendent s'annuler tous les deux, le dernier est prioritaire.
				if (evtInfo2.evt.overrides(evtInfo.evt)) {
					this.evtList.remove(i);
					triggered.remove(i);
					i--;//car décalage des items de la liste vers la gauche.
				}
				else if (evtInfo.evt.overrides(evtInfo2.evt)) {
					this.evtList.remove(j);
					triggered.remove(j);
				}
			}
		}
		
		//déclenchement des évènement.
		for (GameEventInfos geI : triggered) {
			if (this.game != null) geI.evt.trigger(this.game);
			this.evtList.remove(geI);
		}
	}
	
	@Override
	public void setGame(GameEngine game) {
		this.game = (game instanceof GamePlayingDefault) ? (GamePlayingDefault) game : null;
		init();
	}
	
	public void addEvent(GameEvent evt) {
		if (evt == null) throw new NullPointerException();
		if (this.game == null) return;
		this.evtList.add(new GameEventInfos(evt, this.game.getTime()));
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		
	}
	
	@Override
	public GameEventDispatcher decode(BufferedObjectInputStream in) throws GameIOException {
		return this;
	}
	
	@Override
	public boolean shouldSave() {
		return false;
	}
}

class GameEventInfos {
	GameEvent evt;
	long evtTriggeredTime;
	
	public GameEventInfos() {
		
	}
	
	public GameEventInfos(GameEvent evt, long evtTriggeredTime) {
		this.evt = evt;
		this.evtTriggeredTime = evtTriggeredTime;
	}
}
