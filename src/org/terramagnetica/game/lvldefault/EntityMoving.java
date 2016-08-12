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

package org.terramagnetica.game.lvldefault;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.terramagnetica.game.physic.Hitbox;
import org.terramagnetica.game.physic.HitboxPolygon;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.MathUtil;

public abstract class EntityMoving extends Entity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected transient boolean canMove = true;
	protected transient boolean shouldDoCollision = true;
	
	protected transient boolean[] arrayCollisionPermission;
	
	/** La liste des entit�s dont la collision avec celle-ci a d�j� �t�
	 * test� pour ce tour de mise � jour. */
	protected transient List<EntityMoving> testList = new ArrayList<EntityMoving>();
	
	public static final int LANDSCAPE_COLLISION = 0;
	public static final int ENTITY_COLLISION = 1;
	
	protected EntityMoving(){
		super();
		init();
	}
	
	protected EntityMoving(int x, int y){
		super(x,y);
		init();
	}
	
	private void init()  {
		this.arrayCollisionPermission = new boolean[nbCollisionPermission()];
		Arrays.fill(this.arrayCollisionPermission, true);
	}
	
	@Override
	public void applyHitbox(Hitbox hb) {
		super.applyHitbox(hb);
		
		this.setMovement(hb.getSpeedX(), hb.getSpeedY());
		this.hitbox.setRotationSpeed(hb.getRotationSpeed());
	}
	
	@Override
	public void recreateHitbox() {
		super.recreateHitbox();
		this.hitbox.setStatic(false);
		this.lastHitbox.setStatic(false);
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
		
		return (float) MathUtil.angle(
				MathUtil.valueInRange_d(this.getMovementX() / v, -1, 1),
				MathUtil.valueInRange_d(- this.getMovementY() / v, -1, 1));
	}
	
	public void setDirection(float direction) {
		if (this.getMovementX() != 0 || this.getMovementY() != 0) {
			double vitesse = getVelocity();
			
			this.setMovement((float) (Math.cos(direction) * vitesse),(float) (- Math.sin(direction) * vitesse));
		}
	}
	
	/**
	 * Pousse cette entit� avec la force indiqu�e, dans la
	 * direction indiqu�e.&
	 * <p>Cette m�thode peut n'avoir aucun effet, cela d�pend de
	 * l'entit�.
	 * @param force - La force de pouss�e, en cases/s
	 * @param direction - La direction de la pouss�e, en radians
	 * @param game - Le moteur de jeu qui g�re l'entit�
	 * @param pusher - L'entit� qui pousse celle-ci.
	 */
	public void push(float force, float direction, GamePlayingDefault game, Entity pusher) {}
	
	/**
	 * Donne la vitesse maximum de l'entit�, en cases par secondes.
	 * Il est impossible de faire aller une entit� <i>plus vite</i>
	 * que sa vitesse maximum.
	 * @return
	 */
	public float getMaxVelocity() {
		return 15f;
	}
	
	public void enableMoving(boolean canMove) {
		this.canMove = canMove;
	}
	
	public void enableCollision(boolean canCollides) {
		checkNullValues();
		Arrays.fill(this.arrayCollisionPermission, canCollides);
	}
	
	/** Permet d'autoriser les collision pour une classe sp�cifique
	 * d'objet. */
	public void permitCollision(boolean canCollides, int flag) {
		checkNullValues();
		this.arrayCollisionPermission[flag] = canCollides;
	}
	
	/** Indique si cette entit� est autoris�e � subir les collisions avec
	 * la classe d'objet indiqu�e en param�tres.
	 * @param flag
	 * @return */
	public boolean havePermissionForCollision(int flag) {
		checkNullValues();
		return this.arrayCollisionPermission[flag];
	}
	
	@Override
	public boolean hasCollision(Entity other) {
		if (!super.hasCollision(other)) return false;
		if (!this.havePermissionForCollision(ENTITY_COLLISION)) return false;
		
		return true;
	}
	
	private void checkNullValues() {
		if (this.arrayCollisionPermission == null) {
			this.arrayCollisionPermission = new boolean[this.nbCollisionPermission()];
			Arrays.fill(this.arrayCollisionPermission, true);
		}
	}
	
	@Override
	public EntityMoving clone() {
		EntityMoving result = (EntityMoving) super.clone();
		
		return result;
	}
	
	protected int nbCollisionPermission() {
		return 2;
	}
	
	//TODO Mettre � jour le moteur physique afin que toutes les interactions physiques soient r�alis�es dans la m�thode #updatePhysic().
	/* Fct du moteur physique : 
	 * 1 - calcul de tous les pts de collision (doit �tre tr�s rapide)
	 * 2 - on s�lectionne les premiers points, on effectue les collisions.
	 * 3 - boucle �tape 1 avec les entit�s collisionn�es jusqu'� ce qu'il n'y ait plus de collision.
	 * 4 - completeMove pour tous.*/
	
	@Override
	public void updatePhysic(long dT, GamePlayingDefault game) {
		this.move(dT);
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
		checkNullValues();
		
		List<Entity> entityList = game.getEntities();
		
		/* gestion des collision avec le d�cor. */
		if (this.havePermissionForCollision(LANDSCAPE_COLLISION)) {
			if (this.hasLandscapeCollision(game)) {
				this.onLandscapeCollision(dT, game);
			}
		}
		
		/* gestion des collisions avec les entit�s. */
		if (this.havePermissionForCollision(ENTITY_COLLISION)) {
			
			//Collision avec les autres entit�s
			for (Entity e : entityList) {
				if (e != this && !this.testList.contains(e)) {
					if (e instanceof EntityMoving) {
						((EntityMoving) e).testList.add(this);
					}
					if (hasCollision(e)) {
						this.onEntityCollision(dT, game, e);
						e.onEntityCollision(dT, game, this);
					}
				}
			}
		}
		
		this.enableCollision(true);
		this.testList.clear();
		
		super.updateLogic(dT, game);
	}

	@Override
	public void onEntityCollision(long delta, GamePlayingDefault game, Entity collided) {
		bounceEntity(game, collided, delta);
	}
	
	@Override
	protected void onLandscapeCollision(long dT, GamePlayingDefault game) {
		bounceWall(game, dT);
	}
	
	protected final void move(long dT) {
		this.hitbox.translate(this.getMovementX() * dT / 1000, this.getMovementY() * dT / 1000);
	}
	
	/** Ce coefficient indique le pourcentage de vitesse que l'entit� conserve
	 * lorsqu'elle subit une collision avec une autre entit�. */
	private float coefSpeedKeptCollision = 0.95f;
	
	/**
	 * Rebondit sur un mur. On suppose qu'une collision a d�j� eu
	 * lieu avec la case de d�cor sur laquelle est situ�e l'entit�.
	 * Ainsi, l'entit� rebondira toujours sur la case sur laquelle
	 * elle est situ�e ; la collision doit donc �tre confirm�e avant
	 * l'appel � cette m�thode.
	 * @param game
	 */
	public void bounceWall(GamePlayingDefault game, long time) {
		
		bounceWithTries(game, time, null);
		
		//Symbolise les petites asp�rit�s du mur ^^
		Random rand = new Random();
		this.hitbox.scaleLinearSpeed((float) MathUtil.nextGaussian(rand, this.coefSpeedKeptCollision, 0.005),
				(float) MathUtil.nextGaussian(rand, this.coefSpeedKeptCollision, 0.005));
	}
	
	public void bounceEntity(GamePlayingDefault game, Entity ent, long time) {
		
		if (ent instanceof VirtualWall) {
			if (!canPassVirtualWall()) bounceWall(game, time);
			return;
		}
		
		Entity lastHitboxEnt = this.getLastHitboxAsEntity();
		
		//Calcul des nouvelles vitesses et direction : d�pend de la forme de l'objet collisionn�
		if (ent.getHitBoxf() instanceof HitboxPolygon) {
			bounceWithTries(game, time, lastHitboxEnt);
		}
		else {
			float otherDir = getDirection(lastHitboxEnt);
			float thisMoveDir = getDirection();
			
			float newMoveDir = (float) MathUtil.angleMainValue(2 * otherDir - thisMoveDir);
			float newSpeed = (float) this.getVelocity();
			
			if (ent instanceof EntityMoving) {
				EntityMoving entMov = (EntityMoving) lastHitboxEnt;
				newSpeed += - Math.abs(this.getVelocityToDirection(otherDir)) - entMov.getVelocityToDirection(otherDir);
			}
			
			//Application du rebond avec les nouvelles vitesses et directions calcul�es
			this.applyHitbox(this.lastHitbox);
			
			this.setDirection(newMoveDir);
			this.setVelocity(newSpeed);
			
			this.move(time);
			
			//Pour pr�venir les bugs d'interp�n�tration : la distance ne doit pas �tre r�duite apr�s le rebond.
			if (MathUtil.getDistance(this.lastHitbox.getPosition(), ent.getCoordonn�esf()) > getDistancef(ent)) {
				this.hitbox.setPosition(this.lastHitbox.getPositionX(), this.lastHitbox.getPositionY());
				this.hitbox.scaleLinearSpeed(-1, -1);
				this.move(time);
			}
			
		}
		
		this.hitbox.scaleLinearSpeed(this.coefSpeedKeptCollision, this.coefSpeedKeptCollision);
	}
	
	/** Lors d'un rebond sur un mur ou une entit� semblable � un mur, cette
	 *  m�thode calcule le rebond, effectuant plusieurs essais pour d�terminer
	 * la direction dans laquelle va rebondir l'entit�.
	 * @param condition - la condition qui d�termine si cette entit� est en collision.
	 * Si ce param�tre est une entit�, on teste la collision avec celle-ci, sinon
	 * on teste la collision avec le d�cor. */
	private void bounceWithTries(GamePlayingDefault game, long time, Entity conditionEnt) {

		int[][] array = {{1, 1}, {-1, 1}, {1, -1}, {-1, -1}};
		
		applyHitbox(this.lastHitbox);
		
		for (int i = 0 ; i < 4 ; i++) {
			this.hitbox.scaleLinearSpeed(array[i][0], array[i][1]);
			this.move(time);
			
			boolean condition = conditionEnt == null ? this.hasLandscapeCollision(game) : this.hasCollision(conditionEnt);
			
			if (condition) {
				applyHitbox(this.lastHitbox);
			}
			else {
				break;
			}
		}
	}
	
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
