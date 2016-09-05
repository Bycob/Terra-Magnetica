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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.game.Level;
import org.terramagnetica.game.Sauvegarde;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;
import org.terramagnetica.utile.RuntimeGameException;

import net.bynaryscode.util.FileFormatException;
import net.bynaryscode.util.Log;
import net.bynaryscode.util.Util;

public class ExternalFilesManager {
	
	private static String appDataLocation = System.getenv("APPDATA") + "\\terramagnetica\\";
	public static String getAppDataLocation() {
		return appDataLocation;
	}
	
	public static SavesInfo infos = new SavesInfo();
	
	//--------------- SAUVEGARDES ---------------
	
	public static void loadSauvegardesInfos(){
		parsePaths();
		infos = new SavesInfo();
		
		for (File save : (new File(appDataLocation + "saves").listFiles())) {
			String name = save.getName();
			if (name.endsWith(".tms")) {
				try {
					Sauvegarde loaded = loadSauvegarde(appDataLocation + "saves/" + name);
					
					if (loaded != null) {
						infos.registerSave(loaded);
					}
				} catch (GameIOException e) {
					e.printStackTrace();
				} catch (RuntimeGameException e) {
					break;
				}
			}
		}
	}
	
	static Sauvegarde loadSauvegarde(String filename) throws GameIOException {
		Sauvegarde read = null;
		BufferedObjectInputStream input = null;
		
		try {
			input = new BufferedObjectInputStream(
					new BufferedInputStream(
							new FileInputStream(filename)));
			
			read = input.readSauvegarde();
			
			input.close();
		} catch (FileNotFoundException e) {
			loadSauvegardesInfos();
			throw new GameIOException("Impossible de charger la sauvegarde", e);
		} catch (IOException e) {
			throw new GameIOException("Impossible de charger la sauvegarde", e);
		}
		finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return read;
	}
	
	/** Charge la sauvegarde correspondant aux données passées
	 * en paramêtre.
	 * @throws GameIOException Si une erreur survient et qu'il est
	 * impossible de charger la sauvegarde. */
	public static Sauvegarde loadSauvegarde(SaveData data) throws GameIOException {
		parsePaths();
		Sauvegarde result = loadSauvegarde(appDataLocation + "saves/" + data.getName() + ".tms");
		if (result != null) {
			result.setID(data.getID());
		}
		return result;
	}

	public static void save(Sauvegarde toSave) {
		parsePaths();
		try {
			infos.update(toSave);
		} catch (NullPointerException e) {
			try {
				infos.registerSave(toSave);
			} catch (RuntimeGameException e1) {
				e1.printStackTrace();
				return;
			}
		}
		
		toSave.updateLastModified();
		String path = appDataLocation + "saves/" + toSave.getName() + ".tms";
		
		BufferedObjectOutputStream out = null;
		
		try {
			out = new BufferedObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path)));
			out.writeSauvegarde(toSave);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void removeSauvegarde(SaveData data) {
		parsePaths();
		infos.removeByID(data.getID());
		File toRemove = new File(appDataLocation + "saves/" + data.getName() + ".tms");
		if (toRemove.exists()) {
			toRemove.delete();
		}
	}
	
	/** Renomme la sauvegarde avec le nouveau nom passé
	 * en paramètre. */
	public static void renameSauvegarde(SaveData data, String newName) {
		parsePaths();
		try {
			Sauvegarde saved = loadSauvegarde(data);
			saved.setName(newName);
			removeSauvegarde(data);
			infos.update(saved);
			save(saved);
		} catch (GameIOException e) {
			e.printStackTrace();
		}
	}
	
	
	//--------------- OPTIONS -------------------
	
	public static String getOptions() {
		String fileName = appDataLocation + "options.ini";
		BufferedInputStream bis = null;
		StringBuilder sb = new StringBuilder((int) new File(fileName).length());
		try {
			bis = new BufferedInputStream(new FileInputStream(fileName));
			RessourcesManager.readFileString(bis, sb);
		} catch (FileNotFoundException e) {
			//possible, dans ce cas pas d'options préenregistrées.
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return sb.toString();
	}
	
	public static void saveOptions(String fileContents) {
		String fileName = appDataLocation + "options.ini";
		
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(fileName));
			RessourcesManager.writeFileString(bos, fileContents);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	//------------ NIVEAUX DU JOUEUR ------------
	
	private static String[] playerLevelList = new String[0];
	
	/**
	 * Met à jour la liste des niveaux installés par le joueur.
	 */
	public static void updatePlayerLevelList() {
		parsePaths();
		String dirName = appDataLocation + "levels/";
		ArrayList<String> fileList = new ArrayList<String>();
		
		File dir = new File(dirName);
		if (!dir.exists()) {
			playerLevelList = new String[0];
			return;
		}
		File[] fileArray = dir.listFiles();
		if (fileArray == null) {
			return;
		}
		
		for (File f : new File(dirName).listFiles()) {
			String fileName = f.getName();
			if (fileName.endsWith(".mlv") && !f.isDirectory()) {
				fileList.add(fileName.substring(0, fileName.length() - 4));
			}
		}
		
		playerLevelList = fileList.toArray(new String[0]);
	}
	
	/** @return La liste des noms des niveaux installés par
	 * le joueur. */
	public static List<String> getPlayerLevelList() {
		return Util.createList(playerLevelList);
	}
	
	/**
	 * Donne le niveau installé par le joueur portant l'indice
	 * passé en paramètre.
	 * @param index - La position du niveau dans la liste des niveaux
	 * installé par le joueur, que l'on peut obtenir avec la méthode
	 * {@link #getPlayerLevelList()}.
	 * @return Le niveau qui correspond à cette position.
	 */
	public static Level getPlayerLevel(int index) {
		if (index >= playerLevelList.length || index < 0) {
			throw new ArrayIndexOutOfBoundsException();
		}
		
		Level result = null;
		String name = appDataLocation + "levels/" + playerLevelList[index] + ".mlv";
		
		try {
			InputStream stream = new FileInputStream(name);
			result = RessourcesManager.readLevel(stream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Permet d'ajouter le niveau contenu dans le fichier passé en paramètre,
	 * aux niveaux installé par le joueur. Le fichier est ainsi déplacé dans
	 * le dossier spécifiques aux niveaux du joueur.
	 * @param path - Le fichier qui contient le niveau à déplacer.
	 * @throws FileFormatException Si le fichier spécifié n'est pas un fichier
	 * de niveau Terra Magnetica (extension ".mlv").
	 * @throws IOException Si une erreur survient lors du transfert.
	 */
	public static void importPlayerLevel(File path) throws FileFormatException, IOException {
		
		if (path == null) throw new NullPointerException("parameter is null");
		if (!path.exists()) throw new IllegalArgumentException("fichier inexistant");
		if (!path.getName().endsWith(".mlv")) 
			throw new FileFormatException("Le fichier n'est pas un fichier de niveau.");
		
		//détermination du nom du fichier
		String name = path.getName();
		
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		
		try {
			bis = new BufferedInputStream(new FileInputStream(path));
			bos = new BufferedOutputStream(
					new FileOutputStream(
							new File(appDataLocation + "levels/" + name)));
			
			Util.copyData(bis, bos);
		}
		finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		updatePlayerLevelList();
	}
	
	
	//--------- FICHIER .INI - EDITEUR ----------
	
	private static String creatorIniPath = appDataLocation + "creator.ini";
	
	public static String readCreatorIniFile() {
		BufferedInputStream bin = null;
		StringBuilder sb = new StringBuilder(1024);
		try {
			bin = new BufferedInputStream(new FileInputStream(creatorIniPath));
			RessourcesManager.readFileString(bin, sb);
		} catch (FileNotFoundException e) {
			//rien : le fichier n'existe pas, renvoie une chaine vide.
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bin != null) {
				try {
					bin.close();
				} catch (IOException e) {}
			}
		}
		
		return sb.toString();
	}
	
	public static void writeCreatorIniFile(String contents) {
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(creatorIniPath));
			RessourcesManager.writeFileString(bos, contents);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//-------------- SCREENSHOTS ----------------
	
	public static void saveScreenshot(BufferedImage img) {

		String date = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
		File f = new File(appDataLocation + "screenshots/" + date + ".png");
		
		int index = 1;
		while (f.exists()) {
			f = new File(appDataLocation + "screenshots/" + date + "(" + index + ").png");
			index++;
		}
		
		try {
			ImageIO.write(img, "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//----------------- LOG ---------------------
	
	public static Log loadLog() {
		Log log = new Log();
		
		try {
			File pathName = new File(appDataLocation + "terramagnetica.log");
			
			Log.loadLog(pathName, log);
		} catch (IOException e) {
			log.addMessage("Aucun log n'a été détecté. Création du fichier terramagnetica.log...");
		}
		
		log.createNewSession("Terra Magnetica V" + TerraMagnetica.VERSION, new Date());
		
		return log;
	}
	
	/**
	 * Vérifie et crée l'arborescence du dossier APP_DATA/terramagnetica
	 * (si elle ne l'est pas déjà).
	 */
	public static void parsePaths() {
		checkDirectory("saves");
		checkDirectory("levels");
		checkDirectory("screenshots");
	}
	
	private static void checkDirectory(String path) {
		File directory = new File(appDataLocation + path);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}
	
	/**
	 * Permet de savoir si le dossier APP_DATA/terramagnetica a été créé sur
	 * l'ordinateur.
	 * @return {@code true} si le dossier de données de l'application est
	 * installé, {@code false} dans le cas contraire.
	 */
	public static boolean isTMInstalled() {
		return (new File(appDataLocation)).exists();
	}
}
