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

import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class GuiBorderLayout extends GuiLayout {
	
	private static final double MAX_HEIGHT = Double.MAX_VALUE;
	
	public static final String TOP = "top";
	public static final String BOTTOM = "bottom";
	public static final String CENTER = "center";
	
	private GuiComponent top;
	private GuiComponent bottom;
	private GuiComponent center;
	
	private double borderHeight = 0.5;
	
	private double topOffset = 0.0;
	private double bottomOffset = 0.0;
	
	public GuiBorderLayout() {}
	
	public GuiBorderLayout(double borderHeight) {
		this.borderHeight = borderHeight;
	}
	
	@Override
	public void add(GuiComponent c) {
		this.add(c, CENTER);
	}

	@Override
	public void add(GuiComponent c, String specification) {
		if (CENTER.equals(specification)) {
			this.center = c;
		}
		else if (BOTTOM.equals(specification)) {
			this.bottom = c;
		}
		else if (TOP.equals(specification)) {
			this.top = c;
		}
		
		this.updateOffset();
		if (this.container != null) this.container.updateBounds();
	}
	
	public void setBorderHeight(double height) {
		this.borderHeight = MathUtil.valueInRange_d(height, 0, MAX_HEIGHT);
		this.updateOffset();
		if (this.container != null) {
			this.container.updateBounds();
		}
	}
	
	public double getBorderHeight() {
		return this.borderHeight;
	}
	
	private void updateOffset() {
		this.topOffset = this.top == null ? 0 : this.borderHeight;
		this.bottomOffset = this.bottom == null ? 0 : this.borderHeight;
	}

	@Override
	public RectangleDouble getChildBounds(GuiComponent component) {
		RectangleDouble contBounds = this.container.getBoundsGL().clone();
		
		if (component == this.center) {
			return new RectangleDouble(
					contBounds.xmin,
					contBounds.ymin - this.topOffset,
					contBounds.xmax,
					contBounds.ymax + this.bottomOffset);
		}
		else if (component == this.top) {
			return new RectangleDouble(
					contBounds.xmin, contBounds.ymin, contBounds.xmax, contBounds.ymin - this.topOffset);
		}
		else if (component == this.bottom) {
			return new RectangleDouble(
					contBounds.xmin, contBounds.ymax + this.bottomOffset, contBounds.xmax, contBounds.ymax);
		}
		else {
			return super.getChildBounds(component);
		}
	}

}
