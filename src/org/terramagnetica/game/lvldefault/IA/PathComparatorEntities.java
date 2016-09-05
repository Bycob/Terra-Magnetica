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
import java.util.Comparator;

import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.IA.SensorPathScanner.EntityInfo;
import org.terramagnetica.game.lvldefault.IA.SensorPathScanner.PathScannerInfo;

import net.bynaryscode.util.Util;
import net.bynaryscode.util.path.Path;

/** Ce comparateur détermine si un chemin a plus de chance d'être
 * choisi qu'un autre en fonction des entités qu'il y a dessus. */
public class PathComparatorEntities extends PathComparator {
	
	private Comparator<Entity> entityComparator;
	
	public PathComparatorEntities(AIMovable ai) {
		super(ai);
		this.entityComparator = new DefaultEntityComparator();
	}
	
	protected SensorPathScanner getScanner() {
		return this.getAI().getSensor(SensorPathScanner.class);
	}
	
	@Override
	public int compare(Path o1, Path o2) {
		PathScannerInfo info1 = getScanner().checkPath(o1);
		PathScannerInfo info2 = getScanner().checkPath(o2);
		
		Entity ent1 = getMaxEntity(info1);
		Entity ent2 = getMaxEntity(info2);
		
		//On va sur le chemin ou l'entité est LA MOINS dangereuse (le comparator classe les entités par danger croissants.
		int compare = ent1 == null ? 1 : (ent2 == null ? -1 : - this.entityComparator.compare(ent1, ent2));
		//Si les deux chemins sont identiques on privilégie le chemin actuel.
		Path aiPath = this.getAI().getPath();
		if (compare == 0 && aiPath != null) {
			return o1.countSameNodes(aiPath) - o2.countSameNodes(aiPath);
		}
		
		return compare;
	}
	
	public Entity getMaxEntity(PathScannerInfo info) {
		Entity result = null;
		
		for (EntityInfo entInfo : info.getFoundEntities()) {
			if (result == null || this.entityComparator.compare(result, entInfo.getEntity()) < 0) {
				result = entInfo.getEntity();
			}
		}
		
		ArrayList<Entity> maxOfEachPath = new ArrayList<Entity>();
		for (PathScannerInfo child : info.getChildren()) {
			maxOfEachPath.add(getMaxEntity(child));
		}
		Entity min = Util.findMin(maxOfEachPath, this.entityComparator);
		
		if (this.entityComparator.compare(result, min) < 0) {
			result = min;
		}
		
		return result;
	}

	public void setEntityComparator(EntityClassSelector entityComparator) {
		if (entityComparator == null) throw new NullPointerException("entityComparator == null !");
		this.entityComparator = entityComparator;
	}
	
	public class DefaultEntityComparator implements Comparator<Entity> {
		
		@Override
		public int compare(Entity e1, Entity e2) {
			return 0;
		}
	}
}
