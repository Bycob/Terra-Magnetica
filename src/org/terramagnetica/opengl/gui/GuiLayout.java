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

import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public abstract class GuiLayout {
	
	protected GuiContainer container;
	
	public GuiLayout() {
		
	}
	
	public abstract void add(GuiComponent c);
	public abstract void add(GuiComponent c, String specification);
	
	public RectangleDouble getChildBounds(GuiComponent component) {
		RectangleDouble contBounds = this.container.getBoundsGL().clone();
		RectangleDouble childBounds = component.getAbsoluteBoundsGL().clone();
		Vec2d contCenter = contBounds.center();
		Vec2d screenCenter = GuiWindow.getInstance().getOrtho().getBounds2D().center();
		childBounds.translate(contCenter.x - screenCenter.x, contCenter.y - screenCenter.y);
		return childBounds;
	}
}
