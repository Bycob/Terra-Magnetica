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

package org.terramagnetica.creator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.terramagnetica.creator.lvldefault.ModuleLevelDefault;
import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.Level;
import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.game.lvldefault.LevelDefault;
import org.terramagnetica.ressources.ExternalFilesManager;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.RessourcesManager;
import org.terramagnetica.ressources.TextureSet;

import net.bynaryscode.util.Util;
import net.bynaryscode.util.swing.SwingUtil;

@SuppressWarnings("serial")
public class Creator extends JFrame {
	
	public static final String WINDOW_TITLE = "Editeur de niveaux (Terra Magnetica V" + TerraMagnetica.VERSION + ")";
	
	public static void main(String[] arguments) {
		//Passage au L&F système
		SwingUtil.setUpSystemLF();
		
		//Affichage du splash screen.
		RessourcesManager.loadCreatorSplashScreen();
		EditorSplashScreen splash = new EditorSplashScreen();
		
		//Chargement des ressources.
		RessourcesManager.loadRessourcesCreator();
		String iniFile = ExternalFilesManager.readCreatorIniFile();
		
		splash.dispose();
		
		//Création de la fenêtre.
		Creator theCreator = new Creator();
		theCreator.parseIniFile(iniFile);
		
		//Traitement des arguments.
		if (arguments.length >= 1) {
			String fileName = "";
			for (String arg : arguments) fileName += arg + " ";
			
			theCreator.theLevel = theCreator.saver.open(new File(fileName.substring(0, fileName.length() - 1)));
			if (theCreator.theLevel != null) {
				theCreator.onOpen();
			}
		}
	}
	
	public CreatorModule selectModuleForLevel(Level level) {
		if (level instanceof LevelDefault) {
			return new ModuleLevelDefault(this, (LevelDefault) level);
		}
		
		return null;
	}
	
	
	//FICHIER .INI
	private ArrayList<String> recentOpened = new ArrayList<String>();
	public static final int MAX_SIZE_RECENTOPENED = 10;
	
	public void addRecentOpenedFile(String fullPath) {
		if (fullPath == null || "".equals(fullPath)) {
			return;
		}
		
		this.recentOpened.remove(fullPath);
		this.recentOpened.add(fullPath);
		
		while (this.recentOpened.size() > MAX_SIZE_RECENTOPENED) {
			this.recentOpened.remove(0);
		}
	}
	
	private static final String OUVERTS_RECEMMENT = "ouverts recemment";
	
	protected void parseIniFile(String iniContent) {
		String[] lines = iniContent.split("\n");
		
		//marqueurs indiquant les états de lecture (feat La machine de Turing)
		boolean marq0 = false;
		
		for (int i = 0 ; i < lines.length ; i++) {
			String line = lines[i];
			
			if (marq0) {
				if ("".equals(line)) {
					marq0 = false;
				}
				
				File f = new File(line);
				if (f.exists()) {
					this.addRecentOpenedFile(line);
				}
			}
			
			if (OUVERTS_RECEMMENT.equals(line)) {
				marq0 = true;
				continue;
			}
		}
		
		update();
	}
	
	protected String buildIniFile() {
		StringBuilder sb = new StringBuilder(1024);
		sb.append(OUVERTS_RECEMMENT + "\r\n");
		
		for (String filePath : this.recentOpened) {
			sb.append(filePath).append("\r\n");
		}
		
		sb.append("\n");
		
		return sb.toString();
	}
	
	//-----
	
	private CreatorModule modifier = null;
	private Level theLevel = null;
	
	private NiveauSaver saver = new NiveauSaver();
	
	private JPanel content = new JPanel();
	private JMenuBar bar = new JMenuBar();
	private JMenuItem save = new JMenuItem("Enregistrer"),
			saveAs = new JMenuItem("Enregistrer sous..."),
			nouveau = new JMenuItem("Nouveau..."),
			open = new JMenuItem("Ouvrir..."),
			annul = new JMenuItem("Annuler"),
			retablir = new JMenuItem("Rétablir"),
			test = new JMenuItem("Tester le niveau");
	private JMenuItem new1 = new JMenuItem("niveau normal");
	
	private JMenu openRecent = new JMenu("Ouverts récemments");
	
	public Creator() {
		super();
		this.setTitle(WINDOW_TITLE);
		this.setSize(800,600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setIconImage(ImagesLoader.aimantIcon);
		
		this.initMenu();
		
		this.update();
		
		this.setVisible(true);
	}
	
	private void initMenu() {
		save.addActionListener(new SaveListener());
		saveAs.addActionListener(new SaveAsListener());
		nouveau.addActionListener(new NewListener());
		open.addActionListener(new OpenListener());
		test.addActionListener(new TestListener());
		
		save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		saveAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		nouveau.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		test.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.ALT_DOWN_MASK));
	}
	
	private void update() {
		//Menus
		this.updateRecentOpenedMenu();
		
		this.test.setEnabled(false);
		
		this.bar = new JMenuBar();
		
		JMenu fichier = new JMenu("Fichier");
		JMenu edit = new JMenu("Edition");
		JMenu display = new JMenu("Affichage");
		JMenu toolsMenu = new JMenu("Outils");
		
		fichier.add(this.nouveau);
		fichier.add(this.open);
		fichier.add(this.openRecent);
		fichier.addSeparator();
		fichier.add(this.save);
		fichier.add(this.saveAs);
		this.save.setEnabled(false);
		this.saveAs.setEnabled(false);
		
		edit.add(this.annul);
		edit.add(this.retablir);
		//edit.addSeparator();
		this.annul.setEnabled(false);
		this.retablir.setEnabled(false);
		
		toolsMenu.add(this.test);
		
		this.bar.add(fichier);
		this.bar.add(edit);
		this.bar.add(display);
		this.bar.add(toolsMenu);
		
		this.setJMenuBar(this.bar);
		
		this.content = new JPanel();
		this.setContentPane(this.content);
		
		if (this.modifier != null) {
			this.content.setLayout(new BorderLayout());
			
			//Menus
			if (this.theLevel != null) {
				this.save.setEnabled(true);
				this.saveAs.setEnabled(true);
				
				this.test.setEnabled(true);
			}
			
			if (this.modifier.getToolsMenu().length != 0) toolsMenu.addSeparator();
			if (this.modifier.getDisplayMenu().length == 0) this.bar.remove(display);
			
			addItems(toolsMenu, this.modifier.getToolsMenu());
			addItems(display, this.modifier.getDisplayMenu());
			
			//Toolbar
			JPanel toolPan = new JPanel();
			toolPan.setLayout(new BoxLayout(toolPan, BoxLayout.LINE_AXIS));
			
			JToolBar draw = this.modifier.getPaintToolBar();
			if (draw != null) {
				toolPan.add(draw);
			}
			
			JToolBar tools = this.modifier.getToolsToolBar();
			if (tools != null) {
				toolPan.add(tools);
			}
			this.content.add(toolPan, BorderLayout.NORTH);
			
			//Composant principal
			this.content.add(this.modifier.getMainPanel(), BorderLayout.CENTER);
		}
		else {
			this.bar.remove(display);
		}
		
		this.revalidate();
		this.content.revalidate();
		this.refreshTitle();
	}
	
	/**
	 * Ajoute les items au menu passé en paramètre. Chaque item
	 * valant {@code null} dans le tableau, représente un séparateur.
	 * @param menu - Le menu auquel ajouter les items passés en
	 * paramètre.
	 * @param items - Un tableau de {@link JMenuItem} contenant les
	 * items du menu qu'il faut ajouter. Chaque valeur nulle sera
	 * interprétée comme un séparateur.
	 * @throws NullPointerException Si {@code menu == null} ou
	 * {@code item == null}.
	 */
	private void addItems(JMenu menu, JMenuItem[] items) {
		if (items != null) {
			for (JMenuItem item : items) {
				if (item != null) menu.add(item);
				else menu.addSeparator();
			}
		}
	}
	
	private void updateRecentOpenedMenu() {
		
		this.openRecent.removeAll();
		
		for (int i = this.recentOpened.size() - 1 ; i > -1 ; i--) {
			String filePath = this.recentOpened.get(i);
			
			String fileName = (new File(filePath).getName());
			JMenuItem item = new JMenuItem(fileName);
			item.addActionListener(new OpenRecent(filePath));
			
			this.openRecent.add(item);
		}
		
		if (this.openRecent.getItemCount() == 0) {
			this.openRecent.add(new JMenuItem("..."));
		}
	}
	
	@Override
	public void dispose(){
		if (this.testWindow != null && this.testWindow.isRunning()) this.testWindow.stop();
		if (checkSaved()) {
			ExternalFilesManager.writeCreatorIniFile(buildIniFile());
			
			super.dispose();
		}
	}
	
	public boolean checkSaved() {
		if (!saver.isSaved(theLevel)){
			int option = JOptionPane.showConfirmDialog(null,
					"<html>Voulez-vous sauvegarder ?<br>" +
					" Si vous ne sauvegardez pas, les dernières modifications seront perdues... à jamais !</html>",
					"Fichier non sauvegardé",
					JOptionPane.YES_NO_CANCEL_OPTION);
			if (option == JOptionPane.NO_OPTION)
				return true;
			if (option == JOptionPane.YES_OPTION){
				saver.save(theLevel);
				return true;
			}
			else return false;
		}
		return true;
	}
	
	public boolean createLevelWithItem(JMenuItem item) {
		//Sauvegarde de l'ancien niveau et module d'édition
		Level oldLevel = this.theLevel;
		CreatorModule oldModifier = this.modifier;
		
		//Selection du module d'édition associé
		if (item == this.nouveau) {
			this.modifier = new ModuleLevelDefault(this, ModuleLevelDefault.createLevel(this));
		}
		
		//Annulation si aucun module n'a été trouvé.
		if (this.modifier == null) {
			this.theLevel = oldLevel;
			this.modifier = oldModifier;
			return false;
		}
		
		this.theLevel = this.modifier.getLevel();
		
		//Si le niveau n'est pas créé, on revient aux anciens niveau et module d'édition.
		if (this.theLevel == null) {
			this.theLevel = oldLevel;
			this.modifier = oldModifier;
			return false;
		}
		
		return true;
	}
	
	public void refreshTitle() {
		String fileName = this.saver.getCurrentFileName();
		if (fileName.equals("")) {
			this.setTitle(WINDOW_TITLE);
		}
		else {
			this.setTitle(WINDOW_TITLE + " - " + fileName);
		}
	}
	
	public NiveauSaver getSaver() {
		return this.saver;
	}
	
	private TextureSet loaded = null;
	
	/** Cette méthode vérifie si le niveau modifié est un niveau du
	 * jeu (utilisable uniquement en mode "op").
	 * <p>Si oui, détermine le numéro de ce niveau. Le numéro sera 
	 * utilisé pour charger les textures correspondant à ce niveau,
	 * en plus des textures chargées habituellement.*/
	private void updateLevelID() {
		if (this.loaded != null) {
			//TODO ImagesLoader.unloadTextureSet(...)
		}
		
		String name = this.saver.getCurrentFileName();
		
		if (name.startsWith("niveau")) {
			String levelID = name.substring(6, name.lastIndexOf("."));
			int[] parsing = Util.extractIntegers(levelID);
			
			if (parsing.length != 0 && parsing[0] <= RessourcesManager.NB_LEVEL) {
				int id = parsing[0];
				
				boolean setID = true;
				if (!TerraMagnetica.op) {
					int option = JOptionPane.showConfirmDialog(this,
							"Le nom du fichier indique que celui ci pourrait contenir du contenu additionnel. Charger" +
									" ce contenu ?",
							"Contenu additionnel", JOptionPane.YES_NO_OPTION);
					setID = option == JOptionPane.YES_OPTION;
				}
				
				if (setID) {
					this.theLevel.levelID = id;
					ImagesLoader.loadTextureSet(GameRessources.getTextureSetByLevel(id));
				}
			}
		}
	}
	
	/** Effectue les actions nécessaires à chaque ouverture de fichiers. */
	protected void onOpen() {
		if (theLevel != null) {
			updateLevelID();
			
			modifier = Creator.this.selectModuleForLevel(theLevel);
			
			File file = this.saver.getCurrentFile();
			if (file != null) {
				this.addRecentOpenedFile(file.getPath());
			}
		}
		else {
			modifier = null;
		}
		
		update();
	}

	/** Effectue les actions nécessaires à chaque sauvegardes. */
	protected void onSave() {
		refreshTitle();
		
		File file = this.saver.getCurrentFile();
		if (file != null) {
			this.addRecentOpenedFile(file.getPath());
		}
		updateLevelID();
		
		update();
	}
	
	
	
	class SaveListener implements ActionListener, Runnable {
		@Override
		public void actionPerformed(ActionEvent event){
			new Thread(this).start();
		}
		
		@Override
		public void run(){
			if (modifier != null) {
				modifier.refreshData();
				saver.save(theLevel);
				onSave();
			}
		}
	}
	
	class SaveAsListener implements ActionListener, Runnable {
		@Override
		public void actionPerformed(ActionEvent event){
			new Thread(this).start();
		}
		
		@Override
		public void run(){
			if (modifier != null) {
				modifier.refreshData();
				saver.saveAs(theLevel);
				onSave();
			}
		}
	}
	
	class NewListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event){
			if (!checkSaved()) {
				return;
			}
			if (event.getSource() instanceof JMenuItem) {
				if (!createLevelWithItem((JMenuItem) event.getSource())) return;
			}
			saver.reinit();
			if (modifier != null) {
				modifier.refreshData();
			}
			
			update();
		}
	}
	
	
	
	class OpenListener implements ActionListener, Runnable {
		@Override
		public void actionPerformed(ActionEvent event){
			new Thread(this).start();
		}
		
		@Override
		public void run(){
			if (!checkSaved()) {
				return;
			}
			
			Level load = saver.open();
			if (load == null) return;
			
			theLevel = load;
			onOpen();
		}
	}
	
	class OpenRecent implements ActionListener, Runnable {
		
		private String fullPath;
		
		public OpenRecent(String fullPath) {
			this.fullPath = fullPath;
		}
		
		@Override
		public void actionPerformed(ActionEvent evt) {
			new Thread(this, "Open file thread").start();
		}
		
		@Override
		public void run() {
			if (!checkSaved()) return;

			Level load = saver.open(new File(fullPath));
			if (load == null) return;
			
			theLevel = load;
			if (theLevel == null) {
				recentOpened.remove(fullPath);
			}
			
			onOpen();
		}
	}
	
	public TestWindow testWindow;
	
	class TestListener implements ActionListener, Runnable {
		
		@Override
		public void run() {
			testWindow = new TestWindow(theLevel);
			testWindow.pointer = Creator.this;
			try {
				testWindow.start();
			} catch (Throwable e) {
				Creator.this.requestFocus();
				JOptionPane.showMessageDialog(Creator.this,
						
						"<html><br>Problème rencontré lors du lancement :<br> "
						+ e.getClass().getName() + " : " + e.getMessage() + "<html>",
						
						"Erreur", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				testWindow.destroyApp();
			}
			testWindow = null;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (theLevel == null) {
				return;
			}
			
			if (!theLevel.isRunnable()) {
				JOptionPane.showMessageDialog(Creator.this,
						"<html>Le niveau n'est pas complet.<html>",
						"test impossible", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (testWindow == null) {
				Thread t = new Thread(this);
				t.start();
			}
			else {
				JOptionPane.showMessageDialog(Creator.this,
						"<html>Impossible de tester deux niveaux en même temps, veuillez fermer" +
						"<br>la fenêtre de test en cours.<html>",
						"test impossible", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
