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

import org.terramagnetica.game.Page;
import org.terramagnetica.game.gui.GuiPage;

import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.Rectangle;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class GuiDialogMessage extends GuiDialog {
	
	private String message;
	
	private GuiScrollPanel scrollPan;
	private GuiButtonText1 buttonOK;
	
	public GuiDialogMessage() {
		this("");
	}
	
	public GuiDialogMessage(Rectangle bounds) {
		this(bounds, "");
	}
	
	public GuiDialogMessage(String message) {
		this(GuiWindow.getInstance().getOrtho().getBounds2D(), message);
	}
	
	public GuiDialogMessage(Rectangle bounds, String message) {
		this.setBoundsGL(bounds.asDouble());
		this.message = message;
		
		GuiPage textPan = new GuiPage();
		Page contents = new Page(this.message);
		textPan.setPage(contents);
		
		this.scrollPan = new GuiScrollPanel(textPan);
		this.scrollPan.setAutoWrap(true);
		this.scrollPan.fillBackground(false);
		
		this.buttonOK = new GuiButtonText1("OK");
		
		this.add(this.scrollPan);
		this.add(this.buttonOK);
	}
	
	@Override
	RectangleDouble getChildBounds(GuiComponent child) {
		final double marge = 0.03;
		final double buttonHeight = 0.15;
		final double buttonWidth = 0.5;
		
		if (child == this.scrollPan) {
			RectangleDouble b = Util.margeRectangleDouble(this.getBoundsGL().clone(), 2 * marge);
			b.ymax += buttonHeight + marge;
			return b;
		}
		if (child == buttonOK) {
			RectangleDouble b = new RectangleDouble();
			RectangleDouble thisBounds = this.getBoundsGL();
			b.xmin = thisBounds.center().x - buttonWidth / 2;
			b.xmax = b.xmin + buttonWidth;
			b.ymin = thisBounds.ymax + 2 * marge + buttonHeight;
			b.ymax = b.ymin - buttonHeight;
			return b;
		}
		
		return super.getChildBounds(child);
	}
	
	@Override
	public GuiActionEvent processLogic() {
		if (this.buttonOK.processLogic() == GuiActionEvent.CLICK_EVENT) {
			this.close();
		}
		
		return GuiActionEvent.NULL_EVENT;
	}
}
