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

import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Painter.Primitive;
import org.terramagnetica.opengl.gui.GuiAbstractButton;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class GuiButtonBonusLevel extends GuiAbstractButton {
	
	public static final double STANDART_WIDTH = 1, STANDART_HEIGHT = 1;
	
	private String texID = "";
	private String fileName = "";
	private String levelName = "";
	
	public GuiButtonBonusLevel(String texID, String fileName, String levelName) {
		this.texID = texID;
		this.fileName = fileName;
		this.levelName = levelName;
		
		this.setWidthGL(1); this.setHeightGL(1);
	}
	
	@Override
	public void drawComponent() {
		Color4f componentColor = new Color4f(196, 196, 196);
		
		switch (this.getState()) {
		case NORMAL :
			break;
		case MOUSE_ON :
			componentColor = new Color4f(255, 255, 255);
			break;
		case PRESSED :
			componentColor = new Color4f(100, 100, 100);
			break;
		}
		
		Painter p = Painter.instance;
		p.ensure2D();
		p.setPrimitive(Primitive.QUADS);
		p.setColor(componentColor);
		p.setTexture(TexturesLoader.get(this.texID));
		
		GLUtil.drawQuad2D(this.getBoundsGL(), p);
		
		if (this.getState() == MOUSE_ON) {
			//Affichage du nom du niveau
			final int fontSize = 16;
			
			//1-Cadre
			double width = this.theTextPainter.widthOnGL(this.getLevelName(), fontSize);
			double height = this.theTextPainter.heightOnGL(fontSize);
			Vec2d thisCenter = this.getBoundsGL().center();
			
			p.setTexture(null);
			p.setColor(new Color4f(0, 0, 0, 64));
			
			GLUtil.drawQuad2D(RectangleDouble.createRectangleFromCenter(thisCenter.x, thisCenter.y, width + 0.1, height + 0.1), p);
			
			//2-Texte
			this.theTextPainter.setColor(GuiConstants.TEXT_COLOR_DEFAULT);
			this.theTextPainter.drawCenteredString2D(this.getLevelName(), thisCenter.x, thisCenter.y, fontSize);
		}
	}
	
	public void setTexture(String texID) {
		this.texID = texID;
	}
	
	/** Donne le nom du fichier qui contient le niveau bonus. Le nom retourné
	 * est le simple nom du fichier (extension comprise), et non son chemin
	 * d'accès. Les fichiers de niveau bonus se trouvent dans le dossier
	 * {@code "niveaux/bonus/"}. */
	public String getFileName() {
		return this.fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getLevelName() {
		return "".equals(this.levelName) ? this.fileName : this.levelName;
	}
} 
