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

import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.Mark;
import org.terramagnetica.game.lvldefault.PlayerDefault;
import org.terramagnetica.game.lvldefault.IA.Locomotor;
import org.terramagnetica.game.lvldefault.IA.SpaceGraphLevelDefault;
import org.terramagnetica.game.lvldefault.lvl2.Level2.CreatureAI;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.path.Path;
import net.bynaryscode.util.path.PathFinder;

public class CapturedCreatureAI extends CreatureAI implements Level2 {
	
	/** La distance à partir de laquelle on attend le joueur. */
	private static final float DISTANCE_WAITING_PLAYER = 7;
	/** Si on attendait le joueur et qu'il arrive à cette distance de nous, alors
	 * on peut repartir de l'avant.*/
	private static final float DISTANCE_GO_FORWARD = 4;
	/** La vitesse de la créature en phase 2. */
	private static final float SPEED_2 = PlayerDefault.DEFAULT_VELOCITY + 1;
	
	
	
	private boolean waitingPlayer = false;
	
	private Path followedPath = null;
	private Locomotor loco;
	
	public CapturedCreatureAI(TheCreature creature) {
		super(creature);
		this.loco = new Locomotor(creature);
	}
	
	@Override
	public void update() {
		GamePlayingDefault game = this.getGame();
		TheCreature myCreature = this.getEntity();
		float playerDistance = (float) game.getPlayer().getDistancef(myCreature);
		
		if (this.followedPath == null) {
			Vec2i exitCase = ROOM2_EXIT_CASE;
			
			//Pour le level designer -> il peut changer la case en mettant une marque du bon ID
			Mark mark = game.findMark(ROOM2_EXIT_CASE_MARK_ID);
			if (mark != null) {
				exitCase = mark.getCoordonnéesCase();
			}
			
			findPath(exitCase.x, exitCase.y, game);
		}
		
		if (!this.waitingPlayer) {
			
			if (this.followedPath != null) {
				if (this.followedPath.hasNext()) {
					//On suit le chemin.
					Vec2i nextCase = this.followedPath.getNext().getPoint().asInteger();
					//Incrémentation
					if (myCreature.getCoordonnéesCase().equals(nextCase)) {
						this.followedPath.next();
						
						if (this.followedPath.hasNext()) nextCase = this.followedPath.getNext().getPoint().asInteger();
					}
					
					this.loco.moveTo(game, nextCase, SPEED_2);
				}
				else {
					//On attend le joueur une dernière fois pour faire l'animation de fin de niveau.
					this.waitingPlayer = true;
				}
			}
			
			if (playerDistance >= DISTANCE_WAITING_PLAYER) {
				this.waitingPlayer = true;
			}
		}
		else {
			myCreature.setMovement(0, 0);
			
			if (playerDistance <= DISTANCE_GO_FORWARD) {
				
				if (this.followedPath != null) {
					if (this.followedPath.hasNext()) {
						//Alors on est pas arrivé à la fin, on continue d'avancer
						this.waitingPlayer = false;
					}
					else {
						//On est arrivé au bout !
						game.interruptGame(new InterruptionEnd(game));
					}
				}
			}
		}
	}
	
	private void findPath(int caseX, int caseY, GamePlayingDefault game) {
		SpaceGraphLevelDefault graph = new SpaceGraphLevelDefault(game);
		graph.setIgnoreCaseEntity(true);
		
		PathFinder pathFinder = new PathFinder(graph);
		this.followedPath = pathFinder.findPath(graph.getCase(this.getEntity().getCoordonnéesCase()), graph.getCase(caseX, caseY));
	}

	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		
	}

	@Override
	public Codable decode(BufferedObjectInputStream in) throws GameIOException {
		
		return this;
	}
}
