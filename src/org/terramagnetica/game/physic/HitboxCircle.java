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

import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Circle;
import net.bynaryscode.util.maths.geometric.Vec2;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class HitboxCircle extends Hitbox {
	
	private static final long serialVersionUID = 1L;
	
	private float rayon;
	
	public HitboxCircle(Circle circle) {
		this.setCircle(circle);
	}
	
	public HitboxCircle(float radius) {
		this.setRadius(radius);
	}
	
	public void setCircle(Circle circle) {
		this.rayon = (float) circle.getRayon();
		this.setPosition((float) circle.center().x, (float) circle.center().y);
	}
	
	public void setRadius(float radius) {
		this.rayon = radius;
	}
	
	public float getRadius() {
		return this.rayon;
	}
	
	@Override
	protected CollisionPoint getCurrentCollisionPoint(Hitbox other, float time) {
		if (other instanceof HitboxCircle) {
			HitboxCircle otherCircle = (HitboxCircle) other;
			ensureSameTimeOffset(otherCircle);
			
			float vx = this.speedX - otherCircle.speedX;
			float vy = this.speedY - otherCircle.speedY;
			float x = this.x - otherCircle.x;
			float y = this.y - otherCircle.y;
			float r = this.rayon + otherCircle.rayon;
			
			float a = vx * vx + vy * vy;
			float b = 2 * (vx * x + vy * y);
			float c = x * x + y * y - r * r;
			
			float delta = b * b - 4 * a * c;
			if (delta < 0) return null;
			
			float t = (- b - (float) Math.sqrt(delta)) / (2 * a); //en s
			
			return new CollisionPoint(this.timeOffset + t * 1000, this, otherCircle);
		}
		else {
			return other.getCurrentCollisionPoint(this, time);
		}
	}
	
	@Override
	public void doNextCollision() {
		if (this.nextCollisionPoint == null) {
			throw new IllegalStateException("Il n'y a aucune collision à effectuer.");
		}
		
		Hitbox other = this.nextCollisionPoint.getOtherHitbox(this);
		
		if (other instanceof HitboxCircle) {
			if (this.isStatic) other.doNextCollision();
			//-> Je ne suis pas statique
			
			HitboxCircle otherCircle = (HitboxCircle) other;
			HitboxCircle[] both = new HitboxCircle[] {this, otherCircle};
			
			//Placement des hitbox au bon endroit
			this.nextCollisionPoint.goToPoint();
			
			//Calculs préliminaires
			Vec3d en = Vec3d.unitVector(otherCircle.x - this.x, otherCircle.y - this.y);
			
			Vec3d v1 = new Vec3d(this.speedX, this.speedY);
			double v1n = v1.dotProduct(en);
			Vec3d v1nVect = en.multiply(v1n);
			Vec3d v1tVect = v1.substract(v1nVect);
			
			if (! other.isStatic) {
				Vec3d v2 = new Vec3d(other.speedX, other.speedY);
				double v2n = v2.dotProduct(en);
				Vec3d v2nVect = en.multiply(v2n);
				Vec3d v2tVect = v2.substract(v2nVect);
				
				double massSum = this.mass + other.mass;
				
				//Calcul des nouvelles vitesses
				double v1nNew = (v1n * this.mass + v2n * other.mass - (v1n - v2n) * other.mass) / massSum;
				double v2nNew = (v2n * other.mass + v1n * this.mass - (v2n - v1n) * this.mass) / massSum;
				
				//Application sur les données physiques des deux hitbox
				this.speedX = (float) (v1tVect.x + v1nNew * en.x);
				this.speedY = (float) (v1tVect.y + v1nNew * en.y);
				
				other.speedX = (float) (v2tVect.x + v2nNew * en.x);
				other.speedY = (float) (v2tVect.y + v2nNew * en.y);
			}
			else {
				//Rebondissement de base.
				this.speedX = (float) (v1tVect.x - v1n * en.x);
				this.speedY = (float) (v1tVect.y - v1n * en.y);
			}
			
			for (HitboxCircle hb : both) {
				hb.nextCollisionPoint = null;
			}
		}
		else {
			other.doNextCollision();
		}
	}
	
	@Override
	public boolean contains(Vec2 pt) {
		Vec2d c = pt.asDouble();
		return MathUtil.getSquaredDistance(x, y, c.x, c.y) <= this.rayon * this.rayon;
	}
	
	@Override
	public boolean intersects(Hitbox other) {
		if (other instanceof HitboxCircle) {
			HitboxCircle hbCircle = (HitboxCircle) other;
			return MathUtil.getSquaredDistance(this.x, this.y, other.x, other.y)
					<= (this.rayon + hbCircle.rayon) * (this.rayon + hbCircle.rayon);
		}
		else {
			return other.intersects(this);
		}
	}
	
	@Override
	public HitboxCircle clone() {
		HitboxCircle clone = (HitboxCircle) super.clone();
		
		return clone;
	}
}
