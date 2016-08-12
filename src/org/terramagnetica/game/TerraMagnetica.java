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
 * Cette classe est la classe principale du jeu "Terra Magnetica", et g�re
 * les sauvegardes et les niveaux. C'est elle qui d�termine quel niveau doit
 * �tre jou� lorsque l'utilisateur lance le mode histoire ou jeu libre. C'est
 * elle qui execute l'�criture des sauvegardes, quelles qu'elles soient,
 * sur le disque dur.
 * <p>En plus de cela, elle dispose �galement de quelques m�thode utilitaires.
 * @author Louis JEAN
 */
public class TerraMagnetica {
	
	public static final String VERSION = "0.4.2";
	/** Cette variable indique si le programme est lanc� en mode 'operateur' ou non.
	 * Si oui certaines fonctionalit�s suppl�mentaires sont activ�es, notament dans
	 * l'�diteur de niveau. */
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
	 * propres � ces sauvegardes : partie du joueur, options.
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
	 * Cr�e une nouvelle partie.
	 * @param name
	 * @throws GameException - Si le nom indiqu� est d�j� pris pour une
	 * autre partie, ou qu'il y a trop de sauvegardes cr��es.
	 */
	public void createNewSauvegarde(String name) throws GameException {
		try {
			this.save = new Sauvegarde(name);
			ExternalFilesManager.infos.registerSave(this.save);
		} catch (GameException e) {
			this.save = null;
			throw new GameException("Nom d�j� utilis� pour la sauvegarde !");
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
	
	
	
	/** Lance le niveau auquel s'est arr�t� le joueur dans le mode
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
	 * Lance le niveau pass� en param�tres.
	 * @param lvl - Le niveau � lancer.
	 * @throws GameException Si le niveau ne peut pas �tre lanc�. Cela
	 * arrive lorsqu'au moins une des salles est d�pourvu de point
	 * d'apparitions du joueur (C'est � dire de d�cor non inaccessible).
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
		
		//Maintenant toutes les textures sont charg�es.
		this.engine.recreateRenders();
	}
	
	/**
	 * Revient � l'interface graphique et lib�re la m�moire
	 * allou�es au ressources du jeu.
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
	 * dans l'objet {@code Sauvegarde} actuellement utilis�, repr�sentant
	 * les donn�es du joueur.
	 * <p>Les donn�es ne sont pas �crite dans le fichier sauvegarde, voir
	 * la m�thode {@link #saveAll()}.
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
	 * Arr�te le moteur de jeu, d�charge toutes les textures.
	 * @param toSave
	 */
	public void exitHistory(GameEngine toSave) {
		saveHistory(toSave);
		goBackToGui();
	}
	
	/**
	 * Arr�te le moteur de jeu, d�charge toutes les textures.
	 */
	public void exitGamePlaying() {
		goBackToGui();
	}
	
	/**
	 * Affiche un message d'information pour l'utilisateur dans le jeu.
	 * @param msg - Le message � afficher.
	 */
	public void displayMessage(String msg) {
		GameWindow.getInstance().displayMessage(msg);
	}
	
	public int nbFreeHistoryLevel() {
		return this.save.nbFreeHistoryLevel();
	}
	
	/** indique si le niveau � l'index indiqu� est disponible en
	 * mode jeu libre. */
	public boolean available(int level) {
		return this.save.isAvailable(level);
	}
	
	/**
	 * Remplace tous les tags connus de la chaine par les infos
	 * du jeu correspondantes.
	 * <br>Tags remplac�s :
	 * <ul><li>{@literal <pname>} : d�signe le nom du joueur de la partie
	 * en cours.
	 * </ul>
	 * @param o - la chaine d'origine.
	 * @return La chaine transform�e.
	 */
	public String gameStringFormat(String o) {
		if (this.save != null) {
			o = o.replace("<pname>", this.save.getName());
		}
		return o;
	}
	
	public static void main(String[] params) {
		
		//Certaine bo�tes de dialogue (JFileChooser) sont affich�es avec Swing
		//SwingUtil.setUpSystemLF();
		//TODO le JFileChooser est tr�s lent avec le look&feel system
		
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
					
					"<html>Terra Magnetica V" + VERSION + " n'est pas encore install� sur votre ordinateur<br>" +
					"Pour fonctionner, le jeu va cr�er le dossier suivant : <br><br>" +
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
