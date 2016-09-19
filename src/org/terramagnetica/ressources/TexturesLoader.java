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

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.terramagnetica.opengl.engine.AnimatedTexture;
import org.terramagnetica.opengl.engine.Texture;
import org.terramagnetica.opengl.engine.TextureImpl;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.ressources.TextureSet.TextureSheet;
import org.terramagnetica.utile.ImgUtil;

public final class TexturesLoader {
	
	public static final TextureQuad TEXTURE_NULL = new TextureQuad(0d, 0d, 1d, 1d, 0);
	
	private static ArrayList<TextureSet> loaded = new ArrayList<TextureSet>();
	
	private static Map<String, Texture> textureMap = new HashMap<String, Texture>();
	
	
	/** Détruit toutes les ressources, et libère la mémoire. */
	public static void destroyRessources(){
		for (int i = loaded.size() - 1 ; i > -1 ; i--) {
			unloadTextureSet(loaded.get(i));
		}
	}
	
	/** supprime les textures du set de textures passé en paramètre
	 * et libère la mémoire. */
	public static void unloadTextureSet(TextureSet set) {
		for (TextureSheet sheet : set.getAll()) {
			//Vérification : les textures sont parfois contenues dans plusieurs sets en même temps.
			boolean sheetElsewhere = false;
			for (TextureSet set0 : loaded) {
				if (set0.containsImage(sheet.getPath()) && set0 != set) {
					sheetElsewhere = true;
				}
			}
			if (!sheetElsewhere) {
				GL11.glDeleteTextures(sheet.getTexID());
			}
			
			lbl2 : for (Map.Entry<String, Texture> texEntry : sheet.getContentSet()) {
				
				for (TextureSet set0 : loaded) {
					if (set0.findTextureModel(texEntry.getKey()) != null && !set.equals(set0)) {
						continue lbl2;
					}
				}
				
				textureMap.remove(texEntry.getKey());
			}
		}
		loaded.remove(set);
	}
	
	/**
	 * Charge toutes les textures contenue dans le set de textures passé
	 * en paramètres.
	 * @param set
	 */
	public static void loadTextureSet(TextureSet set) {
		if (set == null) {
			return;
		}
		
		if (loaded.contains(set)) {
			return;
		}
		
		TextureSheet[] sheets = set.getAll();
		
		for (TextureSheet sheet : sheets) {
			
			//Vérifie si la texture existe déjà.
			boolean sheetExists = false;
			int id0 = 0;//L'identifiant de la texture déjà chargée, si elle existe.
			for (TextureSet texSet : loaded) {
				if (texSet.containsImage(sheet.getPath())) {
					sheetExists = true;
					id0 = texSet.getImage(sheet.getPath()).getTexID();
					break;
				}
			}
			
			//Définition de l'id de la texture.
			int id = sheetExists ? id0 : loadTexture0(sheet.getPath());
			sheet.setID(id);
			
			//Ajout.
			for (Map.Entry<String, Texture> texEntry : sheet.getContentSet()) {
				textureMap.put(texEntry.getKey(), texEntry.getValue().clone().withTextureID(id));
			}
		}
		
		loaded.add(set);
	}
	
	public static boolean isLoaded(TextureSet set) {
		return loaded.contains(set);
	}
	
	/**
	 * Charge une texture animée à partir de la texture indiquée.
	 * <br>La lecture se fait de gauche à droite, de haut en bas.
	 * @param spriteSizeX - La largeur de l'image animée, en pixels.
	 * @param spriteSizeY - La hauteur de l'image animée, en pixels.
	 * @param imgWidth - La largeur de l'image entière, en pixels.
	 * @param imgHeight - La hauteur de l'image entière, en pixels.
	 * @param startX - L'abscisse du premier sprite. Le sprite en haut à gauche
	 * aura pour coordonnées (0;0), celui juste à sa droite (1;0) et ainsi de
	 * suite.
	 * @param startY - L'ordonnée du premier sprite.
	 * @param endX - L'abscisse du dernier sprite.
	 * @param endY - L'ordonnée du dernier sprite.
	 * @param position - <code>true</code> pour une lecture en ligne, <code>false</code>
	 * pour une lecture en colonne.
	 * @param texID - L'indice de la texture openGL.
	 * @return Une animation composée de tous les sprites compris dans un rectangle dont le
	 * coin en haut à gauche est le premier sprite, et le coin en bas à droite le dernier.
	 * <p>L'ordre des images dépend du paramètre <code>position</code>
	 */
	public static AnimatedTexture createAnimatedTextureFromImage(
			int spriteSizeX, int spriteSizeY, int imgWidth, int imgHeight,
			int startX, int startY, int endX, int endY,
			boolean position, int texID) {
		
		if (startX > endX) {
			throw new IllegalArgumentException("startX > endX !");
		}
		
		if (startY > endY) {
			throw new IllegalArgumentException("startY > endY !");
		}
		
		AnimatedTexture anim = new AnimatedTexture();
		
		int start1 = position ? startY : startX;
		int start2 = position ? startX : startY;
		int end1 = position ? endY : endX;
		int end2 = position ? endX : endY;
		
		for (int i = start1 ; i <= end1 ; i++) {
			for (int j = start2 ; j <= end2 ; j++) {
				
				int i1 = position ? j : i;
				int i2 = position ? i : j;
				
				anim.add(new TextureQuad(
							spriteSizeX * i1,
							spriteSizeY * i2,
							spriteSizeX * (i1 + 1),
							spriteSizeY * (i2 + 1),
							imgWidth, 
							imgHeight,
							texID));
			}
		}
		
		return anim;
	}
	
	/**
	 * Crée une animation dont les images sont placés en ligne
	 * sur l'image et défilent de gauche à droite.
	 * @param spriteSizeX
	 * @param spriteSizeY
	 * @param nbSprite
	 * @param row - l'indice de la ligne de l'animation sur l'image.
	 * @param imgWidth
	 * @param imgHeight
	 * @return
	 * @see #createAnimatedTextureFromImage(int, int, int, int, int, int, int, int, boolean, int)
	 */
	public static AnimatedTexture createAnimatedTexture(
			int spriteSizeX, int spriteSizeY, int nbSprite,
			int row, int imgWidth, int imgHeight) {
		
		return createAnimatedTextureFromImage(spriteSizeX, spriteSizeY, imgWidth, imgHeight, 0, row, nbSprite - 1, row, true, 0);
	}
	
	/** charge une texture contenue dans le jar et l'ajoute à l'index des 
	 * textures.
	 * @return L'identifiant de la texture openGL, où 0 si la texture
	 * n'existe pas. */
	public static int loadTexture(String fileName) {
		if (textureMap.containsKey(fileName)) return get(fileName).getGLTextureID();
		
		int result = loadTexture0(fileName);
		if (result != 0) {
			textureMap.put(fileName, new TextureImpl(result));
		}
		return result;
	}
	
	private static int loadTexture0(String fileName) {
		try {
			return loadTexture(RessourcesManager.getURL(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Charge une texture à partir d'une URL d'image.
	 * @param path - L'URL contenant le chemin de l'image qui doit
	 * être chargée.
	 * @return L'identifiant de la texture openGL, ou 0 si la texture
	 * n'a pas pu être chargée.
	 */
	public static int loadTexture(URL path) {
		int ID = 0;
		
		BufferedInputStream bin = null;
		ByteBuffer buf = null;
		int width = 0;
		int height = 0;
		
		try {
			bin = new BufferedInputStream(
					path.openStream());
			BufferedImage img = ImageIO.read(bin);
			
			buf = ImgUtil.imageToByteBuffer(img);
			width = img.getWidth();
			height = img.getHeight();
			
			bin.close();
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e){
			e.printStackTrace();
		} finally {
			if (bin != null)
				try {
					bin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (buf == null) {
				return 0;
			}
		}
		
		ID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ID);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, 4, width, height,
				0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);

		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
		return ID;
	}
	
	public static Texture get(String ID) {
		Texture tex = textureMap.get(ID);
		if (tex == null) return TEXTURE_NULL;
		
		return tex.clone();
	}
	
	public static TextureQuad getQuad(String ID) {
		Texture tex = textureMap.get(ID);
		if (tex == null || !(tex instanceof TextureQuad)) return TEXTURE_NULL;
		
		return ((TextureQuad) tex).clone();
	}
	
	public static AnimatedTexture getAnimatedTexture(String ID) {
		Texture tex = textureMap.get(ID);
		if (tex == null || !(tex instanceof AnimatedTexture)) return new AnimatedTexture(new TextureQuad[]{TEXTURE_NULL});
		
		return ((AnimatedTexture) tex).clone();
	}
}
