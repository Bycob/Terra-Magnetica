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

package org.terramagnetica.game.gui;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.gui.FontSizeManager;
import org.terramagnetica.opengl.gui.FontSizeRelativeToRectangle;
import org.terramagnetica.opengl.gui.GuiAbstractButton;
import org.terramagnetica.ressources.SaveData;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.RectangleInt;

public class GuiButtonLoadGame extends GuiAbstractButton {

	private SaveData data;
	boolean selected = false;
	
	public GuiButtonLoadGame(SaveData data) {
		this(data, new RectangleDouble(0, 0, 0.2, -0.4));
	}
	
	public GuiButtonLoadGame(SaveData data, RectangleDouble bounds) {
		this.data = data;
		this.setBoundsGL(bounds);
	}
	
	@Override
	public void drawComponent() {
		//initialisation.
		Painter p = Painter.instance;
		p.ensure2D();
		p.setPrimitive(Painter.Primitive.QUADS);
		
		RectangleDouble b = getBoundsGL();
		FontSizeManager fsm = new FontSizeRelativeToRectangle(this.theTextPainter);
		Color4f textColor = GuiConstants.TEXT_COLOR_DEFAULT.clone();
		
		//arrière plan.
		if (this.selected) {
			if (this.getState() == PRESSED) {
				p.setColor(new Color4f(183, 163, 0, 31));
			}
			else {
				p.setColor(new Color4f(255, 255, 0, 31));
			}
		}
		else {
			p.setColor(new Color4f(0, 0, 0, 31));
		}
		p.setTexture(TexturesLoader.get("gui/game/guiGame.png.loadButton"));
		GLUtil.drawQuad2D(b, p);
		
		//Nom du joueur : rectangle
		p.setTexture(null);
		RectangleDouble bName = b.clone();
		double marge = 0.03;
		bName.xmin += marge;
		bName.ymin -= marge;
		bName.ymax += b.getHeight() * 2d / 3d - marge;
		Color4f color1 = new Color4f(255, 100, 0, 127);
		Color4f color2 = color1.clone(); color2.setAlphaf(0);
		GLUtil.drawGradientPaintedRectangle(bName, color1, color2, false, p);
		
		//Nom du joueur : texte
		RectangleInt bNameDisp = boundsGLToDisp(bName);
		int fontSize = fsm.calculFontSize(bNameDisp, this.data.getName(), 20);
		this.theTextPainter.setColor(textColor);
		this.theTextPainter.drawString2DBeginAt(this.data.getName(), bName.xmin + marge, bName.center().y, fontSize);
		
		//Date de la dernière ouverture
		RectangleDouble bDate = bName.clone();
		bDate.translate(0, - marge - bName.getHeight());
		RectangleInt bDateDisp = boundsGLToDisp(bDate);
		String date = (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(new Date(this.data.getLastModified()));
		fontSize = fsm.calculFontSize(bDateDisp, date, 18);
		
		this.theTextPainter.setColor(textColor);
		this.theTextPainter.drawString2DBeginAt(date, bDate.xmin, bDate.center().y, fontSize);
	}
	
	public SaveData getData() {
		return this.data;
	}
	
	public void setData(SaveData data) {
		this.data = data;
	}
}
