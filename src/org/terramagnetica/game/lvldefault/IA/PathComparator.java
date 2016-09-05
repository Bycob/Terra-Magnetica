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

import java.util.Comparator;

import net.bynaryscode.util.path.Path;

public abstract class PathComparator implements Comparator<Path>, Cloneable {

	private AIMovable aiMovable;

	public PathComparator(AIMovable aI) {
		this.setAI(aI);
	}

	public AIMovable getAI() {
		return aiMovable;
	}

	public void setAI(AIMovable aI) {
		if (aI == null) throw new NullPointerException("ai == null");
		aiMovable = aI;
	}
	
	@Override
	public PathComparator clone() {
		PathComparator clone = null;
		
		try {
			clone = (PathComparator) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		return clone;
	}
}
