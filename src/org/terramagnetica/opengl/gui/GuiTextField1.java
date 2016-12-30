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

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.gui.GameWindow;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.engine.Viewport;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec2i;

public class GuiTextField1 extends GuiAbstractTextField {
	
	private Vec2d textBegin;
	
	private Color4f color = new Color4f(1f, 1f, 1f);
	private Color4f textColor = new Color4f(255, 234, 0);
	
	private int preferedFontSize = 20;
	private FontSizeManager fm = new FontSizeRelativeToHeight(this.theTextPainter);
	
	public GuiTextField1() {
		init(new RectangleDouble(0, 0, 0, 0));
	}
	
	public GuiTextField1(double x1, double y1, double x2, double y2) {
		init(new RectangleDouble(x1, y1, x2, y2));
	}
	
	public void init(RectangleDouble coordGL) {
		this.setBoundsGL(coordGL);
		
		this.cursor = new Cursor();
		
		input.accept(new char[] {13, '\n'}, false);//n'accepte ni le retour à la ligne, ni le retour chariot.
	}
	
	@Override
	public void setBoundsGL(RectangleDouble bounds) {
		super.setBoundsGL(bounds);
		
		recalculateTextStartPoint();
	}
	
	private void recalculateTextStartPoint() {
		RectangleDouble bounds = getBoundsGL();
		double marge = 0.03 * bounds.getWidth();
		this.textBegin = new Vec2d(bounds.xmin + marge, bounds.center().y);
	}
	
	@Override
	public void eventMouse(MouseEvent e) {
		int button = e.getButton();
		boolean state = e.getState();
		Vec2i mouse = new Vec2i(e.getX(), e.getY());
		Vec2d mouseGL = new Vec2d(
				theWindow.getXOnGLOrtho(mouse.x),
				theWindow.getYOnGLOrtho(mouse.y));
		
		if (button == BUTTON_LEFT && state) {
			if (this.getBoundsDisp().contains(mouse)) {
				if (!isFocused()) {
					setFocused(true);
				}
				
				String strBefore;
				int fontSize = fm.calculFontSize(this.getBoundsDisp(), text, preferedFontSize);
				boolean done = false;
				for (int i = 0 ; i < text.length() ; i++) {
					strBefore = text.substring(0, i);
					if (mouseGL.x <= textBegin.x + this.theTextPainter.widthOnGL(strBefore, fontSize)) {
						this.cursor.setCursorPlace(i);
						done = true;
						break;
					}
				}
				
				if (!done) {
					this.cursor.setCursorPlace(text.length());
				}
			}
			else {
				setFocused(false);
			}
		}
	}

	@Override
	protected void drawComponent(Painter painter) {
		painter.set2DConfig();
		painter.setTexture(null);
		
		RectangleDouble coordGL = this.getBoundsGL();

		//le champ de texte
		TextureQuad tex = TexturesLoader.getQuad(GameRessources.ID_TEXTFIELD1);
		painter.setColor(this.color);
		tex.drawQuad2D(coordGL.xmin, coordGL.ymin, coordGL.xmax, coordGL.ymax, true, painter);
		
		double marge = 0.03 * coordGL.getWidth();
		
		//le texte
		this.theTextPainter.setColor(this.textColor);

		recalculateTextStartPoint();
		int fontSize = this.fm.calculFontSize(this.getBoundsDisp(), text, preferedFontSize);
		
		RectangleDouble viewport = this.getBoundsGL();
		viewport.xmin += marge; viewport.xmax -= marge;
		painter.setViewport(new Viewport(viewport));
		
		this.theTextPainter.drawString2DBeginAt(text, textBegin.x, textBegin.y, fontSize);
		
		painter.setViewport(null);
		
		//le curseur
		if (isFocused()) {
			this.cursor.drawCursor(painter);
		}
	}
	
	@Override
	public void setColor(Color4f color) {
		this.color = color.clone();
	}
	
	@Override
	public Color4f getColor() {
		return this.color.clone();
	}
	
	public void setTextColor(Color4f textColor) {
		this.textColor = textColor.clone();
	}
	
	public Color4f getTextColor() {
		return this.textColor.clone();
	}
	
	/** @see GLFWTextInput#accept(char, boolean) */
	public void inputAccept(char character, boolean accepted) {
		input.accept(character, accepted);
	}
	
	/** @see GLFWTextInput#accept(char[], boolean) */
	public void inputAccept(char[] characters, boolean accepted) {
		input.accept(characters, accepted);
	}
	
	private class Cursor extends GuiAbstractTextField.GuiCursor {
		
		@Override
		public void drawCursor(Painter painter) {
			long time = GameWindow.getInstance().getTime();
			
			if ((time / 500) % 2 == 1 && time - getLastInput() > 500) {
				return;
			}
			
			RectangleDouble coordGL = getBoundsGL();
			
			double cursorX = getX(),
				height = coordGL.getHeight(),
				cursorY1 = coordGL.ymin - 0.1* height,
				cursorY2 = coordGL.ymax + 0.1* height;
			
			painter.setTexture(null);
			
			painter.setColor(textColor);
			painter.setPrimitive(Painter.Primitive.LINES);
			
			painter.addVertex(cursorX, cursorY1);
			painter.addVertex(cursorX, cursorY2);
			
		}
		
		@Override
		public void moveRight() {
			super.moveRight();
			this.putCursorInBounds();
		}
		
		@Override
		public void moveLeft() {
			super.moveLeft();
			this.putCursorInBounds();
		}
		
		@Override
		public void setCursorPlace(int place) {
			super.setCursorPlace(place);
			this.putCursorInBounds();
		}
		
		public double getX() {
			String textBefore = text.substring(0, this.getCursorPlace());
			int fontSize = fm.calculFontSize(getBoundsDisp(), text, preferedFontSize);
			double widthBefore = GuiTextField1.this.theTextPainter.widthOnGL(textBefore, fontSize);
			return textBegin.x + widthBefore;
		}
		
		private void putCursorInBounds() {
			int fontSize = fm.calculFontSize(getBoundsDisp(), text, preferedFontSize);
			String textBefore = text.substring(0, this.getCursorPlace());
			double widthBefore = GuiTextField1.this.theTextPainter.widthOnGL(textBefore, fontSize);
			
			double marge = getBoundsGL().getWidth() * 0.03;
			double xmin = getBoundsGL().xmin + marge;
			double xmax = getBoundsGL().xmax - marge;
			double cursorX = textBegin.x + widthBefore;
			
			if (cursorX < xmin) {
				textBegin.x = xmin + 10 * marge - widthBefore;
				if (textBegin.x > xmin) {
					textBegin.x = xmin;
				}
			}
			if (cursorX > xmax) {
				textBegin.x = xmax - widthBefore;
			}
		}
	}
}
