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

import java.awt.Image;
import java.util.ArrayList;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

public class BonusArmor extends Bonus {
	
	private static final long serialVersionUID = 1L;
	
	private float armorCoef = 1;
	
	public BonusArmor(float armorCoef) {
		this.setArmorCoef(armorCoef);
	}
	
	public float getArmorCoef() {
		return armorCoef;
	}
	
	public void setArmorCoef(float armorCoef) {
		this.armorCoef = armorCoef;
	}
	
	@Override
	public Image getImage() {
		return null;
	}
	
	@Override
	protected void createRender() {
		
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(DEMI_CASE, DEMI_CASE);
	}
	
	/** Lorsque le joueur prend un coup et qu'il possède une armure,
	 * les dégats sont réduits d'un certain montant. Si l'armure peut
	 * contenir tous les dégats, elle est endommagée et le joueur ne 
	 * prend aucun coup. Dans le cas contraire, l'armure est détruite
	 * et le joueur prend un montant réduit de dégats. */
	@Override
	public float takeDamages(float dmg) {
		if (this.myPlayer == null) return dmg;
		
		dmg = dmg - armorCoef;
		if (dmg >= 0) {
			this.myPlayer.removeBonus(this);
		}
		else {
			this.armorCoef = - dmg;
			dmg = 0;
		}
		
		return dmg;
	}
	
	@Override
	public void onPlayerWalkingOn(GamePlayingDefault game) {
		ArrayList<Bonus> playerBonus = game.getPlayer().getBonusList();
		
		boolean found = false;
		
		for (Bonus bonus : playerBonus) {
			if (bonus instanceof BonusArmor) {
				found = true;
				BonusArmor armorBonus = (BonusArmor) bonus;
				armorBonus.armorCoef += this.armorCoef;
			}
		}
		
		if (!found) {
			game.getPlayer().addBonus(this);
		}
		
		game.removeEntity(this);
	}
}
