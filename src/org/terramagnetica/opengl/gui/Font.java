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

import net.bynaryscode.util.maths.MathUtil;

public class Font {
	
	private Map<Character, Letter> letters = new HashMap<Character, Letter>();
	private int mainTextureID = 0;
	private int spaceValue = 6;
	
	public Font(){
		
	}
	
	public Font(Map<Character, Letter> letters) {
		this.letters = letters;
	}
	
	public Letter getCharTexture(char character) {
		return letters.get(character);
	}
	
	public void setCharTexture(char character, Letter letter) {
		if (character == 'a') {
			mainTextureID = letter.getGLTextureID();
		}
		
		letters.put(character, letter);
	}
	
	public int getStringSize(String str, int fontSize) {
		return getStringSize(str, fontSize, 0);
	}
	
	public int getStringSize(String str, int fontSize, float letterSpacing) {
		int result = 0;
		
		int absLetterSpacing = (int) (fontSize * letterSpacing);
		char[] string1 = str.toCharArray();
		
		for (char character : string1) {
			if (result != 0) {
				result += absLetterSpacing;
			}
			Letter letter = letters.get(character);
			
			if (character == ' ') {
				result += spaceValue;
			}
			
			if (letter != null) {
				result += letter.getWidth(fontSize);
			}
		}
		
		return result;
	}
	
	/**
	 * Calucule, à partir d'une chaine de caractères et de sa
	 * longueur lorsqu'on l'écrit avec cette police de caractère,
	 * la taille de la police du texte. Ne supporte pas les chaines
	 * de caractères dont la taille de police est supérieure à 1024.
	 * @param stringLength - La longueur de la chaine de caractère
	 * {@code str} sur l'écran, en pixels.
	 * @param str - La chaine de caractère en question.
	 * @return La taille de police de la chaine.
	 */
	public int getFontSize(int stringLength, String str) {
		return getFontSize(stringLength, str, 0);
	}
	
	public int getFontSize(int stringLength, String str, float letterSpacing) {
		int borneLow = 0;
		int borneHight = 1024;
		
		while (borneHight - borneLow > 1) {
			int newBorne = MathUtil.integerPart((borneLow + borneHight) / 2.0);
			if (getStringSize(str, newBorne, letterSpacing) >= stringLength) {
				borneHight = newBorne;
			}
			else {
				borneLow = newBorne;
			}
		}
		
		return borneLow;
	}
	
	public int getMainTextureID(){
		return mainTextureID;
	}
	
	/** retourne la largeur d'un espace en pixels. */
	public int getSpaceValue() {
		return spaceValue;
	}
}
