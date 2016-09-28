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

import java.util.ArrayList;

import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Shape;
import net.bynaryscode.util.maths.geometric.Line2D;
import net.bynaryscode.util.maths.geometric.Vec2;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class HitboxPolygon extends Hitbox {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<Vec2f> points = new ArrayList<Vec2f>();
	
	//Variables précalculées
	private boolean precalculated = false;
	private ArrayList<Vec2f> realPositions = new ArrayList<Vec2f>();
	private ArrayList<Line2D> edgeList = new ArrayList<Line2D>();
	
	public HitboxPolygon(Shape hitbox) {
		setForme(hitbox);
	}
	
	public void addPoint(Vec2f point) {
		this.points.add(point);
		
		this.precalculated = false;
	}
	
	public void removeAllPoints() {
		this.points.clear();
		
		this.precalculated = false;
	}
	
	public void setForme(Shape forme) {
		this.points.clear();
		for (Vec2d c : forme.getVertices()) {
			this.points.add(c.asFloat());
		}
		
		this.precalculated = false;
	}
	
	@Override
	protected void positionChanges() {
		this.precalculated = false;
	}
	
	@Override
	protected void rotationChanges() {
		this.precalculated = false;
	}
	
	private void precalculate() {
		if (this.precalculated) return;
		
		//Positions réelles
		this.realPositions.clear();
		for (Vec2f cf : this.points) {
			Vec2f clone = cf.clone();
			clone.translate(x, y); //position
			if (this.rotation != 0);//TODO rotation
			this.realPositions.add(clone);
		}
		
		//Arrêtes
		this.edgeList.clear();
		
		for (int i = 0 ; i < this.realPositions.size() ; i++) {
			Vec2f pt1 = this.realPositions.get(i);
			Vec2f pt2 = this.realPositions.get((i + 1) % this.realPositions.size());
			this.edgeList.add(new Line2D(pt1.x, pt1.y, pt2.x, pt2.y));
		}
		
		this.precalculated = true;
	}
	
	@Override
	public boolean contains(Vec2 point) {
		precalculate();
		
		Vec2f ptf = point.asFloat();
		
		for (Line2D d : this.edgeList) {
			if (d.equation(ptf.x, ptf.y) > 0) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public boolean intersects(Hitbox other) {
		precalculate();
		
		if (other instanceof HitboxPolygon) {
			HitboxPolygon otherPolygon = (HitboxPolygon) other;
			
			/*System.out.println("mes points : " + this.realPositions.toString() + "\t| ses points : " + otherPolygon.realPositions +
					"\t| ma position : " + this.x + " " + this.y + "\t| sa position : " + other.x + " " + other.y);*/
			
			for (Vec2f point : this.realPositions) {
				if (otherPolygon.contains(point)) return true;
			}

			for (Vec2f point : otherPolygon.realPositions) {
				if (this.contains(point)) return true;
			}
			return false;
		}
		else if (other instanceof HitboxCircle) {
			HitboxCircle otherCircle = (HitboxCircle) other;

			//test des collisions avec un bord
			for (int i = 0 ; i < this.realPositions.size() ; i++) {
				float radius = otherCircle.getRadius();
				Vec2f point1 = this.realPositions.get(i);
				Vec2f point2 = this.realPositions.get((i + 1) % this.realPositions.size());
				Line2D line = this.edgeList.get(i);
				
				if (otherCircle.contains(point1)) return true;
				if (line.squaredDistance(otherCircle.x, otherCircle.y) <= radius * radius) {
					Vec3d segment1 = new Vec3d(point1.x - point2.x, point1.y - point2.y);
					Vec3d vec1 = new Vec3d(otherCircle.x - point2.x, otherCircle.y - point2.y);
					double dotProd = segment1.dotProduct(vec1);
					
					if (dotProd >= 0 && dotProd <= segment1.dotProduct(segment1)) return true;
				}
			}
			
			return false;
		}
		else {
			return other.intersects(this);
		}
	}

	public Vec2f[] getRealPoints() {
		this.precalculate();
		return this.realPositions.toArray(new Vec2f[0]);
	}
	
	@Override
	protected CollisionPoint getCurrentCollisionPoint(Hitbox other, float time) {
		if (other instanceof HitboxPolygon) {
			HitboxPolygon otherPoly = (HitboxPolygon) other;
			
			return findCollisionPointDefault(otherPoly, time);
		}
		else if (other instanceof HitboxCircle) {
			HitboxCircle otherCircle = (HitboxCircle) other;
			
			return findCollisionPointDefault(otherCircle, time);
		}
		else {
			return other.getCurrentCollisionPoint(other, time);
		}
	}
	
	@Override
	public void calculateNextCollisionReaction() {
		if (this.nextCollisionPoint == null) {
			throw new IllegalStateException("Il n'y a aucune collision à effectuer.");
		}
		
		Hitbox other = this.nextCollisionPoint.getOtherHitbox(this);
		Hitbox[] both = new Hitbox[] {this, other};

		if (other instanceof HitboxPolygon) {
			if (this.isStatic) {
				other.calculateNextCollisionReaction();
				return;
			}
			//-> Je ne suis pas statique
			
			HitboxPolygon otherPoly = (HitboxPolygon) other;
			
			this.nextCollisionPoint.goToPoint();
			precalculate();
			otherPoly.precalculate();
			
			//On trouve le point de collision et ainsi le vecteur normal
			Line2D collisionEdge = null;
			Vec2f collisionPoint = null;
			boolean myEdge = false;
			double minDistance = Double.MAX_VALUE;
			
			for (int i = 0 ; i < this.realPositions.size() ; i++) {
				Vec2f point1 = this.realPositions.get(i);
				Vec2f point2 = this.realPositions.get((i + 1) % this.realPositions.size());
				Line2D line = this.edgeList.get(i);
				
				for (Vec2f point : otherPoly.realPositions) {
					double distance = line.squaredDistance(point.x, point.y);

					Vec3d segment1 = new Vec3d(point1.x - point2.x, point1.y - point2.y);
					Vec3d vec1 = new Vec3d(point.x - point2.x, point.y - point2.y);
					double dotProd = segment1.dotProduct(vec1);
					boolean edgeCollision = dotProd >= 0 && dotProd <= segment1.dotProduct(segment1);
					
					if (distance < minDistance && edgeCollision) {
						minDistance = distance;
						myEdge = true;
						collisionEdge = line;
						collisionPoint = point;
					}
				}
			}
			
			for (int i = 0 ; i < otherPoly.realPositions.size() ; i++) {
				Vec2f point1 = otherPoly.realPositions.get(i);
				Vec2f point2 = otherPoly.realPositions.get((i + 1) % otherPoly.realPositions.size());
				Line2D line = otherPoly.edgeList.get(i);
				
				for (Vec2f point : this.realPositions) {
					double distance = line.squaredDistance(point.x, point.y);

					Vec3d segment1 = new Vec3d(point1.x - point2.x, point1.y - point2.y);
					Vec3d vec1 = new Vec3d(point.x - point2.x, point.y - point2.y);
					double dotProd = segment1.dotProduct(vec1);
					boolean edgeCollision = dotProd >= 0 && dotProd <= segment1.dotProduct(segment1);
					
					if (distance < minDistance && edgeCollision) {
						minDistance = distance;
						myEdge = false;
						collisionEdge = line;
						collisionPoint = point;
					}
				}
			}
			
			Vec3d vecDir = collisionEdge.getDirectionVector();
			Vec3d en = Vec3d.unitVector(vecDir.y, - vecDir.x);
			
			//Initialisation des variables
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
				this.speedX = (float) (v1tVect.x * this.bounceT + v1nNew * en.x * this.bounceN) * this.bounce;
				this.speedY = (float) (v1tVect.y * this.bounceT + v1nNew * en.y * this.bounceN) * this.bounce;
				
				other.speedX = (float) (v2tVect.x * other.bounceT + v2nNew * en.x * other.bounceN) * other.bounce;
				other.speedY = (float) (v2tVect.y * other.bounceT + v2nNew * en.y * other.bounceN) * other.bounce;
			}
			else {
				//Rebondissement de base.
				this.speedX = (float) (v1tVect.x * this.bounceT - v1n * en.x * this.bounceN) * this.bounce;
				this.speedY = (float) (v1tVect.y * this.bounceT - v1n * en.y * this.bounceN) * this.bounce;
			}
			
			for (Hitbox hb : both) {
				hb.afterCollision();
			}
		}
		else if (other instanceof HitboxCircle) {
			this.nextCollisionPoint.goToPoint();
			precalculate();
			
			HitboxCircle otherCircle = (HitboxCircle) other;
			float radius2 = otherCircle.getRadius() * otherCircle.getRadius();
			
			boolean edgeCollision = false;
			Vec2f closerPoint = null;
			Line2D closerEdge = null;
			double distance = Double.MAX_VALUE;
			
			for (int i = 0 ; i < this.realPositions.size() ; i++) {
				Vec2f point1 = this.realPositions.get(i);
				Vec2f point2 = this.realPositions.get((i + 1) % this.realPositions.size());
				Line2D line = this.edgeList.get(i);
				
				//collision de bord ?
				Vec3d segment1 = new Vec3d(point1.x - point2.x, point1.y - point2.y);
				Vec3d vec1 = new Vec3d(otherCircle.x - point2.x, otherCircle.y - point2.y);
				double dotProd = segment1.dotProduct(vec1);
				edgeCollision = edgeCollision || (dotProd >= 0 && dotProd <= segment1.dotProduct(segment1));
				
				//distance à la ligne
				double lineDistance = Math.abs(line.squaredDistance(otherCircle.x, otherCircle.y) - radius2);
				
				//distance au point
				double pointDistance = Math.abs(MathUtil.getSquaredDistance(point1.x, point1.y, otherCircle.x, otherCircle.y) - radius2);
				
				if (edgeCollision) {
					if (lineDistance < distance || closerEdge == null) {
						distance = lineDistance;
						closerEdge = line;
						closerPoint = null;
					}
				}
				else {
					if (pointDistance < distance) {
						distance = pointDistance;
						closerPoint = point1;
						closerEdge = null;
					}
				}
			}
			
			//Vecteur normal dirigé vers le cercle
			Vec3d en;
			if (closerEdge != null) {
				Vec3d vecDir = closerEdge.getDirectionVector();
				en = Vec3d.unitVector(- vecDir.y, vecDir.x);
			}
			else {
				en = Vec3d.unitVector(otherCircle.x - closerPoint.x, otherCircle.y - closerPoint.y);
			}
			
			//Initialisation des variables
			Vec3d v1 = new Vec3d(this.speedX, this.speedY);
			double v1n = v1.dotProduct(en);
			Vec3d v1nVect = en.multiply(v1n);
			Vec3d v1tVect = v1.substract(v1nVect);
			
			Vec3d v2 = new Vec3d(other.speedX, other.speedY);
			double v2n = v2.dotProduct(en);
			Vec3d v2nVect = en.multiply(v2n);
			Vec3d v2tVect = v2.substract(v2nVect);
			
			if (this.isStatic) {
				other.speedX = (float) (v2tVect.x * other.bounceT - v2n * en.x * other.bounceN) * other.bounce;
				other.speedY = (float) (v2tVect.y * other.bounceT - v2n * en.y * other.bounceN) * other.bounce;
			}
			else if (other.isStatic) {
				//Rebondissement de base.
				this.speedX = (float) (v1tVect.x * this.bounceT - v1n * en.x * this.bounceN) * this.bounce;
				this.speedY = (float) (v1tVect.y * this.bounceT - v1n * en.y * this.bounceN) * this.bounce;
			}
			else {
				double massSum = this.mass + other.mass;
				
				//Calcul des nouvelles vitesses
				double v1nNew = (v1n * this.mass + v2n * other.mass - (v1n - v2n) * other.mass) / massSum;
				double v2nNew = (v2n * other.mass + v1n * this.mass - (v2n - v1n) * this.mass) / massSum;

				//Application sur les données physiques des deux hitbox
				this.speedX = (float) (v1tVect.x * this.bounceT + v1nNew * en.x * this.bounceN) * this.bounce;
				this.speedY = (float) (v1tVect.y * this.bounceT + v1nNew * en.y * this.bounceN) * this.bounce;
				
				other.speedX = (float) (v2tVect.x * other.bounceT + v2nNew * en.x * other.bounceN) * other.bounce;
				other.speedY = (float) (v2tVect.y * other.bounceT + v2nNew * en.y * other.bounceN) * other.bounce;
			}

			for (Hitbox hb : both) {
				hb.afterCollision();
			}
		}
		else {
			other.calculateNextCollisionReaction();
		}
	}
	
	@Override
	public HitboxPolygon clone() {
		HitboxPolygon clone = (HitboxPolygon) super.clone();
		
		clone.points = new ArrayList<Vec2f>();
		for (Vec2f point : this.points) {
			clone.points.add(point.clone());
		}
		
		clone.precalculated = false;
		clone.realPositions = new ArrayList<Vec2f>();
		clone.edgeList = new ArrayList<Line2D>();
		
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof HitboxPolygon)) {
			return false;
		}
		HitboxPolygon other = (HitboxPolygon) obj;
		if (points == null) {
			if (other.points != null) {
				return false;
			}
		} else if (!points.equals(other.points)) {
			return false;
		}
		return true;
	}
}
