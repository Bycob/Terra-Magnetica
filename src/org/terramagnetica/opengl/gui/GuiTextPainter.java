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

import java.util.ArrayList;
import java.util.List;

import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.TextureQuad;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Rectangle;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public final class GuiTextPainter {
	
	/**
	 * Charge les polices de caractère.
	 */
	public static void init() {
		FontManager.loadFontData();
		
		FontManager.loadFont("default", 0);
		FontManager.loadFont("default", 1);
		FontManager.loadFont("default", 2);
	}
	
	private Painter painter;
	private final GuiWindow theWindow = GuiWindow.getInstance();
	
	private Font fontUsed = new Font();
	private int glFontTexID = 0;
	
	private Color4f color = new Color4f(1f, 1f, 1f);
	private Color4f savedColor = null;
	
	private float scale = 1;
	/** L'espace entre les lettres, en pourcentage de la taille de police. */
	private float letterSpacing = 0.1f;
	
	private double lineSpace = 0.0;
	
	public GuiTextPainter(Painter painter) {
		setFont("default");
		this.painter = painter;
	}
	
	public void drawChar2D(char toDraw, double x, double y, int fontSize) {
		this.prDrawChar2D(toDraw, x, y, fontSize);
	}

	private void prDrawChar2D(char toDraw, double x, double y, int fontSize) {
		if (toDraw == ' ') {
			return;
		}
		
		Letter drawn = fontUsed.getCharTexture(toDraw);
		
		if (drawn == null) {
			return;
		}
		
		TextureQuad tex = drawn.getTexture();
		
		double width = theWindow.getWidthOnGLOrtho(drawn.getWidth(fontSize)) * this.getRealScale();
		double height = theWindow.getHeightOnGLOrtho(fontSize) * this.getRealScale();
		
		this.painter.setColor(color.clone());
		tex.drawQuad2D(x, y + height, x + width, y, true, this.painter);
	}

	/**
	 * Dessine une chaine de caractère centrée sur les coordonnées
	 * (x ; y) passés en paramètre.
	 * <p>La taille de police est relative : elle est mise à l'échelle
	 * automatiquement.
	 * @param toDraw - La chaine à dessiner.
	 * @param x - abscisse.
	 * @param y - ordonnée.
	 * @param fontSize - Taille de police en pixels.
	 */
	public void drawCenteredString2D(String toDraw, double x, double y, int fontSize) {
		double lenght = theWindow.getWidthOnGLOrtho(fontUsed.getStringSize(toDraw, (int) (fontSize * getRealScale()), this.letterSpacing));
		double height = theWindow.getHeightOnGLOrtho((int) (fontSize * getRealScale()));
		
		double startX = x - lenght / 2;
		double startY = y + height / 2;
		
		drawString2D(toDraw, startX, startY, fontSize);
	}
	
	/**
	 * Dessine une chaine de caractère commençant en (x ; y).
	 * Le point indiqué est le point en haut à gauche de la première
	 * lettre.
	 * <p>La taille de police est relative : elle est mise à l'échelle
	 * automatiquement.
	 * @param toDraw - La chaine de caractère à dessiner.
	 * @param x - abscisse.
	 * @param y - ordonnée.
	 * @param fontSize - La taille de police.
	 */
	public void drawString2D(String toDraw, double x, double y, int fontSize) {
		char[] charArray = toDraw.toCharArray();
		
		Letter drawn = null;
		
		double height = theWindow.getHeightOnGLOrtho(fontSize) * getRealScale();
		
		for (char character : charArray) {
			prDrawChar2D(character, x, y - height, fontSize);
			
			drawn = fontUsed.getCharTexture(character);
			
			if (drawn != null){
				double lWidth = theWindow.getWidthOnGLOrtho(drawn.getWidth(fontSize)) * getRealScale();
				x += lWidth;
			}
			
			if (character == ' ') {
				x += theWindow.getWidthOnGLOrtho(fontUsed.getSpaceValue()) * getRealScale();
			}
			
			x += theWindow.getWidthOnGLOrtho((int) (this.letterSpacing * fontSize * getRealScale()));
		}
	}
	
	/** Dessine une chaine centrée sur y et commençant en x. */
	public void drawString2DBeginAt(String toDraw, double x, double y, int fontSize) {
		double var0 = theWindow.getHeightOnGLOrtho(fontSize) * getRealScale() / 2.0;
		drawString2D(toDraw, x, y + var0, fontSize);
	}
	
	/**
	 * Dessine le texte passé en paramètre à l'écran, aux coordonnées
	 * indiquées. 
	 * @param text - Le texte à dessiner. Chaque retour à la ligne doit
	 * marquer un changement de paragraphe.
	 * @param bounds - Le rectangle définissant la zone de dessin :
	 * le haut de la première ligne est en {@code bounds.ymin}, la
	 * limite à gauche en {@code bounds.xmin} et la limite à droite
	 * en {@code bounds.xmax}.
	 * @param fontSize - La taille de police.
	 * @return Le nombre de ligne que comporte le texte sur le dessin.
	 */
	public int drawPlainText2D(String text, Rectangle bounds, int fontSize) {
		String[] pg = text.split("\n");
		RectangleDouble bd = bounds.asDouble();
		final int dispWidth = theWindow.getWidthOnDisplay(bd.getWidth());
		final double fontHeight = theWindow.getHeightOnGLOrtho(fontSize) * getRealScale();
		int nbLines = 0;
		
		for (String str : pg) {
			str = "   " + str;
			List<String> ft = getLines(str, fontSize, dispWidth);
			
			for (String tline : ft) {
				double x = bd.xmin;
				double y = bd.ymin - nbLines * (fontHeight + lineSpace);
				this.drawString2D(tline, x, y, fontSize);
				nbLines++;
			}
		}
		
		return nbLines;
	}
	
	/**
	 * La chaine passée en paramètre est découpée en lignes
	 * de texte, de façon à ce que chaque chaine contenue par
	 * la liste ne dépasse pas à droite d'une page dont la
	 * largeur doit être passée en paramètre.
	 * @param str - La chaine à découper.
	 * @param fontSize - la taille de police de la chaine
	 * @param pageWidth - la largeur de la page contenant le texte.
	 * Chaque ligne commençant au bord gauche d'une page d'une telle
	 * largeur se terminera avant le bord droit.
	 * <br>La largeur de la page est exprimée en pixels à l'écran.
	 * @return Une liste contenant les lignes qui, mises bout
	 * à bout et en rajoutant les espaces enlevés, reforment
	 * le texte d'origine.
	 */
	private List<String> getLines(String str, int fontSize, int pageWidth) {
		ArrayList<String> list = new ArrayList<String>();
		int startLine = 0, endLine = 0;
		
		for (int i = 0 ; i < str.length() ; i++) {
			if (str.charAt(i) == ' ') {
				endLine = i;
			}
			if (this.fontUsed.getStringSize(str.substring(startLine, i + 1), fontSize, this.letterSpacing) * getRealScale() > pageWidth) {
				if (endLine == startLine) {
					list.add(str.substring(startLine, i));
					startLine = i;
					endLine = i;
				}
				else {
					list.add(str.substring(startLine, endLine));
					startLine = endLine;
				}
			}
		}
		
		list.add(str.substring(startLine, str.length()));
		return list;
	}
	
	public double widthOnGL(String str, int fontSize) {
		return theWindow.getWidthOnGLOrtho(
				fontUsed.getStringSize(str, fontSize, this.letterSpacing)) * getRealScale();
	}

	public double heightOnGL(int fontSize) {
		return theWindow.getHeightOnGLOrtho(fontSize) * getRealScale();
	}

	public void setFont(String font){
		Font selectedFont = FontManager.font(font);
		
		if (selectedFont != null) {
			fontUsed = selectedFont;
			glFontTexID = fontUsed.getMainTextureID();
		}
	}
	
	public Font getFont() {
		return fontUsed;
	}
	
	public void setScale(float newScale) {
		if (newScale > 0) this.scale = newScale;
	}
	
	public float getScale() {
		return scale;
	}
	
	/** Donne l'échelle réelle du texte, c'est à dire l'échelle du {@link GuiTextPainter}
	 * multipliée par celle de la fenêtre. */
	public double getRealScale() {
		return this.scale * theWindow.getScale();
	}
	
	public void setColor(float r, float g, float b, float a) {
		color = new Color4f(r, g, b, a);
	}
	
	public void setColor(Color4f color) {
		this.color = color.clone();
	}
	
	public Color4f getColor() {
		return new Color4f(color.getRedf(),
				color.getGreenf(),
				color.getBluef(),
				color.getAlphaf());
	}
	
	public void saveColor() {
		savedColor = new Color4f(color.getRedf(),
				color.getGreenf(),
				color.getBluef(),
				color.getAlphaf());
	}
	
	public void retabColor() {
		color = new Color4f(savedColor.getRedf(),
				savedColor.getGreenf(),
				savedColor.getBluef(),
				savedColor.getAlphaf());
	}
	
	public void setLineSpace(double lp) {
		this.lineSpace = Math.max(0, lp);
	}
	
	public double getLineSpace() {
		return lineSpace;
	}
	
	public void setLetterSpacing(float letterSpacing) {
		this.letterSpacing = letterSpacing;
	}
	
	public float getLetterSpacing() {
		return this.letterSpacing;
	}
}
