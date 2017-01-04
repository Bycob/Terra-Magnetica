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

import java.io.Serializable;

import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.MathUtil;

public abstract class EntityMoving extends Entity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private float impulseX, impulseY;
	
	protected EntityMoving(){
		super();
		init();
	}
	
	protected EntityMoving(int x, int y){
		super(x,y);
		init();
	}
	
	private void init()  {
		this.hitbox.setStatic(false);
		this.hitbox.setMaxSpeed(15f);
		this.hitbox.setBounce(0.95f);
	}
	
	public float getMovementX() {
		return this.hitbox.getSpeedX();
	}

	public void setMovementX(float xMoving) {
		this.setMovement(xMoving, this.getMovementY());
	}

	public float getMovementY() {
		return this.hitbox.getSpeedY();
	}

	public void setMovementY(float yMoving) {
		this.setMovement(this.getMovementX(), yMoving);
	}
	
	/**
	 * D�finit le mouvement de l'entit�, � la fois en x et en y. Si il en
	 * r�sulte une trop grande vitesse de l'entit�, celle-ci sera d�finie
	 * apr�s la modification comme �tant :
	 * <blockquote>{@link #getMaxVelocity()}</blockquote>
	 * <p>Les mouvements sont en cases par seconde.
	 * @param xMoving - le nouveau mouvement en x de l'entit�, en cases par
	 * secondes
	 * @param yMoving - le nouveau mouvement en y de l'entit�, en cases par
	 * secondes
	 */
	public void setMovement(float xMoving, float yMoving) {
		this.hitbox.setLinearSpeed(xMoving, yMoving);
		
		if (this.getVelocity() > this.getMaxVelocity()) {
			setVelocity(this.getMaxVelocity());
		}
	}

	/** Donne la vitesse de cette entit� en cases par secondes. */
	public double getVelocity() {
		double sx = getMovementX();
		double sy = getMovementY();
		return Math.sqrt(sx * sx + sy * sy);
	}
	
	/** Donne la vitesse relative de cette entit� par
	 * rapport � celle pass�e en param�tre. */
	public double getRelativeSpeed(EntityMoving e) {
		return this.getVelocity() + e.getVelocityToDirection(e.getDirection(this));
	}
	
	/** Donne la norme du vecteur issu de la projection du vecteur vitesse
	 * de cette entit� dans la direction pass�e en param�tres. */
	public double getVelocityToDirection(float dir) {
		double angle = MathUtil.angleMainValue(dir - this.getDirection());
		return Math.cos(angle) * getVelocity();
	}
	
	/**
	 * D�finit la vitesse de l'entit�. Si elle ne bouge pas, elle ira
	 * vers l'EST.
	 * @param vitesse - la nouvelle vitesse de l'entit�, en cases par
	 * secondes.
	 */
	public void setVelocity(float vitesse) {
		if (Math.abs(vitesse) > this.getMaxVelocity()) {
			vitesse = this.getMaxVelocity() * Math.signum(vitesse);
		}
		
		if (this.getMovementX() == 0 && this.getMovementY() == 0) {
			this.hitbox.setLinearSpeed(vitesse, 0);
		}
		else {
			float ratio = vitesse / (float) getVelocity();
			
			this.hitbox.scaleLinearSpeed(ratio, ratio);
		}
	}
	
	public float getDirection() {
		double v = getVelocity();
		if (v == 0) {
			return 0;
		}
		
		return (float) MathUtil.atan2(
				MathUtil.valueIn(this.getMovementX() / v, -1, 1),
				MathUtil.valueIn(- this.getMovementY() / v, -1, 1));
	}
	
	public void setDirection(float direction) {
		if (this.getMovementX() != 0 || this.getMovementY() != 0) {
			double vitesse = getVelocity();
			
			this.setMovement((float) (Math.cos(direction) * vitesse), (float) (- Math.sin(direction) * vitesse));
		}
	}
	
	/**
	 * Pousse cette entit� avec la force indiqu�e, dans la
	 * direction indiqu�e.
	 * <p>Cette m�thode peut n'avoir aucun effet, cela d�pend de
	 * l'entit�.
	 * @param force - La force de pouss�e, en cases/s
	 * @param direction - La direction de la pouss�e, en radians
	 * @param game - Le moteur de jeu qui g�re l'entit�
	 * @param pusher - L'entit� qui pousse celle-ci.
	 */
	public void push(float force, float direction, GamePlayingDefault game, Entity pusher) {
		this.impulseX += force * Math.cos(direction);
		this.impulseY += force * - Math.sin(direction);
	}
	
	/**
	 * Donne la vitesse maximum de l'entit�, en cases par secondes.
	 * Il est impossible de faire aller une entit� <i>plus vite</i>
	 * que sa vitesse maximum.
	 * @return
	 */
	public float getMaxVelocity() {
		return this.hitbox.getMaxSpeed();
	}
	
	public void enableMoving(boolean canMove) {
		this.hitbox.setStatic(!canMove);
	}
	
	@Override
	public EntityMoving clone() {
		EntityMoving result = (EntityMoving) super.clone();
		
		return result;
	}
	
	@Override
	public void updatePhysic(long dT, GamePlayingDefault game) {
		if (this.impulseX != 0 || this.impulseY != 0) {
			this.hitbox.impulse(this.impulseX, this.impulseY);
		}
		
		this.impulseX = 0;
		this.impulseY = 0;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>Dans le cas d'une entit� mobile, la mise � jour par d�faut effectue
	 * les actions suivantes, dans l'ordre :
	 * <ul><li>v�rification des collisions
	 * <li>r�action sur le mouvement
	 * </ul>
	 */
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		super.updateLogic(dT, game);
	}

	@Override
	public void onEntityCollision(long delta, GamePlayingDefault game, Entity collided) {}
	@Override
	public void onLandscapeCollision(long dT, GamePlayingDefault game) {}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		out.writeFloatField(this.getMovementX(), 100);
		out.writeFloatField(this.getMovementY(), 101);
	}
	
	@Override
	public Entity decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		this.hitbox.setLinearSpeed(in.readFloatField(100), in.readFloatField(101));
		
		return this;
	}
}
