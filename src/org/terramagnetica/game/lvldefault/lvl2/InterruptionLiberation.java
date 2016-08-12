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

import org.terramagnetica.game.lvldefault.DialogInGame;
import org.terramagnetica.game.lvldefault.DialogInGame.Statement;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.InterruptionPhasesLvlDefault;

/** NIVEAU 2 salle 2 : Cette interruption joue l'animation de 
 * libération de la créature dans la salle 2 du niveau 2. */
public class InterruptionLiberation extends InterruptionPhasesLvlDefault {
	
	public TheCreature creature;
	
	public InterruptionLiberation(GamePlayingDefault game, TheCreature creature) {
		this.creature = creature;
		
		DialogInGame dialog = new DialogInGame();
		dialog.addStatement(new Statement("Vous avez pitié de cette pauvre créature et la libérez..."));
		dialog.addStatement(new Statement("Il semble qu'elle vous invite à la suivre !"));
		
		addPhase(new PhasePause(500));
		addPhase(new PhaseDialog(game, dialog));
		addPhase(new PhasePause(500));
		
		addPhase(new Phase() {
			@Override
			public void onStart() {
				InterruptionLiberation.this.creature.changeAI();
			}
			
			@Override public void update(long ms) {}
		});
	}
}
