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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;
import org.terramagnetica.opengl.engine.Model3D;

import net.bynaryscode.util.FileFormatException;

public final class ModelLoader {
	
	private static Map<String, Model3D> models = new HashMap<String, Model3D>();
	
	/**
	 * Charge une liste de modèles.
	 * @param set - la liste qui contient les emplacements des fichiers .obj.
	 * Ces emplacements seront également utilisés comme identifiant du modèle
	 * dans le programme, lors de l'utilisation de la méthode {@link #get(String)}.
	 * @param allowChild - voir {@link Model3D#parse(String, String, boolean)}.
	 */
	public static void loadModelSet(List<String> set, boolean allowChild) {
		for (String str : set) {
			Model3D loaded = loadModel(str, allowChild);
			if (loaded != null) models.put(str, loaded);
		}
	}
	
	/**
	 * Supprime tous les modèles chargés et enregistrés.
	 */
	public static void unloadAll() {
		for (Entry<String, Model3D> e : models.entrySet()) {
			Model3D m = e.getValue();
			GL11.glDeleteTextures(m.getTextureID());
		}
		models.clear();
	}
	
	/** Charge un modèle 3D contenu dans l'archive .jar.
	 * @see Model3D#parse(String, String, boolean) */
	public static Model3D loadModel(String path, boolean allowChild) {
		//Chargement du fichier .obj
		InputStream str = null;
		try {
			str = RessourcesManager.getURL(path).openStream();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
		StringBuilder fileContents = new StringBuilder();
		try {
			RessourcesManager.readFileString(str, fileContents);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String objStr = fileContents.toString();
		
		//Chargement du fichier .mtl
		String mtlPath = Model3D.mtlPath(objStr, path);
		try {
			str = RessourcesManager.getURL(mtlPath).openStream();
		} catch (IOException e1) {
			e1.printStackTrace();
			str = null;
		}
		fileContents = new StringBuilder();
		if (str != null) {
			try {
				RessourcesManager.readFileString(str, fileContents);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String mtlStr = fileContents.toString();
		
		//Analyse
		Model3D ret = null;
		try {
			ret = Model3D.parse(objStr, mtlStr, allowChild);
		} catch (FileFormatException e) {
			e.printStackTrace();
			return null;
		}
		
		//Texture
		int lio = path.lastIndexOf('/'); if (lio == -1) lio = path.lastIndexOf('\\');
		String dirPath = path.substring(0, lio + 1);
		
		ArrayList<Model3D> models = new ArrayList<Model3D>(ret.getChildren());
		models.add(ret);
		
		for (Model3D model : models) {
			URL texURL = null;
			try {
				texURL = RessourcesManager.getURL(dirPath + model.getTexturePath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (texURL != null && model.hasTextures()) {
				model.setTextureID(TexturesLoader.loadTexture(texURL));
			}
		}
		
		return ret;
	}
	
	/** Obtenir le modèle portant l'identifiant passé en paramètre.
	 * S'il n'existe pas, retourne un modèle vide. */
	public static Model3D get(String id) {
		Model3D m = models.get(id);
		if (m == null) m = new Model3D();
		return m;
	}
}
