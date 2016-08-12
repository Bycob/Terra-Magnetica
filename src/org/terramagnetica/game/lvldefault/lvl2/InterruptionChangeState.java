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

package org.terramagnetica.game.lvldefault.lvl2;

import java.util.ArrayList;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.InterruptionPhasesLvlDefault;
import org.terramagnetica.opengl.engine.AnimatedTexture;
import org.terramagnetica.opengl.miscellaneous.AnimationManager;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;

/** NIVEAU 2 salle 1: Cette interruption se produit lorsque
 * le joueur désactive un mur. */
public class InterruptionChangeState extends InterruptionPhasesLvlDefault {
	
	private BarrierHandle handle;
	private boolean state;
	
	public InterruptionChangeState(BarrierHandle handle, boolean state, GamePlayingDefault game) {
		this.handle = handle;
		this.state = state;
		
		//changement d'état
		addPhase(new Phase() {
			
			@Override
			public void update(long ms) {
				InterruptionChangeState.this.handle.setState(InterruptionChangeState.this.state);
			}
			
			@Override
			public long duration() {
				return 0;
			}
		});
		
		//scrolling vers le portail
		ArrayList<Portal> portalList = extractPortals(game, this.handle.getColor());
		for (Portal p : portalList) {
			addPhase(new PhaseScrolling(p.getCoordonnéesf(), game));
			
			addPhase(new PhasePause(500));
			
			final Portal param = p;
			//La phase d'animation du portail.
			addPhase(new Phase() {
				
				private AnimationManager animater;
				private int duration = 0;
				
				@Override
				public void onStart() {
					AnimatedTexture portalTex = TexturesLoader.getAnimatedTexture(GameRessources.PATH_ANIM002_OPENING_PORTAL);
					
					this.animater = param.setAnimatedRender(portalTex);
					this.animater.start();
					
					//Si on enlève pas une image l'animation boucle...
					this.duration = portalTex.getDuration() - 1000 / portalTex.getFPS();
				}
				
				@Override
				public void onEnd() {
					param.setRender(TexturesLoader.getQuad(GameRessources.PATH_LVL2_TEXTURES + GameRessources.TEX_PORTAL_ON));
				}
				
				@Override
				public void update(long ms) {
					
				}

				@Override
				public long duration() {
					return this.duration;
				}
			});
			
			
			addPhase(new PhasePause(500));
		}
		
		//scrolling vers le joueur
		addPhase(scrollingToPlayer(game));
	}
	
	public ArrayList<Portal> extractPortals(GamePlayingDefault game, Color4f color) {
		ArrayList<Portal> portalList = new ArrayList<Portal>();
		
		for (Entity e : game.getEntities()) {
			if (e instanceof Portal) {
				Portal p = (Portal) e;
				if (p.getColor().equals(color)) {
					portalList.add(p);
				}
			}
		}
		
		return portalList;
	}
}
