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

package org.terramagnetica.creator;

public class PinceauFilter {

	protected int levelFilter = 0;
	
	public PinceauFilter() {
		super();
	}

	public PinceauFilter(int levelFilter) {
		this.setLevelFilter(levelFilter);
	}

	public int getLevelFilter() {
		return levelFilter;
	}

	public void setLevelFilter(int levelFilter) {
		this.levelFilter = levelFilter;
	}
	
	/** Indique si un pinceau possédant ce filtre est affiché par un composant
	 * possédant le filtre en paramètres. */
	public boolean matches(PinceauFilter applied) {
		if (this.levelFilter != 0 && this.levelFilter != applied.levelFilter) {
			return false;
		}
		
		return true;
	}
}
