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

package org.terramagnetica.game.physic;

import java.io.Serializable;

import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Vec2;
import net.bynaryscode.util.maths.geometric.Vec2f;

/**
 * La class Hitbox est au coeur du moteur physique de Terra Magnetica,
 * puisqu'elle rassemble toutes les méthodes nécessaires au calcul de
 * mouvements et collisions.
 * <p>
 * Attention, l'application des forces et de la friction ne se fait pas
 * en continu mais à chaque tick. Ainsi, il est conseillé d'avoir des
 * ticks de durée relativement courte (20 ms est une durée satisfaisante).
 */
public abstract class Hitbox implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	protected Force force = new Force(0, 0);
	
	protected float x, y;
	protected float rotation;
	
	protected float speedX, speedY;
	protected float speedRotation;
	
	protected float mass = 1;
	protected boolean isStatic = false;
	protected boolean isSolid = true;
	
	/** Coefficient de friction de l'objet. Indique la perte de vitesse
	 * naturelle de l'objet pendant son déplacement. */
	protected float friction = 0;
	/** Coefficient de rebond. Indique la perte de vitesse naturelle de
	 * l'objet lors d'un choc élastique. */
	protected float bounce = 1;
	protected float bounceN = 1;
	protected float bounceT = 1;
	
	/** Le nombre de secondes écoulées pour cette hitbox, à partir
	 * de l'origine des calculs */
	protected float timeOffset;
	protected CollisionPoint nextCollisionPoint;
	
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
	
	public void setRotation(float rotation) {
		this.rotation = rotation;
		this.rotationChanges();
	}
	
	public float getRotation() {
		return this.rotation;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
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
	
	public float getSpeedX() {
		return this.speedX;
	}
	
	public float getSpeedY() {
		return this.speedY;
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
	
	/** Définit le coefficient de friction de cette hitbox. Le coefficient
	 * de friction quantifie la perte de vitesse lors du déplacement de l'objet.
	 * L'objet subira à chaque instant une force F = -k * v avec k le coefficient
	 * sus-nommé. */
	public void setFriction(float friction) {
		this.friction = friction;
	}
	
	/** @see #setFriction(float) */
	public float getFriction() {
		return this.friction;
	}
	
	/** Définit le coefficient de rebond de cette hitbox. Lors d'un choc élastique,
	 * la vitesse de l'objet sera amortie de <tt>bounce</tt> %. */
	public void setBounce(float bounce) {
		this.bounce = bounce;
	}
	
	/** @see #setBounce(float) */
	public float getBounce() {
		return this.bounce;
	}
	
	/** Définit de façon plus fine le rebond de cette hitbox : <tt>bounceN</tt>
	 * quantifie l'importance du rebond (rebond normal), <tt>bounceT</tt> quantifie
	 * inversement l'adhérence de la hitbox à la surface de rebond (rebond
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
	
	/** Cette méthode est appelée à chaque fois que la position de la hitbox
	 * est modifiée */
	protected void positionChanges() {}
	/** Cette méthode est appelée à chaque fois que la rotation de la hitbox
	 * est modifiée */
	protected void rotationChanges() {}
	
	/** Retourne {@code true} si les variables physique (position, angle,
	 * vitesse) de cette hitbox sont les mêmes que les variables physiques
	 * de la hitbox passée en paramètres. */
	public boolean hasSamePhysic(Hitbox other) {
		return this.x == other.x &&
				this.y == other.y &&
				this.rotation == other.rotation &&
				this.speedX == other.speedX &&
				this.speedY == other.speedY && 
				this.speedRotation == other.speedRotation;
	}
	
	public void setSamePhysic(Hitbox other) {
		this.isStatic = other.isStatic;
		this.isSolid = other.isSolid;
		
		this.x = other.x;
		this.y = other.y;
		this.rotation = other.rotation;
		this.speedX = other.speedX;
		this.speedY = other.speedY;
		this.speedRotation = other.speedRotation;
		
		this.friction = other.friction;
		this.bounce = other.bounce;
		this.bounceT = other.bounceT;
		this.bounceN = other.bounceN;
		
		this.force = other.force.clone();
	}
	
	public abstract boolean contains(Vec2 point);
	public abstract boolean intersects(Hitbox other);
	
	
	// FONCTIONS LIES AUX MOUVEMENTS DE LA HITBOX
	
	protected void ensureSameTimeOffset(Hitbox other) {
		if (Math.abs(this.timeOffset - other.timeOffset) > 0.001) {
			other.applyVelocity(this.timeOffset - other.timeOffset);
		}
	}
	
	/** Applique les forces pour modifier la vitesse de l'entité.
	 * @param ms Le temps pendant lequel on applique les forces, en ms.*/
	public void applyForces(float ms) {
		float time = ms / 1000;
		
		if (!this.isStatic) {
			float frictionRatio = 1 - this.friction * time;
			this.speedX = (this.force.getForceX() * time + this.speedX) * frictionRatio;
			this.speedY = (this.force.getForceY() * time + this.speedY) * frictionRatio;
		}
	}
	
	/** Fait varier la position de l'objet en fonction de sa vitesse.
	 * <p>La hitbox enregistre le temps de déplacement. Ainsi on peut déplacer
	 * plusieurs fois la hitbox pendant un même tick, et il suffit d'appeler la
	 * méthode {@link #completeMove(float)} à la fin d'un tick pour le complèter.
	 * @param ms La durée du mouvement, en ms */
	public void applyVelocity(float ms) {
		float time = ms / 1000;
		
		if (!this.isStatic) {
			this.x = this.x + this.speedX * time;
			this.y = this.y + this.speedY * time;
			
			positionChanges();
		}
		
		this.timeOffset += ms;
	}
	
	/** Complète le mouvement pour qu'il aie duré le temps <tt>completeTime</tt>,
	 * puis réinitialise l'écart de temps enregistré.
	 * @param completeMsTime Le temps d'un tick, en millisecondes. */
	public void completeMove(float completeMsTime) {
		if (this.timeOffset != completeMsTime) {
			this.applyVelocity(completeMsTime - this.timeOffset);
		}
		this.timeOffset = 0;
	}
	
	public boolean hasNextCollisionPoint() {
		return this.nextCollisionPoint != null;
	}
	
	public CollisionPoint getNextCollisionPoint() {
		return this.nextCollisionPoint;
	}
	
	public void calculateNextCollisionPoint(Hitbox other, float time) {
		if (!testCollision(other, time)) {
			return;
		}
		
		CollisionPoint cp = getCurrentCollisionPoint(other, time);
		Hitbox[] both = new Hitbox[] {this, other};
		
		for (Hitbox hb : both) {
			hb.nextCollisionPoint = cp; //testCollision ne détecte que les collisions prioritaires.
		}
	}
	
	/** Cette méthode teste si une collision va avoir lieu entre les deux hitbox
	 * dans le temps imparti.
	 * <p>
	 * Attention, cette méthode déplace les deux hitbox et ne les remet pas en
	 * place, afin d'économiser des calculs. Si une collision est détectée, il 
	 * appartient aux hitbox filles de recaler les hitbox au point de collision.
	 * <p>
	 * Une conséquence est que les deux hitbox auront le même {@link #timeOffset}
	 * si cette méthode renvoie <tt>true</tt>.
	 * <p>
	 * Un appel à la méthode {@link #completeMove(float)} permettra de clore le
	 * mouvement, une fois toutes les collisions calculées.*/
	protected boolean testCollision(Hitbox other, float time) {
		if (this.isStatic && other.isStatic) return false;
		if (!this.isSolid || !other.isSolid) return false;
		
		Hitbox[] both = new Hitbox[] {this, other};
		
		//prétest visant à réduire les calculs
		if (this.speedX == other.speedX && this.speedY == other.speedY) {
			return false;
		}
		if (MathUtil.getSquaredDistance(this.x, this.y, other.x, other.y) > 4*4) {
			//TODO Changer ce critère pourri
			return false;
		}
		
		//Procédure de détection de collision
		float maxTime = time;
		for (Hitbox hb : both) {
			if (hb.nextCollisionPoint != null) {
				maxTime = hb.nextCollisionPoint.getTime();
			}
		}
		
		for (Hitbox hb : both) {
			hb.applyVelocity(maxTime - hb.timeOffset);
		}
		
		return this.intersects(other);
	}
	
	/** Cette méthode doit être appelée lorsque la collision avec la hitbox
	 * <tt>other</tt> a été confirmée, afin de calculer l'instant exacte de la
	 * collision.
	 * <p>
	 * Attention, cette méthode peut éventuellement déplacer la hitbox.*/
	protected abstract CollisionPoint getCurrentCollisionPoint(Hitbox other, float time);
	
	/** Déplace les hitbox passées en paramètre en leur point de collision.
	 * On suppose que les objets s'interpénètrent à t = <tt>time</tt>, et ne
	 * se touche pas à t = 0. Cette méthode trouve un point de collision
	 * approximé par dichotomie. */
	protected final CollisionPoint findCollisionPointDefault(Hitbox other, float time) {
		Hitbox both[] = new Hitbox[] {this, other};
		
		float lower = 0;
		float higher = time;
		
		while (higher - lower > 0.0001) {
			float bound = (higher + lower) / 2;
			for (Hitbox hb : both) {
				applyVelocity(bound - hb.timeOffset);
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
	
	/** Cette méthode nécessite qu'un point de collision ait été précédemment trouvé
	 * par la méthode {@link #calculateNextCollisionPoint(Hitbox, float)}.<p>
	 * Place les deux hitbox concernées par cette collision, à l'instant de leur
	 * collision, puis calcule leur nouvelles vitesses suite au rebond. */
	public abstract void doNextCollision();
	
	@Override
	public Hitbox clone() {
		try {
			return (Hitbox) super.clone();
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
}
