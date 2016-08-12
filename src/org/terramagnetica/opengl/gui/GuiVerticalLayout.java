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

package org.terramagnetica.opengl.gui;

import java.util.ArrayList;
import java.util.List;

import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class GuiVerticalLayout extends GuiLayout {
	
	private List<GuiComponent> components = new ArrayList<GuiComponent>();
	private double height;
	private double yOffset;
	private double xOffset;
	
	public GuiVerticalLayout() {
		this(0.5);
	}
	
	public GuiVerticalLayout(double height) {
		this(height, 0, 0);
	}
	
	public GuiVerticalLayout(double height, double yOffset, double xOffset) {
		this.height = height;
		this.yOffset = yOffset;
		this.xOffset = xOffset;
	}
	
	@Override
	public void add(GuiComponent c) {
		if (!this.components.contains(c) && c != null) this.components.add(c);
	}

	@Override
	public void add(GuiComponent c, String specification) {
		this.add(c);
	}
	
	@Override
	public RectangleDouble getChildBounds(GuiComponent child) {
		RectangleDouble contBounds = this.container.getBoundsGL();
		int index = this.components.indexOf(child);
		
		if (index != -1) {
			return new RectangleDouble(
					contBounds.xmin + this.xOffset,
					contBounds.ymin - (this.height + this.yOffset) * index - this.yOffset,
					contBounds.xmax - this.yOffset,
					contBounds.ymin - (this.height + this.yOffset) * (index + 1));
		}
		
		return super.getChildBounds(child);
	}
}
