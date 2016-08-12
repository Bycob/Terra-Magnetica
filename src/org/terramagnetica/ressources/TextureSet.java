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

package org.terramagnetica.ressources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.terramagnetica.opengl.engine.Texture;

import java.util.Set;

public class TextureSet {
	
	public static class TextureSheet {
		private String path;
		private int id = 0;
		private HashMap<String, Texture> map;
		
		TextureSheet(String path) {
			this.path = path;
			this.map = new HashMap<String, Texture>();
		}
		
		void putTexture(String id, Texture texture) {
			this.map.put(id, texture);
		}
		
		@SuppressWarnings("unchecked")
		public Map<String, Texture> getContents() {
			return (Map<String, Texture>) this.map.clone();
		}
		
		public Set<Map.Entry<String, Texture>> getContentSet() {
			return this.map.entrySet();
		}
		
		public String getPath() {
			return this.path;
		}
		
		public int getTexID() {
			return this.id;
		}
		
		void setID(int id) {
			this.id = id;
		}
	}
	
	private List<TextureSheet> theTextureList = new ArrayList<TextureSheet>();
	private int currentIndex;
	
	public TextureSet() {
		this.currentIndex = -1;
	}
	
	public void createImage(String pathName) {
		int indexOfPath = indexOf(pathName);
		if (indexOfPath != -1) {
			this.currentIndex = indexOfPath;
		}
		else {
			this.currentIndex = this.theTextureList.size();
			TextureSheet ts = new TextureSheet(pathName);
			
			this.theTextureList.add(ts);
		}
	}
	
	public void addImage(TextureSheet img) {
		this.createImage(img.path);
		for (Entry<String, Texture> e : img.getContentSet()) {
			this.theTextureList.get(this.currentIndex).putTexture(e.getKey(), e.getValue());
		}
	}
	
	public void selectImage(String path) {
		int indexOfPath = indexOf(path);
		
		if (indexOfPath == -1) {
			throw new NullPointerException("L'image n'existe pas.");
		}
		else {
			this.currentIndex = indexOfPath;
		}
	}
	
	public boolean containsImage(String path) {
		for (TextureSheet sheet : this.theTextureList) {
			if (sheet.path.equals(path)) {
				return true;
			}
		}
		
		return false;
	}
	
	public TextureSheet getImage(String id) {
		for (TextureSheet sheet : this.theTextureList) {
			if (sheet.path.equals(id)) {
				return sheet;
			}
		}
		return null;
	}
	
	public void addTextureModel(String id, Texture model) {
		if (this.currentIndex == -1) {
			throw new NullPointerException("Pas d'image selectionnée.");
		}
		
		TextureSheet sheet = this.theTextureList.get(this.currentIndex);
		if (!id.startsWith(sheet.path)) {
			id = sheet.path + id;
		}
		
		sheet.putTexture(id, model);
	}
	
	/**
	 * Trouve le modèle de texture dont l'identifiant est le même que celui
	 * passé en paramètres.
	 * @param id - L'identifiant de la texture dans le set.
	 * @return Le modèle de texture du set portant l'identifiant indiqué, ou
	 * {@code null} si il n'existe pas.
	 */
	public Texture findTextureModel(String id) {
		for (TextureSheet sheet : this.theTextureList) {
			if (id.startsWith(sheet.path)) {
				return sheet.map.get(id);
			}
		}
		return null;
	}
	
	int indexOf(String path) {
		for (int i = 0 ; i < this.theTextureList.size() ; i++) {
			TextureSheet here = this.theTextureList.get(i);
			if (here.path.equals(path)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public TextureSheet[] getAll() {
		TextureSheet result[] = new TextureSheet[this.theTextureList.size()];
		return this.theTextureList.toArray(result);
	}
}
