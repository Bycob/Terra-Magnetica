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

import java.awt.Rectangle;

import org.terramagnetica.game.lvldefault.CaseEntity;
import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.LandscapeTile;
import org.terramagnetica.game.lvldefault.OrientableLandscapeTile;
import org.terramagnetica.game.lvldefault.Room;

import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.Vec2i;

/**
 * Cette classe permet de peindre les d�cors sur la salle d�finie
 * auparavent, ainsi que d'y ajouter une/des entit�.
 * @author Louis JEAN
 *
 */
public class LevelDefaultController {
	
	private Room room;
	
	private LandscapeOrientationManager orientationManager = new LandscapeOrientationManager();
	
	private Entity lastEntityPainted;
	
	/**
	 * @param room - La salle sur laquelle faire des modifications. Si
	 * elle vaut {@code null} alors les m�thode du {@link LevelDefaultController} n'auront
	 * aucun impact sur quoi que ce soit.
	 */
	public LevelDefaultController(Room room) {
		setRoom(room);
	}
	
	public void setRoom(Room newRoom) {
		if (newRoom == null) this.room = new Room();
		this.room = newRoom;
	}
	
	/**
	 * Peint le d�cor avec le pinceau indiqu�, sur la plage donn�e, ajoute des murs s'il le faut.
	 * Les mesures sont en unit�s de base (une case = 256).
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param land - pinceau
	 */
	public void paintDecor(int x1, int y1, int x2, int y2, PinceauLandscape land){
		if (!checkAction(x1 , y1, false) || !checkAction(x2, y2, false))
			return;
		int xmin = x1 > x2 ? x2 : x1; xmin /= Entity.CASE;
		int xmax = x1 > x2 ? x1 : x2; xmax /= Entity.CASE;
		int ymin = y1 > y2 ? y2 : y1; ymin /= Entity.CASE;
		int ymax = y1 > y2 ? y1 : y2; ymax /= Entity.CASE;
		
		Vec2i pointMin = new Vec2i(xmin, ymin);
		Vec2i pointMax = new Vec2i(xmax, ymax);
		
		//remplissage avec le d�cor
		for (int i = pointMin.x ; i <= pointMax.x ; i++){
			for (int j = pointMin.y ; j <= pointMax.y ; j++){
				this.room.setLandscapeAt(i , j ,
						land.createInstance());
			}
		}
		
		//Ajustement si le d�cor est orientable
		for (int x = pointMin.x - 1 ; x <= pointMax.x + 1 ; x++) {
			for (int y = pointMin.y - 1 ; y <= pointMax.y + 1 ; y++) {
				
				if (x <= pointMin.x || x >= pointMax.x ||
						y <= pointMin.y || y >= pointMax.y) {
					LandscapeTile l = this.room.getLandscapeAt(x, y);
					if (l instanceof OrientableLandscapeTile) {
						OrientableLandscapeTile lo = (OrientableLandscapeTile) l;
						orientationManager.setLandscapeOrientation(lo, this.room);
					}
				}
			}
		}
		
		//d�truit tous les composants dans la zone s'il le faut.
		if (!land.createInstance().isEnabled()){
			this.room.removeEntityIn(new Rectangle(
					(pointMin.x - 1) * Entity.CASE,
					(pointMin.y - 1) * Entity.CASE,
					(pointMax.x - pointMin.x + 3) * Entity.CASE,
					(pointMax.y - pointMin.y + 3) * Entity.CASE));
		}
	}
	
	/** 
	 * Cr�e une entit� et l'ajoute � la salle.
	 * @param x - abscisse de l'entit�
	 * @param y - ordonn�e de l'entit�
	 * @param entity - le pinceau qui servira � cr�er l'entit�.
	 */
	public boolean paintEntity(int x, int y, PinceauEntity entity){
		return this.paintEntity(x, y, entity.createInstance(x, y, false));
	}
	
	/**
	 * ajoute une entit� � la salle.
	 * @param x - abscisse de l'entit�, en unit� une case = 256.
	 * @param y - ordonn�e de l'entit�, en unit� une case = 256.
	 * @param entity - L'entit� peinte. Elle sera clon�e puis ajout�e � la salle
	 * avec les coordonn�es indiqu�es.
	 */
	public boolean paintEntity(int x, int y, Entity entity) {
		return this.paintEntity(x, y, entity, false);
	}
	
	/**
	 * @param allPermission - {@code true} si les v�rification ne sont pas n�cessaires.
	 * @see #paintEntity(int, int, Entity)
	 */
	boolean paintEntity(int x, int y, Entity entity, boolean allPermission) {
		if (!checkAction(x, y, allPermission)) {
			return false;
		}
		Entity toPaint = entity.clone();
		toPaint.setPositioni(x, y);
		
		if (isValideCase(toPaint) || allPermission) {
			this.room.addEntityByPriority(toPaint);
			this.lastEntityPainted = toPaint;
			return true;
		}
		return false;
	}
	
	/**
	 * Donne la derni�re entit� ajout�e au niveau.
	 * @return La derni�re entit� cr��e par la m�thode
	 * {@link #paintEntity(int, int, Entity)}
	 */
	public Entity getLastEntityPainted() {
		return this.lastEntityPainted;
	}
	
	/**
	 * valide l'action effectu�e aux coordonn�es x et y. Interdit : 
	 * la bande d'une case tout autour de la salle.
	 * @param x - l'abscisse du point dont on cherche � v�rifier la validit�.
	 * @param y - l'ordonn�e du point dont on cherche � v�rifier la validit�.
	 * @param allPermission - Si {@code true}, alors on peut effectuer une action
	 * partout dans la salle (m�me dans la bande normalement interdite).
	 * @return Si l'action est possible en (x, y), retourne <code>true</code>,
	 * si elle ne l'est pas, retourne <code>false</code>.
	 */
	private boolean checkAction(int x, int y, boolean allPermission){
		return x < (this.room.getDimensions().getWidth() - (allPermission ? 0 : 1)) * Entity.CASE &&
				x > Entity.CASE - (allPermission ? Entity.CASE : 0) &&
				y < (this.room.getDimensions().getHeight() - (allPermission ? 0 : 1)) * Entity.CASE &&
				y > Entity.CASE - (allPermission ? Entity.CASE : 0);
	}

	/** Cette m�thode v�rifie si l'entit� est sur du sol ou non.
	 * @return <code>true</code> si l'entit� est sur du sol, ou
	 * <code>false</code> s'il ne l'est pas. */
	private boolean isValideCase(Entity entity){
		if (this.room != null){
			
			if (entity instanceof CaseEntity) {
				Vec2i c = entity.getCasePosition();
				if (this.room.getLandscapeAt(c.x, c.y).isEnabled() && this.room.getCaseEntityAt(c.x, c.y) == null) {
					return true;
				}
			}
			else {
				RectangleDouble bounds = entity.getBoundsf();
				
				if ((this.room.getLandscapeAt((int) bounds.xmin, (int) bounds.ymin).isEnabled()) &&
						(this.room.getLandscapeAt((int) bounds.xmax, (int) bounds.ymax).isEnabled())){
					return true;
				}
			}
		}
		
		return false;
	}
}
