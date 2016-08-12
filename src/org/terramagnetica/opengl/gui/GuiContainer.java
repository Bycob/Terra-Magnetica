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

import org.terramagnetica.opengl.engine.GLOrtho;
import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class GuiContainer extends GuiComponent {
	
	private GuiLayout layout;
	private Color4f backGround = new Color4f(1f, 1f, 1f, 0f);
	
	public GuiContainer() {
		GLOrtho r = theWindow.getOrtho();
		if (r != null) this.setBoundsGL(r.getBounds2D());
	}
	
	public GuiContainer(double xmin, double ymin, double xmax, double ymax) {
		this.setBoundsGL(new RectangleDouble(xmin, ymin, xmax, ymax));
	}
	
	@Override
	RectangleDouble getChildBounds(GuiComponent child) {
		if (this.layout == null) {
			RectangleDouble childBounds = child.getAbsoluteBoundsGL().clone();
			Vec2d thisCenter = this.getBoundsGL().center();
			Vec2d screenCenter = theWindow.getOrtho().getBounds2D().center();
			childBounds.translate(thisCenter.x - screenCenter.x, thisCenter.y - screenCenter.y);
			return childBounds;
		}
		else {
			return checkedBoundsGL(this.layout.getChildBounds(child));
		}
	}
	
	@Override
	public void drawComponent() {
		Painter p = Painter.instance;
		p.ensure2D();
		p.setPrimitive(Painter.Primitive.QUADS);
		p.setTexture(null);
		p.setColor(this.backGround);
		
		RectangleDouble b = this.getBoundsGL();
		
		GLUtil.drawQuad2D(b.xmin, b.ymin, b.xmax, b.ymax, p);
	}
	
	public void setLayout(GuiLayout layout) {
		this.layout = layout;
		
		if (this.layout != null) {
			this.layout.container = this;
			for (GuiComponent child : this.children) {
				this.layout.add(child);
			}
		}
	}
	
	public GuiLayout getLayout() {
		return this.layout;
	}
	
	public void setBackground(Color4f color) {
		this.backGround = color.clone();
	}
	
	@Override
	public void add(GuiComponent c) {
		super.add(c);
		if (this.layout != null) {
			this.layout.add(c);
			c.updateBounds();
		}
	}
	
	public void add(GuiComponent c, String specification) {
		super.add(c);
		if (this.layout != null) {
			this.layout.add(c, specification);
			c.updateBounds();
		}
	}
}
