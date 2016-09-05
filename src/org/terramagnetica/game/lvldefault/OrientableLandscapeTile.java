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

import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxNull;
import org.terramagnetica.physics.HitboxPolygon;

import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.Vec2i;

public abstract class OrientableLandscapeTile extends LandscapeTile {

	private static final long serialVersionUID = 1L;
	
	public static final int NB_IMAGE = 12;
	public static final float DEFAULT_MARGIN = 0.2f;
	public static final float SIDE_OVERFLOW = 0.3f;
	
	/** Le décor n'est pas orienté, car il est entouré de
	 * décor semblable à lui. */
	public static final int PLANE = -1;
	public static final int DROITE = 0;
	public static final int GAUCHE = 1;
	public static final int HAUT = 2;
	public static final int BAS = 3;
	public static final int COIN_DROIT_HAUT = 4;
	public static final int COIN_DROIT_BAS = 5;
	public static final int COIN_GAUCHE_HAUT = 6;
	public static final int COIN_GAUCHE_BAS = 7;
	public static final int ANGLE_DROIT_HAUT = 8;
	public static final int ANGLE_DROIT_BAS = 9;
	public static final int ANGLE_GAUCHE_HAUT = 10;
	public static final int ANGLE_GAUCHE_BAS = 11;

	
	protected int orientation;
	protected float margin = DEFAULT_MARGIN;
	
	public OrientableLandscapeTile() {
		super();
	}

	public OrientableLandscapeTile(int x, int y) {
		super(x, y);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + orientation;
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
		if (!(obj instanceof OrientableLandscapeTile)) {
			return false;
		}
		OrientableLandscapeTile other = (OrientableLandscapeTile) obj;
		if (orientation != other.orientation) {
			return false;
		}
		return true;
	}
	
	public int getOrientation() {
		return orientation;
	}
	
	public void setOrientation(int type) {
		if (type >= -1 && type < NB_IMAGE)
			this.orientation = type;
		else throw new IllegalArgumentException("L'orientation choisie est inconnue de cet objet.");
	}
	
	@Override
	public byte getMetadata() {
		return (byte) this.orientation;
	}
	
	@Override
	public void setMetadata(byte md) {
		this.orientation = md;
	}
	
	@Override
	public Hitbox getHitboxf() {
		Hitbox result = new HitboxPolygon(new RectangleDouble(0, 0, 1, 1));
		
		switch (this.orientation) {
		case COIN_DROIT_BAS :
		case COIN_DROIT_HAUT :
		case COIN_GAUCHE_BAS :
		case COIN_GAUCHE_HAUT :
			result = new HitboxNull();
			break;
		case DROITE :
			result = new HitboxPolygon(new RectangleDouble(margin, - SIDE_OVERFLOW, 1, 1 + SIDE_OVERFLOW));
			break;
		case GAUCHE :
			result = new HitboxPolygon(new RectangleDouble(0, - SIDE_OVERFLOW, 1 - margin, 1 + SIDE_OVERFLOW));
			break;
		case HAUT :
			result = new HitboxPolygon(new RectangleDouble(- SIDE_OVERFLOW, 0, 1 + SIDE_OVERFLOW, 1 - margin));
			break;
		case BAS : 
			result = new HitboxPolygon(new RectangleDouble(- SIDE_OVERFLOW, margin, 1 + SIDE_OVERFLOW, 1));
			break;
		case ANGLE_DROIT_BAS :
			result = new HitboxPolygon(new RectangleDouble(- SIDE_OVERFLOW, - SIDE_OVERFLOW, 1 - margin, 1 - margin));
			break;
		case ANGLE_GAUCHE_BAS :
			result = new HitboxPolygon(new RectangleDouble(margin, - SIDE_OVERFLOW, 1 + SIDE_OVERFLOW, 1 - margin));
			break;
		case ANGLE_DROIT_HAUT :
			result = new HitboxPolygon(new RectangleDouble(- SIDE_OVERFLOW, margin, 1 - margin, 1 + SIDE_OVERFLOW));
			break;
		case ANGLE_GAUCHE_HAUT :
			result = new HitboxPolygon(new RectangleDouble(margin, margin, 1 + SIDE_OVERFLOW, 1 + SIDE_OVERFLOW));
			break;
		}
		
		Vec2i cCase = this.getCoordonnéesCase();
		result.setPosition(cCase.x, cCase.y);
		result.setStatic(true);
		
		return result;
	}
}
