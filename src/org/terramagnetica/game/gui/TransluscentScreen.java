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

package org.terramagnetica.game.gui;

import org.terramagnetica.opengl.engine.GLOrtho;
import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.gui.GuiActionEvent;
import org.terramagnetica.opengl.gui.GuiComponent;

import net.bynaryscode.util.Color4f;

public class TransluscentScreen extends GuiComponent {
	
	@Override
	protected void drawComponent() {
		Painter painter = Painter.instance;
		painter.ensure2D();
		painter.setColor(new Color4f(0f, 0f, 0f, 0.5f));
		painter.setTexture(null);
		
		GLOrtho r = theWindow.getOrtho();
		GLUtil.drawQuad2D(r.left, r.top, r.right, r.bottom, painter);
	}

	@Override
	public GuiActionEvent processLogic() {
		for (GuiComponent component : this.children) {
			component.processLogic();
		}
		return GuiActionEvent.NULL_EVENT;
	}
}
