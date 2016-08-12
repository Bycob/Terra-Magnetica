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

package debug;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Un générateur de fichier de police.
 * @author Louis JEAN
 *
 */
public class FontMachine {
	
	private Font font;
	private String fontName;
	private String appended;
	private int nbChars;
	private int nFile;
	private BufferedImage current;
	private Graphics2D graph;
	
	/**
	 * Créé un générateur de fichier avec le nom de police
	 * spécifié.
	 * @param fontName - Le nom de la police.
	 */
	public FontMachine(String fontName) {
		this.fontName = fontName;
	}
	
	/**
	 * Génère des fichiers de police de caractère. Les fichiers générés
	 * sont :
	 * <ul>
	 * <li>Le dossier qui contient tous les fichiers, portant exactement
	 * le nom du fichier passé en paramètre.</li>
	 * <li>Des fichiers images, au format png. Chaque fichier porte le nom
	 * du dossier, plus un numéro servant à l'identifier.
	 * <br>Les lettres sont dessinées sur chaque images sur 8 colonnes
	 * comportant 8 lignes chacunes.</li>
	 * <li>Un fichiers texte, qui contient l'ordre des lettres dans les
	 * fichiers image. Il contient d'abord les lettres du fichier 0, puis
	 * celle du 1 etc...</li>
	 * </ul>
	 * @param fileName - Le nom utilisé pour tous les fichiers. Ne doit pas
	 * être un chemin, simplement un nom. (Ne doit en aucun cas contenir de
	 * '/').
	 * @param resolution - La résolution d'une lettre. Autrement dit la taille
	 * de police dans laquelle elle sera dessinée. La taille des images
	 * est aussi adaptée.
	 */
	public void generate(String fileName, int resolution) {
		//caractères à génerer : 33 à 126, puis 161 à 255
		//caractères espacés : 168, 34
		File f = new File(fileName);
		f.mkdir();
		
		this.appended = "";
		this.nbChars = 0;
		this.nFile = -1;
		this.font = new Font(this.fontName, Font.BOLD, (int) (resolution * 0.85f));
		this.nextImage(resolution);
		
		for (int i = 33 ; i < 127 ; i++) {
			generateChar(fileName, resolution, (char) i);
		}
		
		for (int i = 161 ; i < 256 ; i++) {
			generateChar(fileName, resolution, (char) i);
		}
		
		if (this.nbChars != 0) {
			this.saveImage(fileName);
		}
		
		BufferedWriter w = null;
		try {
			w = new BufferedWriter(new FileWriter(fileName + "\\" + fileName + ".txt"));
			w.write(this.appended);
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void nextImage(int resolution) {
		this.current = new BufferedImage(resolution * 8, resolution * 8, BufferedImage.TYPE_INT_ARGB);
		this.graph = this.current.createGraphics();
		this.graph.setFont(this.font);
		this.graph.setColor(Color.WHITE);
		this.graph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
		this.graph.fillRect(0, 0, this.current.getWidth(), this.current.getHeight());
		this.graph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
		this.nFile++;
	}
	
	private void generateChar(String fileName, int resolution, char character) {
		if (this.nbChars == 64) {
			this.saveImage(fileName);
			this.nextImage(resolution);
			this.nbChars = 0;
		}
	
		int xChar = this.nbChars % 8;
		int yChar = (this.nbChars - xChar) / 8 + 1;
		
		int xp = xChar * resolution;
		if (xp == 0) xp += 1;
		int yp = yChar * resolution - resolution / 5;
		
		this.graph.setClip(xp, yChar * resolution - resolution, resolution, resolution);
		this.graph.drawString(Character.toString(character), xp, yp);
		
		this.nbChars++;
		this.appended = this.appended + character;
		if (xChar == 7) {
			this.appended += "\n";
		}
	}
	
	private void saveImage(String fileName) {
		String finalFN = fileName + "\\" + fileName + this.nFile + ".png";
		try {
			ImageIO.write(this.current, "png", new File(finalFN));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
