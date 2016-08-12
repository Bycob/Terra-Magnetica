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
import org.terramagnetica.game.lvldefault.InterruptionPhasesLvlDefault;
import org.terramagnetica.game.lvldefault.PlayerDefault;

import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Vec2f;

public class InterruptionEnd extends InterruptionPhasesLvlDefault implements Level2 {
	
	private static final float CREATURE_FIRST_MOVE = 2.5f;
	private static final float CREATURE_SECOND_MOVE = 9;
	private static final float CREATURE_SPEED = 3;
	
	public InterruptionEnd(final GamePlayingDefault game) {
		final Rock rock = game.findEntity(Rock.class);
		final TheCreature creature = game.findEntity(TheCreature.class);
		final PlayerDefault player = game.getPlayer();
		
		if (rock != null && creature != null) {
			//Variables
			final Vec2f creatureStartingPoint = creature.getCoordonnéesf();
			final Vec2f creaturePoint1 = creatureStartingPoint.clone(); creaturePoint1.translate(CREATURE_FIRST_MOVE, 0);
			final Vec2f creaturePoint2 = creaturePoint1.clone(); creaturePoint2.translate(CREATURE_SECOND_MOVE, 0);
			
			final Vec2f playerStartingPoint = player.getCoordonnéesf();
			final Vec2f playerEndPoint = playerStartingPoint.clone(); playerEndPoint.translate(5, 0);
			
			
			//Scrolling sur le rocher
			addPhase(new PhaseScrolling(rock.getCoordonnéesCase(), game));
			
			//L'entité passe le rocher et appuie sur un machin
			addPhase(new Phase() {
				
				@Override
				public void update(long ms) {
					creature.setCoordonnéesf(
							MathUtil.interpolateLinear(creatureStartingPoint, creaturePoint1, (double) ms / duration()).asFloat());
				}
				
				@Override
				public long duration() {
					return (long) (1000 * CREATURE_FIRST_MOVE / CREATURE_SPEED);
				}
			});
			addPhase(new PhasePause(250));
			
			//Le rocher tombe
			addPhase(new Phase() {

				@Override
				public void update(long ms) {
					rock.setElevation(- (float) ms / duration() * 1f);
				}
				
				@Override
				public long duration() {
					return 500;
				}
			});
			addPhase(new PhasePause(250));
			
			//La créature s'en va loin !!!!
			addPhase(new Phase() {

				@Override
				public void update(long ms) {
					creature.setCoordonnéesf(
							MathUtil.interpolateLinear(creaturePoint1, creaturePoint2, (double) ms / duration()).asFloat());
				}
				
				@Override
				public long duration() {
					return (long) (1000 * CREATURE_SECOND_MOVE / (CREATURE_SPEED * 2));
				}
			});
			
			//Le joueur la suit
			addPhase(scrollingToPlayer(game));
			addPhase(new Phase() {

				@Override
				public void update(long ms) {
					player.setCoordonnéesf(
							MathUtil.interpolateLinear(playerStartingPoint, playerEndPoint, (double) ms / duration()).asFloat());
				}
				
				@Override
				public long duration() {
					return 1000;
				}
			});
		}
		
		//Fin du niveau
		addPhase(new PhaseEnd(game));
	}
}
