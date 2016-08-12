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

package org.terramagnetica.game.lvldefault.IA;

import java.util.ArrayList;

import org.terramagnetica.game.lvldefault.GamePlayingDefault;

import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.path.Case;
import net.bynaryscode.util.path.Path;

public class PathSeeker {
	
	private static final int DEFAULT_CASE_COUNT = 15;
	
	private GamePlayingDefault game;
	private SpaceGraphLevelDefault spaceGraph;
	
	private boolean acceptDeadEnd = false;
	private int caseCount = DEFAULT_CASE_COUNT;
	
	public PathSeeker(GamePlayingDefault game) {
		this.setGame(game);
	}
	
	public void setGame(GamePlayingDefault game) {
		if (game == null) throw new NullPointerException("no game no life");
		this.game = game;
		
		if (this.spaceGraph == null) this.spaceGraph = new SpaceGraphLevelDefault(game);
		this.spaceGraph.setSpace(game);
	}
	
	public GamePlayingDefault getGame() {
		return this.game;
	}
	
	/** Permet d'obtenir le {@link SpaceGraphLevelDefault} utilisé par le
	 * {@link PathSeeker}, pour le paramètrer. */
	public SpaceGraphLevelDefault getSpaceGraph() {
		if (this.game == null) throw new NullPointerException("no game defined -> no spacegraph defined");
		return this.spaceGraph;
	}
	
	public void setAcceptDeadEnd(boolean on) {
		this.acceptDeadEnd = on;
	}
	
	public Path[] seekPaths(Vec2i from) {
		return lookForPath(new Case(from), this.caseCount);
	}
	
	/** Méthode récursive opérant la recherche de chemin en calculant tous les
	 * embranchements jusqu'à avoir parcouru le nombre de cases indiquées en paramètres.
	 * Les chemins s'arrêtent toujours à des embranchements. */
	private Path[] lookForPath(Case start, int remainingCases) {
		ArrayList<Case> pathFound = new ArrayList<Case>(4);
		
		//ETAPE I : Recherche des différents chemins existants.
		
		//Recherche des différents chemins en sortie de salle.
		if (this.spaceGraph.isRoomCase(start)) {
			ArrayList<Case> currentStep = new ArrayList<Case>(1); currentStep.add(start);
			ArrayList<Case> nextStep;
			
			//On parcourt toute la salle
			while (currentStep.size() != 0) {
				nextStep = new ArrayList<Case>(currentStep.size() + 4);
				
				for (int i = 0 ; i < currentStep.size() ; i++) {
					Case c = currentStep.get(i);
					
					//On regarde les cases adjacentes
					Case[] around = this.spaceGraph.getNearPoints(c);
					for (Case cNew : around) {
						
						//On rajoute la case si elle n'a pas déjà été visitée.
						if (!currentStep.contains(cNew) && !c.hasPrevious(cNew)) {			
							
							//Si elle est dans une salle, on l'ajoute à la liste des cases trouvées
							if (this.spaceGraph.isRoomCase(cNew.getLocation())) {
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
							//Sinon on l'ajoute à la liste des chemins trouvés.
							else {
								pathFound.add(cNew);
							}
						}
					}
				}
				
				currentStep = nextStep;
			}
		}
		//Si on est dans un chemin, on a directement les deux possibilités de 
		else {
			Util.addAll(this.spaceGraph.getNearPoints(start), pathFound, false);
		}
		
		//ETAPE II : on reconstruit le chemin à partir de la case qui le commence.
		ArrayList<Path> result = new ArrayList<Path>(pathFound.size());
		
		for (Case c : pathFound) {
			//On retrouve le début du chemin 
			//Dans le cas d'une salle, c'est le bout de chemin entre la case de départ et la case de sortie de la salle
			Path path = this.spaceGraph.buildPath(c);
			path.goToEnd();
			
			//On ajoute la fin : on poursuit le chemin trouvé jusqu'à tomber dans une salle (et donc plusieurs embranchements possibles).
			Case cCurrent = c;
			boolean deadEnd = true;
			while (!this.spaceGraph.isRoomCase(cCurrent.getLocation())) {
				
				Case[] around = this.spaceGraph.getNearPoints(cCurrent);
				deadEnd = true;
				
				for (Case cNext : around) {
					
					if (!cCurrent.hasPrevious(cNext)) {
						path.addNode(cNext.getLocation());
						cCurrent = cNext;
						deadEnd = false;
						break;
					}
				}
				
				//détection des culs de sacs
				if (deadEnd) {
					break;
				}
			}
			
			//On ajoute les embranchements
			int remaining = remainingCases - path.getLength();
			Case lastCase = new Case(path.getLast().getPoint().asInteger());
			
			if (remaining > 0) {
				Path[] fork = lookForPath(lastCase, remaining);
				path.getLast().setCrossroads(fork);
			}
			
			//Test : si les culs de sac ne sont inclus dans la recherche on les enlève.
			if (!deadEnd || this.acceptDeadEnd) {
				path.begin();
				result.add(path);
			}
		}
		
		return result.toArray(new Path[0]);
	}
	
	@Override
	public PathSeeker clone() {
		PathSeeker clone = null;
		
		try {
			clone = (PathSeeker) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		clone.spaceGraph = this.spaceGraph.clone();
		
		return clone;
	}
}
