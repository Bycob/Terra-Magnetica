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

package org.terramagnetica.opengl.gui;

import java.util.ArrayList;
import java.util.List;

import net.bynaryscode.util.maths.geometric.RectangleDouble;

/**
 * Le composant ajouté à ce layout prend toute la place 
 * verticalement, et garde ses limites normales horizontalement.
 * @author Louis JEAN
 *
 */
public class GuiLayout002 extends GuiLayout {
	
	private List<GuiComponent> component = new ArrayList<GuiComponent>();
	
	private double topOffset;
	private double bottomOffset;
	
	/**
	 * Le composant ajouté à ce layout prend toute la place 
	 * verticalement, et garde ses limites normales horizontalement.
	 */
	public GuiLayout002() {
		
	}
	
	@Override
	public void add(GuiComponent c) {
		if (c != null) {
			this.component.add(c);
		}
	}

	@Override
	public void add(GuiComponent c, String specification) {
		this.add(c);
	}

	@Override
	public RectangleDouble getChildBounds(GuiComponent component) {
		if (this.component.contains(component)) {
			RectangleDouble contBounds = this.container.getBoundsGL();
			RectangleDouble compBounds = component.getAbsoluteBoundsGL().clone();
			compBounds.ymin = contBounds.ymin;
			compBounds.ymax = contBounds.ymax;
			return compBounds;
		}
		else {
			return super.getChildBounds(component);
		}
	}

}
