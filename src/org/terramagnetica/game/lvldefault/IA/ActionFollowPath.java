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

package org.terramagnetica.game.lvldefault.IA;

import org.terramagnetica.game.lvldefault.EntityMoving;
import org.terramagnetica.game.lvldefault.PlayerDefault;

import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.path.Path;

/** Cette action permet à une entité de suivre un chemin. Il faut
 * pour cela que l'intelligence artificielle de l'entité soit de
 * type {@link AIMovable}.
 * <p>Le chemin suivi est celui retourné par la méthode
 * {@link AIMovable#getPath()}. Si cette méthode renvoie {@code null}
 * alors l'entité s'arrête. */
public class ActionFollowPath extends Action {
	
	public static final float DEFAULT_VELOCITY = PlayerDefault.DEFAULT_VELOCITY;
	
	private float velocity;
	
	public ActionFollowPath() {
		this(DEFAULT_VELOCITY);
	}
	
	public ActionFollowPath(float velocity) {
		this.setVelocity(velocity);
	}
	
	public void setVelocity(float velocity) {
		if (velocity < 0) throw new IllegalArgumentException("velocity must be positive");
		this.velocity = velocity;
	}
	
	public float getVelocity() {
		return this.velocity;
	}
	
	@Override
	public void execute(AIBase ai) {
		AIMovable aiMove = ai.cast(AIMovable.class);
		EntityMoving ent = aiMove.getEntity();
		Path entPath = aiMove.getPath();
		
		if (entPath != null && entPath.hasNext()) {
			//Obtention des informations
			Vec2i nextCase = entPath.getNext().getPoint().asInteger();
			
			//Si on est déjà sur la bonne case, on avance et on recommence.
			if (ent.getCasePosition().equals(nextCase)) {
				entPath.next();
				execute(ai);
			}
			else {
				//Déplacement
				Locomotor loco = new Locomotor(ent);
				loco.moveTo(aiMove.getGame(), nextCase, this.velocity);
			}
		}
		else {
			aiMove.setPath(null);
			ent.setVelocity(0);
		}
	}
	
	@Override
	public ActionFollowPath clone() {
		ActionFollowPath clone = (ActionFollowPath) super.clone();
		
		return clone;
	}
}
