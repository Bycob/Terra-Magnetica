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

package org.terramagnetica.game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Util;

/**
 * Classe permettant d'analyser les fichiers contenant les
 * descriptions de pages. Le language spécifique (et inédit)
 * utilisé est décrit dans la documentation de la méthode
 * {@link #parseAndSet(String)}.
 * <p>Un texte simple peut aussi être passé à la Page. Chaque
 * retour à la ligne sera interprêté comme un espace. Pour changer
 * de paragraphe, utiliser la chaîne :
 * <blockquote>{@code "\n#p\n"}</blockquote>
 * @author Louis JEAN
 */
public class Page {
	
	public static Page read(File f) {
		StringBuilder fileContents = new StringBuilder(1024);
		BufferedReader bfr = null;
		
		try {
			bfr = new BufferedReader(new FileReader(f));
			String line = "";
			
			while ((line = bfr.readLine()) != null) {
				fileContents.append(line);
				fileContents.append("\n");
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (bfr != null) {
				try {
					bfr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return new Page(fileContents.toString());
	}
	
	private ArrayList<Object> contents = new ArrayList<Object>();
	
	public Page() {}
	
	public Page(String fileContents) {
		this.parseAndSet(fileContents);
	}
	
	/**
	 * Analyse le contenu d'un fichier contenant du texte valide,
	 * et le transforme en contenu lisible pour l'affichage. Les fichiers
	 * sont rédigés normalement. On peut aussi ajouter un tag particulier,
	 * précédé d'un dièse (#) lui-même précédé d'un retour à la ligne.
	 * <p>Les tags disponibles sont :<ul>
	 * <li><b>tex {@literal <id>} [légende]</b> : insère une texture du jeu d'identifiant
	 * <i>id</i>. On peut aussi ajouter une légende à l'image.
	 * <li><b>p</b> : insère un retour à la ligne forcé.
	 * </ul>
	 * <p>Pour ajouter un '#' en début de ligne, taper "\" avant. Le '\' sera
	 * tout simplement ignoré, et le tag ne sera pas pris en compte. Pour ajouter
	 * le caractère '\' en début de ligne, l'insérer deux fois.
	 * @param fileContents - Le contenu du fichier à analyser.
	 */
	public void parseAndSet(String fileContents) {
		this.contents.clear();
		String[] lineArray = fileContents.split("\n");
		
		for (String line : lineArray) {
			line = line.trim();
			if (line.length() == 0) continue;
			
			if (!(line.charAt(0) == '#')) {
				if (line.charAt(0) == '\\') {
					line = line.substring(1);
				}
				insertString(line);
			}
			else {
				String[] array1 = line.substring(1).split(" ");
				String tag = array1.length == 0 ? "" : array1[0];
				
				if ("tex".equals(tag)) {
					if (array1.length >= 2) {
						TextureQuad tex = TexturesLoader.getQuad(array1[1]);
						if (tex != null) this.contents.add(tex);
					}
					if (array1.length > 2) {//ajoute une légende si il y en a une.
						String[] legArray = new String[array1.length - 2];
						System.arraycopy(array1, 2, legArray, 0, legArray.length);
						String leg = Util.fillStringArray(legArray, " ");
						this.contents.add(new Legende(leg));
					}
				} else if ("p".equals(tag)) {
					this.insertString("\n");
				}
			}
		}
		
		// Séparation des paragraphes pour permettre l'optimisation
		ArrayList<Object> rebuiltList = new ArrayList<Object>();
		
		for (Object item : this.contents) {
			if (item instanceof String) {
				String[] paragraphs = item.toString().split("\n");
				Util.addAll(paragraphs, rebuiltList, false);
			}
			else {
				rebuiltList.add(item);
			}
		}
		
		this.contents = rebuiltList;
	}
	
	public Object[] getContents() {
		return this.contents.toArray();
	}
	
	private Object getLast() {
		if (this.contents.size() == 0) return null;
		return this.contents.get(this.contents.size() - 1);
	}
	
	private void setLast(Object c) {
		if (this.contents.size() == 0) return;
		this.contents.set(this.contents.size() - 1, c);
	}
	
	private void insertString(String toInsert) {
		if (toInsert == null || toInsert.length() == 0) {
			return;
		}
		
		if (getLast() instanceof String) {
			String lastText = getLast().toString();
			lastText += " " + toInsert;
			setLast(lastText);
		}
		else {
			this.contents.add(toInsert);
		}
	}
	
	/**
	 * Une légende d'image.
	 * @author Louis JEAN
	 *
	 */
	public static class Legende {
		public String légende;
		
		public Legende(){}
		public Legende(String légende) {this.légende = légende;}
	}
}
