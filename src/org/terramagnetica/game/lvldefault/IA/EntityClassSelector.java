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
import java.util.HashMap;

import org.terramagnetica.game.lvldefault.Entity;

import net.bynaryscode.util.Predicate;

public class EntityClassSelector implements Predicate<Entity>, Comparator<Entity> {
	
	private HashMap<Class<? extends Entity>, Float> weightMap = new HashMap<Class<? extends Entity>, Float>();
	
	public EntityClassSelector() {
		
	}
	
	public void setWeight(Class<? extends Entity> clazz, float weight) {
		if (clazz == null) throw new NullPointerException("clazz == null");
		this.weightMap.put(clazz, weight);
	}
	
	public float getWeight(Entity e) {
		return this.weightMap.getOrDefault(e.getClass(), 0f);
	}
	
	@Override
	public boolean test(Entity t) {
		return getWeight(t) != 0;
	}
	
	@Override
	public int compare(Entity ent1, Entity ent2) {
		if (ent1 == null) return -1;
		if (ent2 == null) return 1;
		
		float w1 = getWeight(ent1);
		float w2 = getWeight(ent2);
		
		return (int) Math.signum(w1 - w2);
	}
}
