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

package org.terramagnetica.opengl.gui;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.ressources.RessourcesManager;
import org.terramagnetica.ressources.TexturesLoader;
import org.terramagnetica.utile.ImgUtil;

/**
 * Gestionnaire des polices de caractères. Une police de caractère
 * contient dans ce programme une image par lettre. Chaque lettre
 * sera représentée à l'écran par son image, à la taille spécifiée.
 * @author Louis JEAN
 *
 */
public final class FontManager {
	
	private static Map<String, Font> fonts = new HashMap<String, Font>();
	
	private static String[] fontData;
	
	public static void addFont(String name, Font font) {
		fonts.put(name, font);
	}
	
	public static Font font(String name) {
		return fonts.get(name);
	}
	
	public static Letter getCharTexture(String fontName, char character){
		Font font = fonts.get(fontName);
		if (font != null){
			return font.getCharTexture(character);
		}
		return null;
	}
	
	/**
	 * Charge le fichier de police de caractère de nom "name", d'identifiant "nFile".<br>
	 * Le fichier doit être placé dans le répertoire "fonts" et son nom doit être formé comme il suit :
	 * <blockquote><tt>[name][nFile].png</tt></blockquote>
	 * @param name - le nom de la police.
	 * @param nFile - le numero de fichier.
	 */
	public static void loadFont(String name, int nFile) {
		Font font = FontManager.font(name);
		boolean exists = (font != null);
		
		if (!exists) {
			font = new Font();
		}
		
		URL fileLocation;
		try {
			fileLocation = RessourcesManager.getURL("fonts/" + name + nFile + ".png");
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		
		int fontID = TexturesLoader.loadTexture(fileLocation);
		BufferedImage fontImage = null;
		
		try {
			fontImage = ImageIO.read(fileLocation);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		int imgHeight = fontImage.getHeight();
		int resolution = imgHeight / 8;
		int[][] fontRGBA = ImgUtil.getRGBA(fontImage);
		int imgWidth = fontImage.getWidth();
		
		for (int i = 0 ; i < 8 ; i++) {
			if (fontData.length <= i + nFile * 8) {
				break;
			}
			
			char[] charArray = fontData[i + nFile * 8].toCharArray();
			int column = 0;
			boolean pixels = false;
			int left = 0, right = 0;
			boolean readen = false;
			
			for (int j = 0 ; j < charArray.length ; j++) {
				boolean unbegun = true;
				int charCounter = 0;
				while (!readen) {
					pixels = false;
					
					for (int y = resolution * i ; y < resolution * (i + 1) ; y++) {
						int alpha = fontRGBA[y * imgWidth + column][3];
						if (alpha != 0) {
							pixels = true;
						}
					}
					
					
					if (unbegun && pixels) {
						unbegun = false;
						if (charCounter == 0) left = column - 1;
					}
					
					if (!unbegun && !pixels) {
						readen = true;
						right = column + 1;
						charCounter++;
					}
					
					if ((charArray[j] == '\"' || charArray[j] == '¨') && charCounter == 1 && readen) {
						readen = false;
						unbegun = true;
					}
					
					
					column++;
					if (column > imgWidth) {
						right = 0;
						left = 0;
						break;
					}
				}
				
				TextureQuad tex = new TextureQuad(left, resolution * i, right, resolution * (i + 1),
						fontImage.getWidth(), fontImage.getHeight(), fontID);
				font.setCharTexture(charArray[j], new Letter(tex, charArray[j]));
				
				column++;
				readen = false;
			}
		}
		
		if (!exists) {
			fonts.put(name, font);
		}
	}
	
	/** Charge le fichier qui contient l'ordre des lettres sur une image de police de caractère.<br>
	 * Toutes les polices doivent respecter cet ordre. */
	public static void loadFontData(){
		List<String> lecture = new ArrayList<String>();
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(
					new InputStreamReader(
							RessourcesManager.getURL("fonts/fontData.txt").openStream()));
			String line = new String();
			
			while ((line = br.readLine()) != null) {
				lecture.add(line);
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FontManager.fontData = new String[lecture.size()];
		
		for (int i = 0 ; i < lecture.size() ; i++) {
			FontManager.fontData[i] = lecture.get(i);
		}
	}
}
