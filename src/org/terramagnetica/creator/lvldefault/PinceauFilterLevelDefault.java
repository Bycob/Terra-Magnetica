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

import org.terramagnetica.creator.PinceauFilter;
import org.terramagnetica.game.lvldefault.DecorType;

public class PinceauFilterLevelDefault extends PinceauFilter {

	protected DecorType decorFilter = null;
	
	public PinceauFilterLevelDefault() {
		super();
	}
	
	/** Construit un filtre de pinceau.
	 * @param levelFilter Le niveau dans lequel ce pinceau est utilisable. 0 si le
	 * pinceau est utilisable dans tous les niveaux. */
	public PinceauFilterLevelDefault(DecorType decorFilter, int levelFilter) {
		super(levelFilter);
		setDecorFilter(decorFilter);
	}
	
	public DecorType getDecorFilter() {
		return decorFilter;
	}

	public void setDecorFilter(DecorType decorFilter) {
		this.decorFilter = decorFilter;
	}
	
	@Override
	public boolean matches(PinceauFilter applied) {
		if (applied instanceof PinceauFilterLevelDefault) {
			PinceauFilterLevelDefault applied2 = (PinceauFilterLevelDefault) applied;
			if (this.decorFilter != null && applied2.decorFilter != this.decorFilter) {
				return false;
			}
		}
		
		return super.matches(applied);
	}
}
