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

import net.bynaryscode.util.maths.geometric.RectangleInt;

/** Ce calculateur de taille de police calcule la taille de façon
 * à ce que le texte loge dans un rectangle, passé en paramètre
 * à la méthode {@link FontSizeManager#calculFontSize(RectangleInt, String, int)}.
 * <p>Il tient compte de la taille de police de préférence : 
 * si le texte loge dans le rectangle à cette police, alors c'est
 * celle-ci que le calculateur retournera
 * @author Louis JEAN
 *  */
public class FontSizeRelativeToRectangle implements FontSizeManager {

	private GuiTextPainter textPainter;
	
	public FontSizeRelativeToRectangle(GuiTextPainter textPainter) {
		this.textPainter = textPainter;
	}
	
	@Override
	public int calculFontSize(RectangleInt bounds, String str, int preferedFontSize) {
		
		int fontSize;
		if (bounds.getHeight() < (preferedFontSize * this.textPainter.getRealScale()) ||
				bounds.getWidth() < (this.textPainter.getFont().getStringSize(str, preferedFontSize, this.textPainter.getLetterSpacing())
						* this.textPainter.getRealScale())) {
			
			int widthFontSize = this.textPainter.getFont().getFontSize(
					(int) (bounds.getWidth() / this.textPainter.getRealScale()), str, this.textPainter.getLetterSpacing());
			fontSize = (int) Math.min(widthFontSize, bounds.getHeight() / this.textPainter.getRealScale());
		}
		else {
			fontSize = preferedFontSize;
		}
		
		return fontSize;
	}
}
