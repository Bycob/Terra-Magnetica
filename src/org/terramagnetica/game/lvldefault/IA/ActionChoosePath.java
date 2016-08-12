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
import java.util.Random;

import org.terramagnetica.game.lvldefault.EntityMoving;

import net.bynaryscode.util.RandomPool;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.path.Path;

public class ActionChoosePath extends Action {
	
	private PathComparator comparator;
	private PathSeeker pathSeeker = null;
	
	public ActionChoosePath() {
		this(null);
	}
	
	public ActionChoosePath(PathComparator comparator) {
		setComparator(comparator);
	}
	
	/** Définit le comparateur dont on se sert pour déterminer quels sont
	 * le ou les chemins les plus profitables. */
	public void setComparator(PathComparator comparator) {
		this.comparator = comparator;
	}
	
	/** Définit le {@link PathSeeker} qui fournit les différents chemins entre
	 * lesquels il faut choisir. S'il est nul, alors c'est le PathSeeker par défaut
	 * qui sera utilisé.
	 * @see AIMovable */
	public void setPathSeeker(PathSeeker pathSeeker) {
		this.pathSeeker = pathSeeker;
	}
	
	@Override
	public void execute(AIBase ai) {
		//Initialisation des variables nécessaires
		AIMovable aiMove = ai.cast(AIMovable.class);
		EntityMoving ent = aiMove.getEntity();
		PathSeeker pathSeeker = this.pathSeeker == null ? aiMove.getPathSeeker() : this.pathSeeker;
		PathComparator comparator = this.comparator == null ? new DefaultComparator(aiMove) : this.comparator;
		
		//Liaison
		if (pathSeeker == null) {
			aiMove.setPath(null); return;
		}
		else {
			aiMove.bindPathSeeker(pathSeeker);
		}
		comparator.setAI(aiMove);
		
		//Obtention des chemins disponibles
		Vec2i start = ent.getCoordonnéesCase();
		Path[] paths = pathSeeker.seekPaths(start);
		
		if (paths.length == 0) {
			aiMove.setPath(null); return;
		}
		
		//Détermination des meilleurs chemins
		ArrayList<Path> choiceList = new ArrayList<Path>();
		if (aiMove.getPath() != null) {
			//Le choix par défaut est le chemin actuel de l'entité, s'il est défini.
			choiceList.add(aiMove.getPath());
		}
		else {
			choiceList.add(paths[0]);
		}
		
		for (int i = 1 ; i < paths.length ; i++) {
			int comparison = comparator.compare(choiceList.get(0), paths[i]);
			
			if (comparison == 0) {
				choiceList.add(paths[i]);
			}
			else if (comparison < 0) {
				choiceList.clear();
				choiceList.add(paths[i]);
			}
		}
		
		//Selection aléatoire parmi les meilleurs chemins
		Random rand = RandomPool.getDefault().getRandom();
		aiMove.setPath(choiceList.get(rand.nextInt(choiceList.size())));
	}
	
	@Override
	public ActionChoosePath clone() {
		ActionChoosePath clone = (ActionChoosePath) super.clone();
		
		clone.pathSeeker = this.pathSeeker == null ? null : this.pathSeeker.clone();
		
		return clone;
	}
	
	/** Le comparateur par défaut considère que tous les chemins sont égaux. */
	private class DefaultComparator extends PathComparator {

		public DefaultComparator(AIMovable aI) {
			super(aI);
		}

		@Override
		public int compare(Path o1, Path o2) {
			return 0;
		}
	}
}
