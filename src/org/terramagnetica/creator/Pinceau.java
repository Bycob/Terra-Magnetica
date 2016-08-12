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

package org.terramagnetica.creator;

import javax.swing.Icon;

/** Un pinceau spécifie les actions à effectuer dans l'éditeur
 * en lors d'un clic de souris. Généralement un pinceau est assigné
 * à une classe d'objet spécifique. */
public abstract class Pinceau {
	
	protected String name;
	
	protected PinceauFilter filter = new PinceauFilter();
	
	public Pinceau(String name){
		this.name = name;
	}
	
	public abstract Icon getIcon();
	public abstract Class<? extends Object> getElementPaintedClass();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setFilter(PinceauFilter filter) {
		if (filter == null) filter = new PinceauFilter();
		this.filter = filter;
	}
	
	public PinceauFilter getFilter() {
		return this.filter;
	}
}
