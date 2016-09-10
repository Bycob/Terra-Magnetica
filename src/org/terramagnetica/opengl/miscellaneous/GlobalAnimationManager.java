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

package org.terramagnetica.opengl.miscellaneous;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère toutes les animations du jeu. Ainsi, lorque le jeu
 * se met en pause par exemple, toutes les animations s'arrêtent.
 * @author Louis JEAN
 */
public class GlobalAnimationManager {
	
	private boolean state;
	private List<Animation> list = new ArrayList<Animation>();
	
	public void add(Animation animation) {
		this.list.add(animation);
		if (state) {
			animation.start();
		}
		else {
			animation.stop();
		}
	}
	
	public boolean remove(Animation animation) {
		return this.list.remove(animation);
	}
	
	public void clear() {
		this.list.clear();
	}
	
	public void start() {
		for (Animation animation : this.list) {
			animation.start();
		}
	}
	
	public void stop() {
		for (Animation animation : this.list) {
			animation.stop();
		}
	}
	
	public void reset() {
		for (Animation animation : this.list) {
			animation.reset();
		}
	}
}
