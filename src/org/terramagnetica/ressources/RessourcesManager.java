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

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.Level;
import org.terramagnetica.game.NullDescriptionException;
import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.opengl.gui.GuiTextPainter;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.GameIOException;
import org.terramagnetica.utile.ImgUtil;

public class RessourcesManager {
	
	private static boolean gameRessourcesLoaded = false;
	private static String resLoc = "assets/";
	
	public static final int NB_LEVEL = 2;
	
	/** Icone du jeu */
	public static ByteBuffer tmIcon;
	
	private RessourcesManager(){};
	
	/** Charge les ressources du jeu.
	 * Cela comprend les textures, les polices de caractère, les
	 * informations de sauvegarde, les options, les sons. */
	public static void loadRessourcesGame(){
		TexturesLoader.loadTextureSet(GameRessources.guiTextureSet);
		
		GuiTextPainter.init();
		
		ExternalFilesManager.loadSauvegardesInfos();
		String options = ExternalFilesManager.getOptions();
		TerraMagnetica.theGame.options.loadOptions(options);
		
		SoundManager.loadAudio();
		
		gameRessourcesLoaded = true;
	}
	
	/** Charge l'image d'écran de démarrage seul. */
	public static void loadGameStartScreen() {
		
	}
	
	/**
	 * Charge l'icone du jeu. Celui-ci est ensuite accessible via
	 * {@link TexturesLoader#aimantIcon}.
	 */
	public static void loadIcon() {
		BufferedImage img = ImagesLoader.readImage("gui/game/tm32.png");
		if (img != null) tmIcon = ImgUtil.imageToByteBuffer(img);
	}
	
	/**
	 * charge les ressources du créateur de niveaux.
	 */
	public static void loadRessourcesCreator(){
		GameRessources.initClassDef();
		
		ImagesLoader.loadImages();
		ImagesLoader.loadCreatorGui();
		
		//TODO------ DEBUG ---------> A chaque fois qu'une image est ajoutée, vérifier le nombre d'image total.
		//System.out.println(ImagesLoader.imgLoadedCount());
		//Puis modifier la constante : ThreadImageLoadingObserver.NB_IMAGES
	}
	
	public static void loadCreatorSplashScreen() {
		ImagesLoader.splashScreen = ImagesLoader.readImage("gui/creator/splashscreen.png");
		ImagesLoader.aimantIcon = ImagesLoader.readImage("gui/creator/tm_editor32.png");
	}
	
	/** 
	 * @return true si les ressources du jeu sont chargées, false sinon. */
	public static boolean areGameRessourcesLoaded() {
		return gameRessourcesLoaded;
	}
	
	
	
	/** 
	 * Donne la localisation des ressources de l'application de façon
	 * générale (car les textures, modèles et sons peuvent avoir des
	 * emplacements spécifiques).
	 * @return Le chemin du dossier qui contient toutes les ressources
	 * de l'application. Si la chaîne retournée est "", cela signifie
	 * que les ressources sont situées dans l'archive .jar, plus précisément
	 * dans le package {@code org.terramagnetica.ressources}.
	 */
	public static String getRessourcesLocation() {
		return resLoc;
	}
	
	/**
	 * Permet d'obtenir une URL valable pour la ressource
	 * passée en paramètre. Si aucune URL n'est trouvée, lance
	 * une {@link FileNotFoundException}.
	 * @param ressource - La ressource dont on veut obtenir une URL.
	 * C'est le chemin de la ressource à partir du dossier qui les
	 * contient toutes.
	 * @return Une URL non-{@code null} qui pointe sur la ressource
	 * voulue.
	 * @throws FileNotFoundException Si l'URL correspondant à la ressource
	 * n'est pas trouvée.
	 */
	public static URL getURL(String ressource) throws FileNotFoundException {
		URL ret = null;
		
		if (getRessourcesLocation().equals("")) {
			ret = RessourcesManager.class.getResource(ressource);
		}
		else {
			File location = new File(resLoc);
			try {
				ret = new File(location.getAbsolutePath(), ressource).toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		
		if (ret == null) {
			throw new FileNotFoundException("Fichier introuvable");
		}
		
		return ret;
	}
	
	/**
	 * Lit et retourne le niveau du jeu qui porte le numéro passé en paramètres.
	 * @param id - Le numéro du niveau. Attention le niveau 1 porte le numéro 0.
	 * @return Le niveau du jeu qui correspond au numéro.
	 */
	public static Level getLevel(int id) {
		Level result = null;
		
		try {
			InputStream url = RessourcesManager.getURL("niveaux/niveau" + (id + 1) + ".mlv").openStream();
			result = readLevel(url);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		
		if (result != null) {
			result.levelID = id + 1;
		}
		
		return result;
	}
	
	public static Level getBonusLevel(String levelPath) {
		Level result = null;
		
		try {
			InputStream url = RessourcesManager.getURL("niveaux/bonus/" + levelPath).openStream();
			result = readLevel(url);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Lit un niveau à partir d'un flux passé en paramètre.
	 * @param stream - Le flux depuis le fichier.
	 * @return Le niveau contenu par le fichier, ou {@code null}
	 * si le fichier ne contient pas un niveau valide, ou n'existe
	 * pas.
	 */
	static Level readLevel(InputStream stream) {
		BufferedObjectInputStream ois = null;
		Level result = null;
		
		try{
			ois = new BufferedObjectInputStream(new BufferedInputStream(stream));
			result = ois.readLevel();
			ois.close();
			
		} catch (NullPointerException e){
			e.printStackTrace();
		} catch (FileNotFoundException e){
			e.printStackTrace();
		} catch (GameIOException e) {
			//essai à la déserialization.
			ObjectInputStream ois2 = null;
			try {
				ois2 = new ObjectInputStream(new BufferedInputStream(stream));
				
				result = (Level) ois2.readObject();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} finally {
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		} finally{
			try {
				if (ois != null)
					ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * Lit le fichier contenant les information sur le niveau et le
	 * retourne. Si une erreur quelquonque survient ou que le fichier
	 * n'existe pas, lance une {@link NullDescriptionException}.
	 * @param id - L'identifiant du niveau.
	 * @return
	 */
	public static String getLevelPlot(int id) throws NullDescriptionException {
		if (id == NB_LEVEL) return "";
		final String file = "niveaux/niveau" + String.valueOf(id + 1) + ".txt";
		StringBuilder sb = new StringBuilder(1024);
		BufferedInputStream stream = null;
		
		try {
			stream = new BufferedInputStream(RessourcesManager.getURL(file).openStream());
			readFileString(stream, sb);
		} catch (IOException e) {
			throw new NullDescriptionException(e);
		}
		
		return TerraMagnetica.theGame.gameStringFormat(sb.toString());
	}
	
	/**
	 * Lit la chaine de caractère contenue dans un fichier.
	 * @param stream - le flux depuis le fichier
	 * @param sb - le {@link StringBuilder} dans lequel est écrite
	 * la chaine lue. Faire {@link StringBuilder#toString()} pour la
	 * récupérer.
	 * @throws IOException Si une erreur d'entrée / sortie survient.
	 */
	public static final void readFileString(InputStream stream, StringBuilder sb) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(stream));
		String line = "";
		
		try {
			while ((line = r.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
		}
		finally {
			if (r != null) {
				try {
					r.close();
				} catch (Exception e) {}
			}
		}
	}
	
	/**
	 * Ecrit une chaine de caractère dans un fichier texte.
	 * @param out - le flux vers le fichier.
	 * @param file - la chaine à écrire.
	 * @throws IOException Si une erreur d'entrée / sortie survient.
	 */
	public static final void writeFileString(OutputStream out, String file) throws IOException {
		BufferedWriter w = null;
		w = new BufferedWriter(new OutputStreamWriter(out));
		
		try {
			w.write(file);
		} 
		finally {
			if (w != null) {
				try {
					w.close();
				} catch (Exception e) {}
			}
		}
	}
}
