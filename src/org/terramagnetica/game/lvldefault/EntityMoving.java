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
	
	/** La liste des entités dont la collision avec celle-ci a déjà été
	 * testé pour ce tour de mise à jour. */
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
	 * Définit le mouvement de l'entité, à la fois en x et en y. Si il en
	 * résulte une trop grande vitesse de l'entité, celle-ci sera définie
	 * après la modification comme étant :
	 * <blockquote>{@link #getMaxVelocity()}</blockquote>
	 * <p>Les mouvements sont en cases par seconde.
	 * @param xMoving - le nouveau mouvement en x de l'entité, en cases par
	 * secondes
	 * @param yMoving - le nouveau mouvement en y de l'entité, en cases par
	 * secondes
	 */
	public void setMovement(float xMoving, float yMoving) {
		this.hitbox.setLinearSpeed(xMoving, yMoving);
		
		if (this.getVelocity() > this.getMaxVelocity()) {
			setVelocity(this.getMaxVelocity());
		}
	}

	/** Donne la vitesse de cette entité en cases par secondes. */
	public double getVelocity() {
		double sx = getMovementX();
		double sy = getMovementY();
		return Math.sqrt(sx * sx + sy * sy);
	}
	
	/** Donne la vitesse relative de cette entité par
	 * rapport à celle passée en paramètre. */
	public double getRelativeSpeed(EntityMoving e) {
		return this.getVelocity() + e.getVelocityToDirection(e.getDirection(this));
	}
	
	/** Donne la norme du vecteur issu de la projection du vecteur vitesse
	 * de cette entité dans la direction passée en paramètres. */
	public double getVelocityToDirection(float dir) {
		double angle = MathUtil.angleMainValue(dir - this.getDirection());
		return Math.cos(angle) * getVelocity();
	}
	
	/**
	 * Définit la vitesse de l'entité. Si elle ne bouge pas, elle ira
	 * vers l'EST.
	 * @param vitesse - la nouvelle vitesse de l'entité, en cases par
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
	 * Pousse cette entité avec la force indiquée, dans la
	 * direction indiquée.&
	 * <p>Cette méthode peut n'avoir aucun effet, cela dépend de
	 * l'entité.
	 * @param force - La force de poussée, en cases/s
	 * @param direction - La direction de la poussée, en radians
	 * @param game - Le moteur de jeu qui gère l'entité
	 * @param pusher - L'entité qui pousse celle-ci.
	 */
	public void push(float force, float direction, GamePlayingDefault game, Entity pusher) {}
	
	/**
	 * Donne la vitesse maximum de l'entité, en cases par secondes.
	 * Il est impossible de faire aller une entité <i>plus vite</i>
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
	
	/** Permet d'autoriser les collision pour une classe spécifique
	 * d'objet. */
	public void permitCollision(boolean canCollides, int flag) {
		checkNullValues();
		this.arrayCollisionPermission[flag] = canCollides;
	}
	
	/** Indique si cette entité est autorisée à subir les collisions avec
	 * la classe d'objet indiquée en paramètres.
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
	
	//TODO Mettre à jour le moteur physique afin que toutes les interactions physiques soient réalisées dans la méthode #updatePhysic().
	/* Fct du moteur physique : 
	 * 1 - calcul de tous les pts de collision (doit être très rapide)
	 * 2 - on sélectionne les premiers points, on effectue les collisions.
	 * 3 - boucle étape 1 avec les entités collisionnées jusqu'à ce qu'il n'y ait plus de collision.
	 * 4 - completeMove pour tous.*/
	
	@Override
	public void updatePhysic(long dT, GamePlayingDefault game) {
		this.move(dT);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>Dans le cas d'une entité mobile, la mise à jour par défaut effectue
	 * les actions suivantes, dans l'ordre :
	 * <ul><li>vérification des collisions
	 * <li>réaction sur le mouvement
	 * </ul>
	 */
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		checkNullValues();
		
		List<Entity> entityList = game.getEntities();
		
		/* gestion des collision avec le décor. */
		if (this.havePermissionForCollision(LANDSCAPE_COLLISION)) {
			if (this.hasLandscapeCollision(game)) {
				this.onLandscapeCollision(dT, game);
			}
		}
		
		/* gestion des collisions avec les entités. */
		if (this.havePermissionForCollision(ENTITY_COLLISION)) {
			
			//Collision avec les autres entités
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
	
	/** Ce coefficient indique le pourcentage de vitesse que l'entité conserve
	 * lorsqu'elle subit une collision avec une autre entité. */
	private float coefSpeedKeptCollision = 0.95f;
	
	/**
	 * Rebondit sur un mur. On suppose qu'une collision a déjà eu
	 * lieu avec la case de décor sur laquelle est située l'entité.
	 * Ainsi, l'entité rebondira toujours sur la case sur laquelle
	 * elle est située ; la collision doit donc être confirmée avant
	 * l'appel à cette méthode.
	 * @param game
	 */
	public void bounceWall(GamePlayingDefault game, long time) {
		
		bounceWithTries(game, time, null);
		
		//Symbolise les petites aspérités du mur ^^
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
		
		//Calcul des nouvelles vitesses et direction : dépend de la forme de l'objet collisionné
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
			
			//Application du rebond avec les nouvelles vitesses et directions calculées
			this.applyHitbox(this.lastHitbox);
			
			this.setDirection(newMoveDir);
			this.setVelocity(newSpeed);
			
			this.move(time);
			
			//Pour prévenir les bugs d'interpénétration : la distance ne doit pas être réduite après le rebond.
			if (MathUtil.getDistance(this.lastHitbox.getPosition(), ent.getCoordonnéesf()) > getDistancef(ent)) {
				this.hitbox.setPosition(this.lastHitbox.getPositionX(), this.lastHitbox.getPositionY());
				this.hitbox.scaleLinearSpeed(-1, -1);
				this.move(time);
			}
			
		}
		
		this.hitbox.scaleLinearSpeed(this.coefSpeedKeptCollision, this.coefSpeedKeptCollision);
	}
	
	/** Lors d'un rebond sur un mur ou une entité semblable à un mur, cette
	 *  méthode calcule le rebond, effectuant plusieurs essais pour déterminer
	 * la direction dans laquelle va rebondir l'entité.
	 * @param condition - la condition qui détermine si cette entité est en collision.
	 * Si ce paramètre est une entité, on teste la collision avec celle-ci, sinon
	 * on teste la collision avec le décor. */
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
