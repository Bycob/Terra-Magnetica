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

package org.terramagnetica.physics;

import java.io.Serializable;
import java.util.ArrayList;

import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Vec2;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec3d;

/**
 * La class Hitbox est au coeur du moteur physique de Terra Magnetica,
 * puisqu'elle rassemble toutes les m�thodes n�cessaires au calcul de
 * mouvements et collisions.
 * <p>
 * Attention, l'application des forces et de la friction ne se fait pas
 * en continu mais � chaque tick. Ainsi, il est conseill� d'avoir des
 * ticks de dur�e relativement courte (20 ms est une dur�e satisfaisante).
 */
public abstract class Hitbox implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	/** Ce champs permet d'ajouter des informations externes � la hitbox. */
	public HitboxInfo infos;
	
	//Variables
	protected Force force = new Force(0, 0);
	
	protected float x, y;
	protected float rotation;
	
	protected float speedX, speedY;
	protected float speedRotation;
	
	//Propri�t�s
	protected float mass = 1;
	protected boolean isStatic = false;
	protected boolean isSolid = true;
	protected float maxSpeed = -1;
	
	/** Coefficient de friction de l'objet. Indique la perte de vitesse
	 * naturelle de l'objet pendant son d�placement. */
	protected float friction = 0;
	/** Coefficient de rebond. Indique la perte de vitesse naturelle de
	 * l'objet lors d'un choc �lastique. */
	protected float bounce = 1;
	protected float bounceN = 1;
	protected float bounceT = 1;
	
	protected HitboxFamily family = null;
	
	//Collisions
	/** Le nombre de secondes �coul�es pour cette hitbox, � partir
	 * de l'origine des calculs */
	protected float timeOffset;
	protected CollisionPoint nextCollisionPoint;
	
	/** Cette liste enregistre les hitboxes collisionn�es au temps "lastCollision".
	 * Ainsi, on peut �viter les d�tections de collision infinies. */
	protected ArrayList<Hitbox> lastHitboxes = new ArrayList<Hitbox>();
	protected float lastCollision = 0;
	protected float lastCollisionX;
	protected float lastCollisionY;
	
	
	public Hitbox setStatic(boolean isStatic) {
		this.isStatic = isStatic;
		return this;
	}
	
	public boolean isStatic() {
		return this.isStatic;
	}
	
	public Hitbox setSolid(boolean solid) {
		this.isSolid = solid;
		return this;
	}
	
	public boolean isSolid() {
		return this.isSolid;
	}
	
	public void setFamily(HitboxFamily family) {
		this.family = family;
	}
	
	public HitboxFamily getFamily() {
		return this.family;
	}
	
	public void setRotation(float rotation) {
		this.rotation = rotation;
		this.rotationChanges();
	}
	
	public float getRotation() {
		return this.rotation;
	}
	
	/** Change directement la position de la hitbox. Cette m�thode est � 
	 * utiliser en dehors de la mise � jour du moteur physique. */
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		updateLastCollisionData();
		this.positionChanges();
	}
	
	public void translate(float x, float y) {
		this.x += x;
		this.y += y;
		this.positionChanges();
	}
	
	public Vec2f getPosition() {
		return new Vec2f(this.x, this.y);
	}
	
	public float getPositionX() {
		return this.x;
	}
	
	public float getPositionY() {
		return this.y;
	}
	
	public void setLinearSpeed(float x, float y) {
		if (this.isStatic) return;
		this.speedX = x;
		this.speedY = y;
	}
	
	public void scaleLinearSpeed(float scaleX, float scaleY) {
		if (this.isStatic) return;
		this.speedX *= scaleX;
		this.speedY *= scaleY;
	}
	
	public Vec2f getSpeed() {
		return new Vec2f(this.speedX, this.speedY);
	}
	
	public float getSquaredSpeedLength() {
		return this.speedX * this.speedX + this.speedY * this.speedY;
	}
	
	public float getSpeedLength() {
		return (float) Math.sqrt(this.getSquaredSpeedLength());
	}
	
	public float getSpeedX() {
		return this.speedX;
	}
	
	public float getSpeedY() {
		return this.speedY;
	}
	
	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	
	public float getMaxSpeed() {
		return this.maxSpeed;
	}
	
	/** Si elle est trop grande, r�duit la vitesse de cette hitbox pour
	 * qu'elle ne soit pas plus grande que {@link #getMaxSpeed()}. */
	protected void capSpeed() {
		float scale = this.maxSpeed / this.getSpeedLength();
		if (scale < 1) {
			this.scaleLinearSpeed(scale, scale);
		}
	}
	
	public void setRotationSpeed(float speed) {
		this.speedRotation = speed;
	}
	
	public float getRotationSpeed() {
		return this.speedRotation;
	}
	
	public void addForce(Force force) {
		//Cela changera avec les forces non constantes dans le temps
		this.force.setForce(this.force.getForceX() + force.getForceX(), this.force.getForceY() + force.getForceY());
	}
	
	public void clearForces() {
		this.force.setForce(0, 0);
	}
	
	public void setMass(float mass) {
		this.mass = mass;
	}
	
	public float getMass() {
		return this.mass;
	}
	
	/** D�finit le coefficient de friction de cette hitbox. Le coefficient
	 * de friction quantifie la perte de vitesse lors du d�placement de l'objet.
	 * L'objet subira � chaque instant une force F = -k * v avec k le coefficient
	 * sus-nomm�. */
	public void setFriction(float friction) {
		this.friction = friction;
	}
	
	/** @see #setFriction(float) */
	public float getFriction() {
		return this.friction;
	}
	
	/** D�finit le coefficient de rebond de cette hitbox. Lors d'un choc �lastique,
	 * la vitesse de l'objet sera amortie de <tt>bounce</tt> %. */
	public void setBounce(float bounce) {
		this.bounce = bounce;
	}
	
	/** @see #setBounce(float) */
	public float getBounce() {
		return this.bounce;
	}
	
	/** D�finit de fa�on plus fine le rebond de cette hitbox : <tt>bounceN</tt>
	 * quantifie l'importance du rebond (rebond normal), <tt>bounceT</tt> quantifie
	 * inversement l'adh�rence de la hitbox � la surface de rebond (rebond
	 * tangentiel)  */
	public Hitbox setBounceWeighting(float bounceN, float bounceT) {
		this.bounceN = bounceN;
		this.bounceT = bounceT;
		return this;
	}
	
	public float getBounceN() {
		return this.bounceN;
	}
	
	public float getBounceT() {
		return this.bounceT;
	}
	
	/** Cette m�thode est appel�e � chaque fois que la position de la hitbox
	 * est modifi�e */
	protected void positionChanges() {}
	
	/** Cette m�thode est appel�e � chaque fois que la rotation de la hitbox
	 * est modifi�e */
	protected void rotationChanges() {}
	
	/** Retourne {@code true} si les variables physique (position, angle,
	 * vitesse) de cette hitbox sont les m�mes que les variables physiques
	 * de la hitbox pass�e en param�tres. */
	public boolean hasSamePhysicVariables(Hitbox other) {
		return this.x == other.x &&
				this.y == other.y &&
				this.rotation == other.rotation &&
				this.speedX == other.speedX &&
				this.speedY == other.speedY && 
				this.speedRotation == other.speedRotation &&
				this.force.equals(other.force);
	}
	
	/** Copie toutes les donn�es physiques de cette hitbox. */
	public void setSamePhysicPropertiesAndVariables(Hitbox other) {
		this.infos = other.infos;
		
		//Variables
		this.x = other.x;
		this.y = other.y;
		this.rotation = other.rotation;
		this.speedX = other.speedX;
		this.speedY = other.speedY;
		this.speedRotation = other.speedRotation;
		
		this.force = other.force.clone();
		
		//Propri�t�s
		this.family = other.family;
		this.mass = other.mass;
		this.isStatic = other.isStatic;
		this.isSolid = other.isSolid;
		this.maxSpeed = other.maxSpeed;
		
		this.friction = other.friction;
		this.bounce = other.bounce;
		this.bounceT = other.bounceT;
		this.bounceN = other.bounceN;
	}
	
	public abstract boolean contains(Vec2 point);
	public abstract boolean intersects(Hitbox other);
	
	
	// FONCTIONS LIES AUX MOUVEMENTS DE LA HITBOX
	
	protected void ensureSameTimeOffset(Hitbox other) {
		if (Math.abs(this.timeOffset - other.timeOffset) > 0.001) {
			other.applyVelocity(this.timeOffset - other.timeOffset);
		}
	}
	
	/** Applique les forces pour modifier la vitesse de l'entit�.
	 * @param ms Le temps pendant lequel on applique les forces, en ms.*/
	public void applyForces(float ms) {
		float time = ms / 1000;
		
		if (!this.isStatic) {
			float frictionRatio = 1 - this.friction * time;
			this.speedX = (this.force.getForceX() * time + this.speedX) * frictionRatio;
			this.speedY = (this.force.getForceY() * time + this.speedY) * frictionRatio;
		}
		
		capSpeed();
	}

	public void impulse(float impulseX, float impulseY) {
		//Obtention des variables dans le rep�re de Frenet.
		Vec3d en = Vec3d.unitVector(impulseX, impulseY);
		Vec3d v = new Vec3d(this.speedX, this.speedY);
		double vn = v.dotProduct(en);
		Vec3d vnVect = en.multiply(vn);
		Vec3d vtVect = v.substract(vnVect);

		double impulseLength = Math.sqrt(impulseX * impulseX + impulseY * impulseY);
		this.speedX = (float) (vtVect.x + en.x * impulseLength);
		this.speedY = (float) (vtVect.y + en.y * impulseLength);
	}
	
	/** Fait varier la position de l'objet en fonction de sa vitesse.
	 * <p>La hitbox enregistre le temps de d�placement. Ainsi on peut d�placer
	 * plusieurs fois la hitbox pendant un m�me tick, et il suffit d'appeler la
	 * m�thode {@link #completeMove(float)} � la fin d'un tick pour le compl�ter.
	 * @param ms La dur�e du mouvement, en ms */
	public void applyVelocity(float ms) {
		setTimeOffset(ms + this.timeOffset);
	}
	
	protected void setTimeOffset(float ms) {
		float time = (ms - this.lastCollision) / 1000;
		
		if (!this.isStatic) {
			this.x = this.lastCollisionX + this.speedX * time;
			this.y = this.lastCollisionY + this.speedY * time;
			
			positionChanges();
		}
		
		this.timeOffset = ms;
	}
	
	/** Compl�te le mouvement pour qu'il aie dur� le temps <tt>completeTime</tt>,
	 * puis r�initialise l'�cart de temps enregistr�.
	 * @param completeMsTime Le temps d'un tick, en millisecondes. */
	public void completeMove(float completeMsTime) {
		if (this.timeOffset != completeMsTime) {
			this.applyVelocity(completeMsTime - this.timeOffset);
		}
		this.nextCollisionPoint = null;
		this.timeOffset = 0;
		updateLastCollisionData();
	}
	
	public boolean hasNextCollisionPoint() {
		return this.nextCollisionPoint != null;
	}
	
	public CollisionPoint getNextCollisionPoint() {
		return this.nextCollisionPoint;
	}
	
	public float getLastCollisionTime() {
		return this.lastCollision;
	}
	
	public void calculateNextCollisionPoint(Hitbox other, float time) {
		if (!testCollision(other, time)) {
			return;
		}
		
		CollisionPoint cp = getCurrentCollisionPoint(other, time);
		
		//d�tection des collisions infinies
		if (this.lastCollision >= cp.getTime() && this.lastHitboxes.contains(other)) {
			return;
		}
		
		Hitbox[] both = new Hitbox[] {this, other};
		
		for (Hitbox hb : both) {
			if (hb.nextCollisionPoint != null) hb.nextCollisionPoint.delete();
			hb.nextCollisionPoint = cp; 
		}
	}
	
	public boolean canCollide(Hitbox other) {
		if (this.isStatic && other.isStatic) return false;
		if (!this.isSolid || !other.isSolid) return false;
		
		if (this.family != null) {
			if (!this.family.canCollide(other.family)) return false;
		}
		else if (other.family != null) {
			if (!other.family.canCollide(this.family)) return false;
		}
		
		return true;
	}
	
	/** Cette m�thode teste si une collision va avoir lieu entre les deux hitbox
	 * dans le temps imparti.
	 * <p>
	 * Attention, cette m�thode d�place les deux hitbox et ne les remet pas en
	 * place, afin d'�conomiser des calculs. Si une collision est d�tect�e, il 
	 * appartient aux hitbox filles de recaler les hitbox au point de collision.
	 * <p>
	 * Une cons�quence est que les deux hitbox auront le m�me {@link #timeOffset}
	 * si cette m�thode renvoie <tt>true</tt>.
	 * <p>
	 * Un appel � la m�thode {@link #completeMove(float)} permettra de clore le
	 * mouvement, une fois toutes les collisions calcul�es.*/
	protected boolean testCollision(Hitbox other, float time) {
		if (!canCollide(other)) return false;
		
		Hitbox[] both = new Hitbox[] {this, other};
		
		//pr�test visant � r�duire les calculs
		if (this.speedX == other.speedX && this.speedY == other.speedY) {
			return false;
		}
		if (MathUtil.getSquaredLength(this.x, this.y, other.x, other.y) > 4*4) {
			//TODO Changer ce crit�re pourri
			return false;
		}
		
		//Proc�dure de d�tection de collision
		float maxTime = time;
		for (Hitbox hb : both) {
			if (hb.nextCollisionPoint != null) {
				maxTime = hb.nextCollisionPoint.getTime();
			}
		}
		
		for (Hitbox hb : both) {
			hb.setTimeOffset(maxTime);
		}
		
		return this.intersects(other);
	}
	
	/** Cette m�thode doit �tre appel�e lorsque la collision avec la hitbox
	 * <tt>other</tt> a �t� confirm�e, afin de calculer l'instant exacte de la
	 * collision.
	 * <p>
	 * Attention, cette m�thode peut �ventuellement d�placer la hitbox.*/
	protected abstract CollisionPoint getCurrentCollisionPoint(Hitbox other, float time);
	
	/** D�place les hitbox pass�es en param�tre en leur point de collision.
	 * On suppose que les objets s'interp�n�trent � t = <tt>time</tt>, et ne
	 * se touche pas � t = 0. Cette m�thode trouve un point de collision
	 * approxim� par dichotomie. */
	protected final CollisionPoint findCollisionPointDefault(Hitbox other, float time) {
		Hitbox both[] = new Hitbox[] {this, other};
		
		float lower = Math.max(this.lastCollision, other.lastCollision);
		float higher = time;
		
		while (higher - lower > 0.0001) {
			float bound = (higher + lower) / 2;
			
			for (Hitbox hb : both) {
				hb.applyVelocity(bound - hb.timeOffset);
			}
			
			if (this.intersects(other)) {
				higher = bound;
			}
			else {
				lower = bound;
			}
		}
		
		return new CollisionPoint(lower, this, other);
	}
	
	/** Cette m�thode n�cessite qu'un point de collision ait �t� pr�c�demment trouv�
	 * par la m�thode {@link #calculateNextCollisionPoint(Hitbox, float)}.<p>
	 * Place les deux hitbox concern�es par cette collision, � l'instant de leur
	 * collision, puis calcule leur nouvelles vitesses suite au rebond. */
	public abstract void calculateNextCollisionReaction();
	
	protected void beforeCollision() {}
	
	protected void afterCollision() {
		updateLastCollisionData();
		this.nextCollisionPoint = null;
		
		this.capSpeed();
	}
	
	/** Enregistre l'�tat de la hitbox lors de la derni�re collision, afin
	 * de servir d'origine pour les calculs suivants.
	 * <p>Cette m�thode permet aussi de r�initialiser les variables concernant
	 * la derni�re collision, entre chaque tour. */
	public void updateLastCollisionData() {
		if (this.nextCollisionPoint != null) {
			if (this.lastCollision != this.nextCollisionPoint.getTime()) {
				this.lastHitboxes.clear();
			}
			
			this.lastCollision = this.nextCollisionPoint.getTime();
			this.lastHitboxes.add(this.nextCollisionPoint.getOtherHitbox(this));
		}
		else {
			this.lastCollision = 0;
			this.lastHitboxes.clear();
		}
		
		this.lastCollisionX = this.x;
		this.lastCollisionY = this.y;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((force == null) ? 0 : force.hashCode());
		result = prime * result + Float.floatToIntBits(rotation);
		result = prime * result + Float.floatToIntBits(speedRotation);
		result = prime * result + Float.floatToIntBits(speedX);
		result = prime * result + Float.floatToIntBits(speedY);
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Hitbox)) {
			return false;
		}
		Hitbox other = (Hitbox) obj;
		if (!hasSamePhysicVariables(other)) {
			return false;
		}
		return true;
	}
	
	@Override
	public Hitbox clone() {
		Hitbox clone = null;
		
		try {
			clone = (Hitbox) super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		clone.force = this.force.clone();
		
		return clone;
	}
}
