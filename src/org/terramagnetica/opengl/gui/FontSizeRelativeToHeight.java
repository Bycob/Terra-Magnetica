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

/**
 * Ce calculateur de taille de police calcule la taille de police
 * du texte par rapport � une hauteur particuli�re. La hauteur prise
 * en compte est obtenue gr�ce � la m�thode {@link RectangleInt#getHeight()},
 * op�r�e sur le rectangle pass� en param�tre � la m�thode
 * {@link FontSizeManager#calculFontSize(RectangleInt, String, int)}.
 * <p>La taille de police est calcul�e de fa�on � ce que le texte ne
 * prenne pas plus de place en hauteur que ce qui est indiqu�.
 * Le calculateur prend en compte la taille de police par pr�f�rence :
 * si le texte dessin� a une hauteur inf�rieure ou �gale � celle indiqu�e
 * alors c'est cette taille de police qui sera retourn�e.
 * @author Louis JEAN
 *
 */
public class FontSizeRelativeToHeight implements FontSizeManager {
	
	private GuiTextPainter textPainter;
	
	public FontSizeRelativeToHeight(GuiTextPainter textPainter) {
		this.textPainter = textPainter;
	}
	
	@Override
	public int calculFontSize(RectangleInt bounds, String str,
			int preferedFontSize) {
		
		int fontSize;
		if (bounds.getHeight() < (preferedFontSize * this.textPainter.getRealScale())) {
			fontSize = bounds.getHeight();
		}
		else {
			fontSize = preferedFontSize;
		}
		return fontSize;
	}

}
