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

import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.TextureQuad;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.Vec2d;

public class GuiLabel extends GuiComponent {
	
	private int mode;
	
	public static final int TEXT = 0;
	public static final int IMAGE = 1;
	public static final int NULL = 2;
	private static final int[] availableModes = {TEXT, IMAGE};
	
	private Color4f color = new Color4f(1f, 1f, 1f);
	
	private String text = "";
	private int fontSize = 20;
	private boolean textInitialized = false;
	private Vec2d centerText = null;
	
	private TextureQuad image = null;
	
	public GuiLabel() {
		mode = NULL;
	}
	
	public GuiLabel(String text, int fontSize, double x, double y) {
		mode = TEXT;
		this.text = text;
		this.centerText = new Vec2d(x, y);
		this.setFontSize(fontSize);
		this.textInitialized = true;
	}
	
	public GuiLabel(TextureQuad image, double x, double y) {
		mode = IMAGE;
	}
	
	@Override
	public void drawComponent(Painter painter) {
		switch (this.mode) {
		case TEXT :
			if (this.textInitialized) {
				this.updateCenterText();
				
				this.theTextPainter.setColor(color);
				this.theTextPainter.drawCenteredString2D(this.text, this.centerText.x, this.centerText.y, this.fontSize);
			}
			
			break;
		case IMAGE :
			
			break;
		default : break;
		}
	}
	
	public void setText(String text) {
		this.text = text;
		this.setFontSize(this.fontSize);
	}
	
	public String getText() {
		return text;
	}
	
	public void setImage(TextureQuad image) {
		
	}
	
	public TextureQuad getImage() {
		return image.clone();
	}
	
	public void setFontSize(int fontSize) {
		double width = this.theTextPainter.widthOnGL(this.text, fontSize);
		double height = this.theTextPainter.heightOnGL(fontSize);
		
		this.setBoundsGL(RectangleDouble.createRectangleFromCenter(centerText.x, centerText.y, width, height));
		this.fontSize = fontSize;
	}
	
	public int getFontSize() {
		return fontSize;
	}
	
	@Override
	public Color4f getColor() {
		return color.clone();
	}

	@Override
	public void setColor(Color4f color) {
		this.color = color.clone();
	}
	
	@Override
	public GuiActionEvent processLogic() {
		return GuiActionEvent.NULL_EVENT;
	}
	
	protected void updateCenterText() {
		this.centerText = getBoundsGL().center();
	}
}
