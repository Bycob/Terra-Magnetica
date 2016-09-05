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

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityDefault;
import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxPolygon;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Boussole;
import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec2i;

public class MagneticWave extends EntityMoving {
	
	private static final long serialVersionUID = 1L;
	/** Largeur d'une vague */
	private static final float WAVE_WIDTH = 1;
	
	// Propriétés de départ
	private float distance;
	
	//Infos de parcours
	private float coveredDist = 0;
	private boolean isAlive = true;
	
	/**
	 * @deprecated Utilisé dynamiquement dans la lecture de fichier.
	 */
	public MagneticWave() {
		
	}
	
	protected MagneticWave(float speed, float distance, float direction) {
		this();
		this.setVelocity(speed);
		this.setDirection(direction);
		this.distance = distance;
	}
	
	@Override
	public Image getImage() {
		return null;
	}
	
	@Override
	protected RenderEntityDefault createRender() {
		RenderEntityDefault render = new RenderEntityDefault(GameRessources.ID_WAVES).setOnGround(true);
		render.setRotation((int) Math.toDegrees(Boussole.getPointCardinalPourAngle(this.getDirection() + Math.PI /2).getOrientation()));
		return render;
	}

	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(CASE, CASE);
	}
	
	public boolean isAlive() {
		return this.isAlive;
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		super.updateLogic(dT, game);
		
		this.updateLastHitbox();
		this.hitbox.completeMove(dT);
		
		Vec2f co = this.lastHitbox.getPosition();
		Vec2f ca = this.getCoordonnéesf();
		
		this.coveredDist += MathUtil.getDistance(co, ca);
		
		if (this.coveredDist >= this.distance) {
			this.isAlive = false;
		}
		
		//collision
		for (Entity ent : game.getEntities()) {
			if (ent instanceof EntityMoving && this.hasCollision(ent)) {
				((EntityMoving) ent).push((float) this.getVelocity(), this.getDirection(), game, this);
			}
		}
	}
	
	@Override
	public Hitbox createHitbox() {
		if (Math.abs(this.getMovementX()) > Math.abs(this.getMovementY())) {
			return new HitboxPolygon(RectangleDouble.createRectangleFromCenter(0, 0, DEMI_CASE_F, 1));
		}
		else {
			return new HitboxPolygon(RectangleDouble.createRectangleFromCenter(0, 0, 1, DEMI_CASE_F));
		}
	}
	
	@Override
	public boolean hasCollision(Entity ent) {
		boolean caseParam = false;
		
		Vec2i entC = ent.getCoordonnéesCase();
		Vec2i myC = getCoordonnéesCase();
		
		Boussole thisDir = Boussole.getPointCardinalPourAngle(getDirection());
		
		switch (thisDir) {
		case EST :
		case OUEST :
			caseParam = entC.y == myC.y;
			break;
		case NORD :
		case SUD :
			caseParam = entC.x == myC.x;
			break;
		}
		
		return caseParam && super.hasCollision(ent);
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		
		out.writeFloatField(this.distance, 201);
		out.writeFloatField(this.coveredDist, 202);
	}
	
	@Override
	public MagneticWave decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		
		this.distance = in.readFloatField(201);
		this.coveredDist = in.readFloatField(202);
		
		return this;
	}
}
