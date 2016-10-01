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
	 * Charge les polices de caract�re.
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
	 * Dessine une chaine de caract�re centr�e sur les coordonn�es
	 * (x ; y) pass�s en param�tre.
	 * <p>La taille de police est relative : elle est mise � l'�chelle
	 * automatiquement.
	 * @param toDraw - La chaine � dessiner.
	 * @param x - abscisse.
	 * @param y - ordonn�e.
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
	 * Dessine une chaine de caract�re commen�ant en (x ; y).
	 * Le point indiqu� est le point en haut � gauche de la premi�re
	 * lettre.
	 * <p>La taille de police est relative : elle est mise � l'�chelle
	 * automatiquement.
	 * @param toDraw - La chaine de caract�re � dessiner.
	 * @param x - abscisse.
	 * @param y - ordonn�e.
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
	
	/** Dessine une chaine centr�e sur y et commen�ant en x. */
	public void drawString2DBeginAt(String toDraw, double x, double y, int fontSize) {
		double var0 = theWindow.getHeightOnGLOrtho(fontSize) * getRealScale() / 2.0;
		drawString2D(toDraw, x, y + var0, fontSize);
	}
	
	/**
	 * Dessine le texte pass� en param�tre � l'�cran, aux coordonn�es
	 * indiqu�es. 
	 * @param text - Le texte � dessiner. Chaque retour � la ligne doit
	 * marquer un changement de paragraphe.
	 * @param bounds - Le rectangle d�finissant la zone de dessin :
	 * le haut de la premi�re ligne est en {@code bounds.ymin}, la
	 * limite � gauche en {@code bounds.xmin} et la limite � droite
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
	 * La chaine pass�e en param�tre est d�coup�e en lignes
	 * de texte, de fa�on � ce que chaque chaine contenue par
	 * la liste ne d�passe pas � droite d'une page dont la
	 * largeur doit �tre pass�e en param�tre.
	 * @param str - La chaine � d�couper.
	 * @param fontSize - la taille de police de la chaine
	 * @param pageWidth - la largeur de la page contenant le texte.
	 * Chaque ligne commen�ant au bord gauche d'une page d'une telle
	 * largeur se terminera avant le bord droit.
	 * <br>La largeur de la page est exprim�e en pixels � l'�cran.
	 * @return Une liste contenant les lignes qui, mises bout
	 * � bout et en rajoutant les espaces enlev�s, reforment
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
	
	/** Donne l'�chelle r�elle du texte, c'est � dire l'�chelle du {@link GuiTextPainter}
	 * multipli�e par celle de la fen�tre. */
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
