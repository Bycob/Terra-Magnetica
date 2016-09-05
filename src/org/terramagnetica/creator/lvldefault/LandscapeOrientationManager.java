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

package org.terramagnetica.creator.lvldefault;

import static org.terramagnetica.game.lvldefault.OrientableLandscapeTile.*;

import java.util.ArrayList;

import org.terramagnetica.game.lvldefault.LandscapeTile;
import org.terramagnetica.game.lvldefault.OrientableLandscapeTile;
import org.terramagnetica.game.lvldefault.Room;

import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.maths.geometric.DimensionsInt;

public class LandscapeOrientationManager {
	
	private static final float PI = (float) Math.PI;
	public static final float E = 0, N = PI / 2, O = PI, S = PI * 3 / 2,
			NE = PI / 4, NO = PI * 3 / 4, SO = PI * 5 / 4, SE = PI * 7 / 4;
	
	private static final float[] orientationTable = {E, O, N, S, NE, SE, NO, SO, SO, NO, SE, NE};
	
	/**
	 * Donne les constantes d'orientation (définie dans
	 * {@link OrientableLandscapeTile}) correspondant à la valeur
	 * d'angle passée en paramètres.
	 * <p>Si l'angle est droit ou plat, alors l'orientation retournée
	 * est unique et correspond à un mur en bord (orienté vers un point
	 * cardinal). Sinon deux orientations sont retournées, la première
	 * correspondant à un mur en coin et la deuxième à un mur en angle.
	 * @param angle
	 * @return
	 */
	private static final int[] getConstForAngle(float angle) {
		ArrayList<Integer> constantes = new ArrayList<Integer>();
		
		for (int i = 0 ; i < orientationTable.length ; i++) {
			if (MathUtil.isAngleBetween(angle, orientationTable[i] - 0.01, orientationTable[i] + 0.01)) {
				constantes.add(i);
			}
		}
		
		int[] array = new int[constantes.size()];
		for (int i = 0 ; i < constantes.size() ; i++) array[i] = constantes.get(i);
		return array;
	}
	
	private static final float[][] envToAngleTable = 
		{{SE, E, NE},
		{S, 0, N},
		{SO, O, NO}};
	
	
	
	public LandscapeOrientationManager() {
		
	}
	
	/** Définit l'orientation du décor en fonction de ce
	 * qu'il y a autour de lui.
	 * @param l - La case de décor à orienter.
	 * @param room - La salle concernée.*/
	public void setLandscapeOrientation(OrientableLandscapeTile l, Room room) {
		if (l == null) throw new NullPointerException();
		
		Vec2i lc = l.getCoordonnéesCase();
		
		DimensionsInt rd = room.getDimensions();
		if (rd.getWidth() <= lc.x || rd.getHeight() <= lc.y) {
			
			throw new IllegalArgumentException("Le décor ne correspond pas à la salle");
		}
		
		boolean[][] env = new boolean[3][3];
		for (int x = lc.x - 1 ; x <= lc.x + 1 ; x++) {
			for (int y = lc.y - 1 ; y <= lc.y + 1 ; y++) {
				Class<? extends LandscapeTile> localType = null;
				
				if (x < 0 || y < 0 || x >= rd.getWidth() || y >= rd.getHeight()) {
					localType = l.getClass();
				}
				else {
					localType = room.getLandscapeAt(x, y).getClass();
				}
				
				env[x - lc.x + 1][y - lc.y + 1] = localType.equals(l.getClass());
			}
		}
		
		l.setOrientation(calculateOrientation(env));
	}
	
	
	private int calculateOrientation(boolean[][] env) {
		int orientation = PLANE;
		
		//Calcul du nombre de décors différents
		ArrayList<Vec2i> difCoordinates = new ArrayList<Vec2i>();
		for (int x = 0 ; x < 3 ; x++) {
			for (int y = 0 ; y < 3 ; y++) {
				if (!env[x][y]) difCoordinates.add(new Vec2i(x, y));
			}
		}
		
		if (!arePointsConsecutives(difCoordinates)) return orientation;
		//à partir d'ici on sait que les murs ne sont en contact qu'avec une seule salle.
		
		switch (difCoordinates.size()) {
		case 1 ://mur en coin ou en bord.
			Vec2i c = difCoordinates.get(0);
			orientation = getConstForAngle(envToAngleTable[c.x][c.y])[0];
			break;
		case 2 ://mur en bord obligatoirement
			Vec2i c1 = difCoordinates.get(0),
				c2 = difCoordinates.get(1);
			
			if (c1.x == 1 || c1.y == 1) {
				orientation = getConstForAngle(envToAngleTable[c1.x][c1.y])[0];
			}
			else {
				orientation = getConstForAngle(envToAngleTable[c2.x][c2.y])[0];
			}
			break;
		case 3 :
		case 4 :
		case 5 :
			Vec2i c0 = getCentralPoint(difCoordinates);
			int[] possibleOrientation = getConstForAngle(envToAngleTable[c0.x][c0.y]);
			if (possibleOrientation.length == 1) {
				orientation = possibleOrientation[0];//mur en bord
			}
			else {//==2
				orientation = possibleOrientation[1];//mur en angle
			}
			break;
		}
		
		return orientation;
	}
	
	
	private boolean arePointsConsecutives(ArrayList<Vec2i> points) {
		if (points.size() == 1) return true;
		
		for (int i = 0 ; i < points.size() - 1 ; i++) {
			boolean hasConsecutive = false;
			for (int j = 0 ; j < points.size() ; j++) {
				if (i == j) continue;
				if (MathUtil.getDistance(points.get(i), points.get(j)) == 1) {
					hasConsecutive = true;
					break;
				}
			}
			
			if (!hasConsecutive) return false;
		}
		
		return true;
	}
	
	/**
	 * Donne le point, parmi une liste de points consécutifs, qui
	 * définira la direction de l'orientation du mur. 
	 * <p>
	 * Suppose que les points soient consecutifs. Les coins sont
	 * prioritaires sur les bords. La liste contient au moins trois
	 * éléments.
	 * @param points
	 * @return
	 */
	private Vec2i getCentralPoint(ArrayList<Vec2i> points) {
		//Tri de la liste
		ArrayList<Vec2i> sortedList = new ArrayList<Vec2i>();
		final Vec2i[] pointOrder = {
				new Vec2i(2, 1),
				new Vec2i(2, 0),
				new Vec2i(1, 0),
				new Vec2i(0, 0),
				new Vec2i(0, 1),
				new Vec2i(0, 2),
				new Vec2i(1, 2),
				new Vec2i(2, 2)
		};
		
		boolean sortStarted = false;
		int pointOrderIndex = 0;
		
		while (sortedList.size() < points.size()) {
			if (!sortStarted) {
				if (!points.contains(pointOrder[pointOrderIndex])) {
					sortStarted = true;
				}
			}
			else {
				if (points.contains(pointOrder[pointOrderIndex])) {
					sortedList.add(pointOrder[pointOrderIndex]);
				}
			}
			
			pointOrderIndex++;
			if (pointOrderIndex == 8) pointOrderIndex = 0;
		}
		
		//Extraction du point central.
		float centralIndex = (sortedList.size() - 1f) / 2f;
		if (centralIndex == (int) centralIndex) {
			return sortedList.get((int) centralIndex);
		}
		else {
			Vec2i c1 = sortedList.get((int) Math.floor(centralIndex)),
					c2 = sortedList.get((int) Math.ceil(centralIndex));
			
			if (c1.x == 1 || c1.y == 1) {
				return c2;
			}
			else {
				return c1;
			}
		}
	}
}
