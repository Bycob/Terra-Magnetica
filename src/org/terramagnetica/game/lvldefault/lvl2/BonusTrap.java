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

import java.awt.Image;

import org.terramagnetica.game.GameInputBuffer.InputKey;
import org.terramagnetica.game.lvldefault.Bonus;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.PlayerDefault;
import org.terramagnetica.game.lvldefault.rendering.RenderObject;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityNothing;

import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.maths.geometric.DimensionsInt;

/** Cet objet attaché au joueur contient le nombre de piège que le joueur
 * a ramassé et a à sa disposition. Il hérite de la classe {@link Bonus},
 * mais n'est pas prévu pour être utilisé comme une entité dans le jeu.
 * <p>Le joueur ne possède qu'un {@link BonusTrap} dans son inventaire, c'est
 * celui-ci qui peut contenir plusieurs pièges. */
public class BonusTrap extends Bonus {
	
	public static final int MAX_TRAP_COUNT = 4;
	
	private static final long serialVersionUID = 1L;
	
	private int trapCount;
	
	public static final long INPUT_TIME = 250;
	/** La date de la dernière entrée, en heure système */
	long lastInput = System.currentTimeMillis() - INPUT_TIME;
	
	/** Crée un {@link BonusTrap} ne contenant aucun piège. */
	public BonusTrap() {
		this.trapCount = 0;
	}
	
	/** Ajoute dans la mesure du possible un piège à l'inventaire
	 * du joueur. Retourne {@code false} si le piège n'a pas été posé. */
	public boolean addTrap() {
		if (this.trapCount < MAX_TRAP_COUNT) {
			this.trapCount++;
			return true;
		}
		return false;
	}
	
	/** Retire un piège de l'inventaire du joueur. Si le joueur n'a
	 * aucun piège, retourne {@code false}. */
	public boolean removeTrap() {
		if (this.trapCount > 0) {
			this.trapCount--;
			return true;
		}
		return false;
	}
	
	public int getTrapCount() {
		return this.trapCount;
	}
	
	@Override
	public void updateBonus(long dT, GamePlayingDefault game) {
		long systemTime = System.currentTimeMillis();
		
		if (game.getInput().isKeyPressed(InputKey.KEY_TALK) &&
				systemTime - this.lastInput > INPUT_TIME) {
			
			PlayerDefault player = this.myPlayer;
			if (player != null) {
				
				Vec2i playerCase = player.getCasePosition();
				if (game.getCaseEntityAt(playerCase.x, playerCase.y) == null
						&& game.getLandscapeAt(playerCase.x, playerCase.y).isEnabled()
						&& this.removeTrap()) {
					
					this.lastInput = systemTime;
					
					Trap trap = new Trap(playerCase);
					game.addEntity(trap);
				}
			}
		}
	}
	
	
	@Override public Image getImage() {return null;}
	@Override protected RenderObject createRender() {return new RenderEntityNothing();}
	@Override public DimensionsInt getDimensions() {return new DimensionsInt();}
}
