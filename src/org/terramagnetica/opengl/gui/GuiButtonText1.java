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

import java.util.HashMap;
import java.util.Map;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.gui.GuiConstants;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.Vec2d;

/**
 * Un bouton contenant du texte. Si le bouton est cliqué
 * alors qu'il n'est pas visible, le click n'est pas
 * enregistré.
 * @author Louis JEAN
 *
 */
public class GuiButtonText1 extends GuiAbstractButton {
	
	/** tableau de textures en fonction de l'état du bouton */
	private static Map<Integer, String> texturesMap = new HashMap<Integer, String>();
	
	static {
		texturesMap.put(NORMAL, GameRessources.ID_BUTTON);
		texturesMap.put(PRESSED, GameRessources.ID_BUTTON_PRESSED);
		texturesMap.put(MOUSE_ON, GameRessources.ID_BUTTON_OVER);
	}
	
	private String text;
	private int preferedFontSize = 16;
	private FontSizeManager fsm = new FontSizeRelativeToRectangle(this.theTextPainter);
	
	private Color4f textColor = GuiConstants.TEXT_COLOR_DEFAULT;
	
	public GuiButtonText1() {
		init(new RectangleDouble(0, 0, 0, 0), "");
	}
	
	public GuiButtonText1(String text) {
		init(new RectangleDouble(0, 0, 0, 0), text);
	}
	
	public GuiButtonText1(int xmin, int ymin, int xmax, int ymax, String text){
		init(new RectangleDouble(
				theWindow.getXOnGLOrtho(xmin), theWindow.getYOnGLOrtho(ymin),
				theWindow.getXOnGLOrtho(xmax), theWindow.getYOnGLOrtho(ymax)), text);
	}
	
	public GuiButtonText1(double xmin, double ymin, double xmax, double ymax, String text){
		init(new RectangleDouble(xmin, ymin, xmax, ymax), text);
	}
	
	private void init(RectangleDouble coordGL, String text){
		this.setBoundsGL(coordGL);
		this.text = text;
		
		this.setColor(new Color4f(1f, 1f, 1f));
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getText(){
		return text;
	}
	
	public Color4f getTextColor() {
		return textColor.clone();
	}

	public void setTextColor(Color4f textColor) {
		this.textColor = textColor.clone();
	}
	
	@Override
	protected void drawComponent(Painter painter) {
		painter.setTexture(null);
		
		RectangleDouble coordGL = this.getBoundsGL();
		
		//dessin du bouton
		TextureQuad tex = TexturesLoader.getQuad(texturesMap.get(getState()));
		painter.setColor(this.getColor());
		tex.drawQuad2D(coordGL.xmin, coordGL.ymin, coordGL.xmax, coordGL.ymax, true, painter);
		
		//ajout du texte
		drawText();
	}
	
	protected void drawText() {
		Vec2d textPlace = this.getBoundsGL().center();
		
		int fontSize = fsm.calculFontSize(this.getBoundsDisp().scaleFromCenter(0.9, 0.9), text, preferedFontSize);
		
		this.theTextPainter.setColor(textColor.getRedf(),
				textColor.getGreenf(),
				textColor.getBluef(),
				textColor.getAlphaf());
		
		this.theTextPainter.drawCenteredString2D(text, textPlace.x, textPlace.y, fontSize);
	}
	
	@Override
	public GuiButtonText1 clone() {
		GuiButtonText1 result = (GuiButtonText1) super.clone();
		
		return result;
	}
}
