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

package org.terramagnetica.game.lvldefault.IA;

import java.util.ArrayList;

import org.terramagnetica.game.lvldefault.CaseEntity;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.LandscapeTile;
import org.terramagnetica.game.lvldefault.LevelDefault;
import org.terramagnetica.game.lvldefault.Room;

import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.maths.geometric.RectangleInt;
import net.bynaryscode.util.path.Case;
import net.bynaryscode.util.path.Path;
import net.bynaryscode.util.path.SpaceGraph;
import net.bynaryscode.util.path.SpaceGraphPoint;

/** Ce graphe permet de caractériser les possibilités de déplacement d'une
 * entité dans un niveau de type {@link LevelDefault}.
 * <p>Le graphe donne, à partir de la case ou est positionnée une entité, 
 * toutes les cases sur lesquelles elle peut directement aller. De base, 
 * il renverra ainsi les quatre cases adjacentes à la case d'origine, qui
 * ne sont pas des murs. Il est possible de paramètrer le graphe pour qu'il
 * selectionne les cases disponibles de façon plus fine, detectant ainsi les
 * entités qui bloquent le passage, par exemple.*/
public class SpaceGraphLevelDefault extends SpaceGraph implements Cloneable {
	
	private GamePlayingDefault game;
	private Room room;
	
	//paramètres
	private boolean ignoreCaseEntity = true;
	private boolean ignoreMovingEntity = true;
	private RectangleInt bounds = null;
	
	public SpaceGraphLevelDefault(GamePlayingDefault game) {
		setSpace(game);
	}
	
	public SpaceGraphLevelDefault(Room room) {
		setSpace(room);
	}
	
	public void setSpace(GamePlayingDefault game) {
		if (game == null) throw new NullPointerException("game == null !");
		
		this.game = game;
		this.room = null;
	}
	
	public void setSpace(Room room) {
		if (room == null) throw new NullPointerException("room == null !");
		
		this.room = room;
		this.game = null;
	}
	
	//PARAMETRAGE
	
	public void setIgnoreCaseEntity(boolean flag) {
		this.ignoreCaseEntity = flag;
	}
	
	public boolean ignoreCaseEntity() {
		return this.ignoreCaseEntity;
	}
	
	/** @deprecated ne fonctionne pas encore
	 * @param flag */
	public void setIgnoreMovingEntity(boolean flag) {
		this.ignoreMovingEntity = flag;
	}
	
	public boolean ignoreMovingEntity() {
		return ignoreMovingEntity;
	}
	
	public void setBounds(RectangleInt bounds) {
		this.bounds = bounds;
	}
	
	//INFORMATIONS
	
	public Case getCase(int x, int y) {
		return new Case(x, y);
	}
	
	public Case getCase(Vec2i caseLocation) {
		return getCase(caseLocation.x, caseLocation.y);
	}
	
	public boolean isCaseFree(Case c) {
		return isCaseFree(getLandscapeAt(c.getLocation().x, c.getLocation().y));
	}
	
	public boolean isCaseFree(LandscapeTile l) {
		if (!l.isEnabled()) return false; //Mur
		if (!this.ignoreCaseEntity && this.getCaseEntityOn(l.getCoordonnéesCase()) != null) return false;
		if (this.bounds != null && !this.bounds.contains(l.getCoordonnéesCase())) return false;
		return true;
	}
	
	public boolean isRoomCase(int x, int y) {
		LandscapeTile[] around = getLandscapeAround(x, y);
		ArrayList<LandscapeTile> permitted = new ArrayList<LandscapeTile>(4);
		
		for (LandscapeTile l : around) {
			if (l.isEnabled()) permitted.add(l);
		}
		
		if (permitted.size() == 1) return false;//cul de sac
		if (permitted.size() == 2) {//Indétermination : nécessite des calculs supplémentaires.
			Vec2i l1 = permitted.get(0).getCoordonnéesCase();
			Vec2i l2 = permitted.get(1).getCoordonnéesCase();
			
			if (l1.x != l2.x && l1.y != l2.y) {//Cela peut être soit un coude, soit une salle
				return getLandscapeAt(l1.x, l2.y).isEnabled() && getLandscapeAt(l2.x, l1.y).isEnabled();
			}
			else {//Chemin droit
				return false;
			}
		}
		
		return true;
		
	}
	
	public boolean isRoomCase(Vec2i caseLocation) {
		return isRoomCase(caseLocation.x, caseLocation.y);
	}
	
	public boolean isRoomCase(Case caseLocation) {
		return isRoomCase(caseLocation.getLocation());
	}
	
	public ArrayList<Case> getRoomCases(Vec2i searchStart) {
		//TODO Cet algorithme existe aussi dans le PathSeeker, quasiment à l'identique -> Fusion possible des deux ?
		ArrayList<Case> result = new ArrayList<Case>();
		
		ArrayList<Case> currentStep = new ArrayList<Case>(1); currentStep.add(getCase(searchStart));
		ArrayList<Case> nextStep;
		
		//On parcourt toute la salle
		while (currentStep.size() != 0) {
			nextStep = new ArrayList<Case>(currentStep.size() + 4);
			
			for (int i = 0 ; i < currentStep.size() ; i++) {
				Case c = currentStep.get(i);
				
				//On regarde les cases adjacentes
				ArrayList<Case> cAround = getPermittedCaseAround(c.getLocation());
				for (Case cNew : cAround) {
					
					//On rajoute la case si elle n'a pas déjà été visitée.
					if (!result.contains(cNew)) {			
						
						//Si elle est dans une salle, on l'ajoute à la liste des cases trouvées
						if (isRoomCase(cNew.getLocation())) {
							int j;
							//Si la case est déjà trouvée, on met à jour les cases parentes.
							if ((j = nextStep.indexOf(cNew)) != -1) {
								nextStep.get(j).addPrevious(c);
							}
							//Sinon on ajoute juste la case.
							else {
								nextStep.add(cNew);
							}
						}
					}
				}
				
				for (Case c0 : nextStep) {
					result.add(c0);
				}
			}
			
			currentStep = nextStep;
		}
		
		return result;
	}
	
	/** Renvoie une liste de cases correspondant aux décors praticables autour du point
	 * passé en paramètres. Cette méthode ne fait pas de traitement sur les entités, elle
	 * est utilisée lors */
	private ArrayList<Case> getPermittedCaseAround(Vec2i c) {
		LandscapeTile[] lAround = getLandscapeAround(c.x, c.y);
		ArrayList<Case> cAround = new ArrayList<Case>();
		for (LandscapeTile l : lAround) {
			if (l.isEnabled()) cAround.add(getCase(l.getCoordonnéesCase()));
		}
		
		return cAround;
	}
	
	private Case toCase(SpaceGraphPoint point) {
		if (!(point instanceof Case)) throw new IllegalArgumentException("Ce #SpaceGraph s'utilise avec des #Case");
		return (Case) point;
	}

	@Override
	public Case[] getNearPoints(SpaceGraphPoint point) {
		Case parent = toCase(point);
		Vec2i pointLoc = parent.getLocation();
		LandscapeTile[] around = getLandscapeAround(pointLoc.x, pointLoc.y);
		
		ArrayList<Case> result = new ArrayList<Case>(4);
		for (LandscapeTile land : around) {
			//On ajoute la case si elle est libre
			if (isCaseFree(land)) {
				result.add(new Case(land.getCoordonnéesCase(), parent));
			}
		}
		
		return result.toArray(new Case[0]);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>Dans le cas du {@link SpaceGraphLevelDefault} on renvoie la distance au carré
	 * qui nous sépare de la destination.
	 */
	@Override
	public int getHeuristic(SpaceGraphPoint point, SpaceGraphPoint destination) {
		Vec2i case1 = toCase(point).getLocation();
		Vec2i case2 = toCase(destination).getLocation();
		
		return (int) (Math.pow(case2.x - case1.x, 2) + Math.pow(case2.y - case1.y, 2));
	}
	
	@Override
	public Path buildPath(SpaceGraphPoint end) {
		Path result = new Path();
		Case lastCase = toCase(end);
		
		//On trouve la première case du chemin.
		Case firstCase = lastCase;
		while (firstCase.hasPrevious()) {
			firstCase = (Case) firstCase.getPrevious();
		}
		
		//On joint les deux par le chemin le plus court possible.
		result.addNode(lastCase.getLocation());
		for (Case[] prevCases = lastCase.getPreviousCases() ; prevCases.length > 0 ; prevCases = lastCase.getPreviousCases()) {
			float minDistance = Float.MAX_VALUE;
			
			for (Case prevCase : prevCases) {
				if (MathUtil.getLength(prevCase.getLocation(), firstCase.getLocation()) <= minDistance) {
					lastCase = prevCase;
				}
			}
			
			result.addNodeFirst(lastCase.getLocation());
		}
		
		result.begin();
		
		return result;
	}
	
	
	private LandscapeTile[] getLandscapeAround(int x, int y) {
		if (this.game != null) {
			return this.game.getLandscapeAround(x, y);
		}
		else {
			return this.room.getLandscapeAround(x, y);
		}
	}
	
	private LandscapeTile getLandscapeAt(int x, int y) {
		if (this.game != null) {
			return this.game.getLandscapeAt(x, y);
		}
		else {
			return this.room.getLandscapeAt(x, y);
		}
	}
	
	private CaseEntity getCaseEntityOn(Vec2i c) {
		if (this.game != null) {
			return this.game.getCaseEntityAt(c);
		}
		else {
			return this.room.getCaseEntityAt(c);
		}
	}
	
	@Override
	public SpaceGraphLevelDefault clone() {
		SpaceGraphLevelDefault clone = null;
		
		try {
			clone = (SpaceGraphLevelDefault) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		clone.bounds = this.bounds == null ? null : this.bounds.clone();
		
		return clone;
	}
}
