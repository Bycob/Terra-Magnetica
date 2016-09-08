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

package org.terramagnetica.ressources;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.opengl.engine.Texture;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.ressources.TextureSet.TextureSheet;
import org.terramagnetica.utile.ImgUtil;

public class ImagesLoader {
	
	//gui du Créator
	public static Image aimantIcon;
	public static Image splashScreen;
	public static final String popupToolbar = "gui/creator/popupToolbar.png";
	public static final String effacer = "gui/creator/effacer.png";
	public static final String deplacer = "gui/creator/deplacer.png";
	public static final String decoration = "gui/creator/decoration.png";
	public static final String marqueur = "gui/creator/marqueur.png";
	public static final String declencheur = "gui/creator/declencheur.png";
	public static final String flèche = "gui/creator/arrow.png";
	
	public static final String[] creatorGuiImages = {
		popupToolbar,
		effacer,
		deplacer,
		decoration,
		marqueur,
		declencheur,
		flèche
	};
	
	private static Map<String, Image> imgMap = new HashMap<String, Image>();
	
	/**
	 * Lit une image avec le chemin indiqué.
	 * @param path Le chemin relatif de la ressource voulue. Cette ressource
	 * sera recherché dans le dossier racine des ressources défini par le
	 * {@link RessourcesManager}.
	 * @return L'image qui a été lue, ou {@code null} si l'image n'a pu
	 * être lue.
	 */
	public static final BufferedImage readImage(String path) {
		try {
			URL url = RessourcesManager.getURL(path);
			BufferedImage result = ImageIO.read(url);
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void loadImages(){
		
		loadTextureSet(GameRessources.gameTextureSet);
		
		if (aimantIcon == null) {
			ImagesLoader.aimantIcon = ImagesLoader.readImage("gui/creator/tm_editor32.png");
		}
		
		try {
			Image player = ImageIO.read(RessourcesManager.getURL(GameRessources.PATH_PLAYER));
			
			if (player != null) {
				ImagesLoader.imgMap.put(GameRessources.PATH_PLAYER, ImgUtil.getPartOfImage(player, 0, 0, 192, 288));
			}
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadCreatorGui(){
		for (String str : creatorGuiImages) {
			try {
				imgMap.put(str, ImageIO.read(RessourcesManager.getURL(str)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void loadTextureSet(TextureSet texSet) {
		TextureSheet sheets[] = texSet.getAll();
		
		for (TextureSheet sheet : sheets) {
			try {
				
				Image src = ImageIO.read(RessourcesManager.getURL(sheet.getPath()));
				
				for (Map.Entry<String, Texture> textureEntry : sheet.getContentSet()) {
					if (textureEntry.getValue() instanceof TextureQuad) {
						ImagesLoader.imgMap.put(
								textureEntry.getKey(),
								ImgUtil.getPartOfImage(src, (TextureQuad) textureEntry.getValue()));
					}
				}
				
			} catch (IllegalArgumentException e){
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Image get(String id) {
		Image got = imgMap.get(id);
		if (got == null) return null;
		return got;
	}
	
	public static int imgLoadedCount() {
		return imgMap.size();
	}
}
