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

package org.terramagnetica.game;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.terramagnetica.game.gui.GameWindow;
import org.terramagnetica.ressources.ExternalFilesManager;
import org.terramagnetica.ressources.ModelLoader;
import org.terramagnetica.ressources.RessourcesManager;
import org.terramagnetica.ressources.TextureSet;
import org.terramagnetica.ressources.TexturesLoader;
import org.terramagnetica.utile.GameException;
import org.terramagnetica.utile.RuntimeGameException;

import net.bynaryscode.util.Log;

/**
 * Cette classe est la classe principale du jeu "Terra Magnetica", et gère
 * les sauvegardes et les niveaux. C'est elle qui détermine quel niveau doit
 * être joué lorsque l'utilisateur lance le mode histoire ou jeu libre. C'est
 * elle qui execute l'écriture des sauvegardes, quelles qu'elles soient,
 * sur le disque dur.
 * <p>En plus de cela, elle dispose également de quelques méthode utilitaires.
 * @author Louis JEAN
 */
public class TerraMagnetica {
	
	public static final String VERSION = "0.4.2";
	/** Cette variable indique si le programme est lancé en mode 'operateur' ou non.
	 * Si oui certaines fonctionalités supplémentaires sont activées, notament dans
	 * l'éditeur de niveau. */
	public static final boolean dev = true;
	
	/** L'instance unique du jeu qui tournera dans le programme */
	public static TerraMagnetica theGame = new TerraMagnetica();
	
	public GameEngine engine;
	public Sauvegarde save;
	
	public Options options;
	
	public Log log;
	
	private TerraMagnetica() {
		this.options = new Options();
	}
	
	
	
	//GESTION DES SAUVEGARDES
	
	
	
	public void onClose() {
		if (this.engine != null) {
			if (this.engine.isRunning()) {
				this.engine.stop();
			}
			if (this.engine.history) {
				this.exitHistory(this.engine);
			}
		}
		
		this.saveAll();
	}
	
	
	/**
	 * Sauvegarde toutes les informations sur le jeu dans les fichiers
	 * propres à ces sauvegardes : partie du joueur, options.
	 */
	public void saveAll() {
		if (this.save != null) {
			if (this.engine != null && this.engine.history) {
				this.saveHistory(this.engine);
			}
			ExternalFilesManager.save(this.save);
		}
		
		StringBuilder sb = new StringBuilder(1024);
		this.options.writeOptions(sb);
		ExternalFilesManager.saveOptions(sb.toString().replace("\n", "\r\n"));
	}
	
	/**
	 * Crée une nouvelle partie.
	 * @param name
	 * @throws GameException - Si le nom indiqué est déjà pris pour une
	 * autre partie, ou qu'il y a trop de sauvegardes créées.
	 */
	public void createNewSauvegarde(String name) throws GameException {
		try {
			this.save = new Sauvegarde(name);
			ExternalFilesManager.infos.registerSave(this.save);
		} catch (GameException e) {
			this.save = null;
			throw new GameException("Nom déjà utilisé pour la sauvegarde !");
		} catch (RuntimeGameException e) {
			this.save = null;
			throw new GameException("Trop de sauvegardes !");
		}
	}

	public void exitCurrentSave() {
		this.saveAll();
		this.save = null;
	}
	
	
	
	//LANCEMENT DES NIVEAUX
	
	
	
	/** Lance le niveau auquel s'est arrêté le joueur dans le mode
	 * histoire. */
	public void runHistory() {
		if (this.save != null) {
			Sauvegarde_Histoire story = this.save.getStory();
			setGameEngine(story.getGamePlaying());
		}
	}
	
	public void runFreeLevel(int lvl) {
		if (lvl >= RessourcesManager.NB_LEVEL || lvl < 0) throw new IllegalArgumentException("Niveau invalide.");
		Level freeLvl = RessourcesManager.getLevel(lvl);
		setGameEngine(freeLvl.createGameEngine());
	}
	
	/**
	 * Lance le niveau passé en paramètres.
	 * @param lvl - Le niveau à lancer.
	 * @throws GameException Si le niveau ne peut pas être lancé. Cela
	 * arrive lorsqu'au moins une des salles est dépourvu de point
	 * d'apparitions du joueur (C'est à dire de décor non inaccessible).
	 */
	public void runLevel(Level lvl) throws GameException {
		if (!lvl.isRunnable()) throw new GameException("Impossible de lancer le niveau !");
		
		setGameEngine(lvl.createGameEngine());
	}
	
	private void setGameEngine(GameEngine game) {
		this.engine = game;
		if (game == null) goBackToGui();
		else {
			for (TextureSet set : this.engine.getTextures()) {
				TexturesLoader.loadTextureSet(set);
			}
			ModelLoader.loadModelSet(this.engine.getModels(), true);
		}
		
		//Maintenant toutes les textures sont chargées.
		this.engine.recreateRenders();
	}
	
	/**
	 * Revient à l'interface graphique et libère la mémoire
	 * allouées au ressources du jeu.
	 */
	private void goBackToGui() {
		if (this.engine == null) return;
		for (TextureSet set : this.engine.getTextures()) {
			TexturesLoader.unloadTextureSet(set);
		}
		ModelLoader.unloadAll();
		this.engine = null;
	}
	
	/**
	 * Permet de sauvegarder la progression du joueur en mode histoire
	 * dans l'objet {@code Sauvegarde} actuellement utilisé, représentant
	 * les données du joueur.
	 * <p>Les données ne sont pas écrite dans le fichier sauvegarde, voir
	 * la méthode {@link #saveAll()}.
	 * @param toSave - Le moteur de jeu qui contient les informations de 
	 * progression du joueur dans le niveau qu'il parcourt actuellement.
	 */
	public void saveHistory(GameEngine toSave) {
		if (this.save != null && toSave != null) {
			Sauvegarde_Histoire history = this.save.getStory();
			history.saveGamePlaying(toSave);
			
			ExternalFilesManager.infos.update(this.save);
		}
	}
	
	/** Enregistre la partie du joueur en mode histoire.
	 * Arrête le moteur de jeu, décharge toutes les textures.
	 * @param toSave
	 */
	public void exitHistory(GameEngine toSave) {
		saveHistory(toSave);
		goBackToGui();
	}
	
	/**
	 * Arrête le moteur de jeu, décharge toutes les textures.
	 */
	public void exitGamePlaying() {
		goBackToGui();
	}
	
	/**
	 * Affiche un message d'information pour l'utilisateur dans le jeu.
	 * @param msg - Le message à afficher.
	 */
	public void displayMessage(String msg) {
		GameWindow.getInstance().displayMessage(msg);
	}
	
	public int nbFreeHistoryLevel() {
		return this.save.nbFreeHistoryLevel();
	}
	
	/** indique si le niveau à l'index indiqué est disponible en
	 * mode jeu libre. */
	public boolean available(int level) {
		return this.save.isAvailable(level);
	}
	
	/**
	 * Remplace tous les tags connus de la chaine par les infos
	 * du jeu correspondantes.
	 * <br>Tags remplacés :
	 * <ul><li>{@literal <pname>} : désigne le nom du joueur de la partie
	 * en cours.
	 * </ul>
	 * @param o - la chaine d'origine.
	 * @return La chaine transformée.
	 */
	public String gameStringFormat(String o) {
		if (this.save != null) {
			o = o.replace("<pname>", this.save.getName());
		}
		return o;
	}
	
	public static void main(String[] params) {
		
		//Certaine boîtes de dialogue (JFileChooser) sont affichées avec Swing
		//SwingUtil.setUpSystemLF();
		//TODO le JFileChooser est très lent avec le look&feel system
		
		avertissement();
		
		//Log
		final TerraMagnetica GAME = TerraMagnetica.theGame;
		GAME.log = ExternalFilesManager.loadLog();
		
		try {
			GameWindow.createInstance();
			GameWindow.getInstance().start();
		} catch (Throwable e) {
			e.printStackTrace();
			GAME.log.addErrorMessage("Crash de l'application : ", e);
		}
		
		//Ecriture du log
		try {
			Log.writeLog(GAME.log);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** avertissement avant installation. */
	public static void avertissement() {
		if (!ExternalFilesManager.isTMInstalled()) {
			int option = JOptionPane.showConfirmDialog(null,
					
					"<html>Terra Magnetica V" + VERSION + " n'est pas encore installé sur votre ordinateur<br>" +
					"Pour fonctionner, le jeu va créer le dossier suivant : <br><br>" +
					"<blockquote>" + ExternalFilesManager.getAppDataLocation() + "</blockquote><br>" +
					"Voulez-vous installer Terra Magnetica V" + VERSION + " ?" +
					"</html>",
					
					"Installation de Terra Magnetica", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (option != JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}
}
