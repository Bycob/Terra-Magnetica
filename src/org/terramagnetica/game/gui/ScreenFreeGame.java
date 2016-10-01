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

package org.terramagnetica.game.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import org.terramagnetica.creator.ExtensionFileFilter;
import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.Level;
import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.gui.Bounds;
import org.terramagnetica.opengl.gui.GuiActionEvent;
import org.terramagnetica.opengl.gui.GuiBorderLayout;
import org.terramagnetica.opengl.gui.GuiButtonText1;
import org.terramagnetica.opengl.gui.GuiContainer;
import org.terramagnetica.opengl.gui.GuiFrameContainer;
import org.terramagnetica.opengl.gui.GuiLabel;
import org.terramagnetica.opengl.gui.GuiLayout002;
import org.terramagnetica.opengl.gui.GuiMovingPanel;
import org.terramagnetica.opengl.gui.GuiScrollPanel;
import org.terramagnetica.opengl.gui.GuiScrollPanel.ScrollBar;
import org.terramagnetica.opengl.gui.GuiVerticalLayout;
import org.terramagnetica.ressources.ExternalFilesManager;
import org.terramagnetica.ressources.RessourcesManager;
import org.terramagnetica.ressources.TexturesLoader;
import org.terramagnetica.utile.GameException;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.FileFormatException;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class ScreenFreeGame extends GameScreen {
	
	private GuiButtonText1 backButton;
	private GuiButtonText1 playerLevelsButton;
	private GuiButtonText1 historyLevelsButton;
	private GuiButtonText1 bonusLevelsButton;
	
	/** Le bouton qui permet d'ajouter des niveaux fait par le joueur.
	 * Il n'apparait que lorsque la liste des niveaux joueur est
	 * affichée. */
	private GuiButtonText1 addButton = new GuiButtonText1("Plus de niveaux...");
	
	/** 0=Liste des niveaux histoire, 1=Liste des niveaux joueur,
	 * 2=Liste des niveaux bonus. */
	private int mode = 0;
	
	private GuiFrameContainer frameCont;
	private GuiContainer centerCont;
	
	//Conteneurs des boutons des niveaux du jeu et des niveaux du joueur
	private GuiButtonFreeLevel lvlButtons[];
	private GuiContainer content;
	private GuiScrollPanel scroll;
	
	//Conteneur des boutons des niveaux bonus.
	private BonusLevelManager bonusLvlManager = new BonusLevelManager();
	
	public ScreenFreeGame() {
		final double borderHeight = 0.3;
		
		this.backButton = new GuiButtonText1(-0.45, 0.1, 0.45, -0.1, "Menu");
		this.historyLevelsButton = new GuiButtonText1("Niveaux du mode histoire");
		this.playerLevelsButton = new GuiButtonText1("Niveaux ajoutés");
		this.bonusLevelsButton = new GuiButtonText1("Niveaux bonus");
		
		//Création du container global.
		this.frameCont = new GuiFrameContainer();
		GuiBorderLayout framLay = new GuiBorderLayout();
		framLay.setBorderHeight(borderHeight);
		this.frameCont.setLayout(framLay);
		
		//Création des container enfants
		GuiContainer titleCont = new GuiContainer();
		GuiContainer buttonCont = new GuiContainer();
		this.centerCont = new GuiContainer();
		Color4f background = new Color4f(0, 0, 0, 32);
		titleCont.setBackground(background);
		buttonCont.setBackground(background);
		this.frameCont.add(titleCont, GuiBorderLayout.TOP);
		this.frameCont.add(buttonCont, GuiBorderLayout.BOTTOM);
		this.frameCont.add(this.centerCont, GuiBorderLayout.CENTER);
		
		//Ajout des boutons du menu au container du menu
		this.updateMenuButtonsPosition();
		
		buttonCont.add(this.backButton);
		buttonCont.add(this.historyLevelsButton);
		buttonCont.add(this.playerLevelsButton);
		buttonCont.add(this.bonusLevelsButton);
		
		//Titre
		GuiLabel title = new GuiLabel("Selectionnez un niveau", 20, 0, 0);
		title.setColor(new Color4f(255, 234, 0));
		titleCont.add(title);
		
		//Boutons de selection des niveaux
		initLevelButtons();
		this.scroll = new GuiScrollPanel(this.content, -1, 0, 1, 0);
		this.scroll.fillBackground(false);
		this.scroll.setColor(new Color4f(209, 182, 0));
		this.scroll.setScrollBarsUsed(ScrollBar.VERTICAL);
		this.centerCont.setLayout(new GuiLayout002());
		this.centerCont.add(this.scroll);
		
		this.add(this.frameCont);
		
		//Ajout de l'adapteur de position
		this.historyLevelsButton.addBehavior(new Bounds(this.historyLevelsButton, buttonCont));
		this.playerLevelsButton.addBehavior(new Bounds(this.playerLevelsButton, buttonCont));
	}
	
	/** Lors d'un changement de section, les boutons sont déplacés.
	 * Cette méthode effectue leur déplacement. */
	private void updateMenuButtonsPosition() {
		final double borderHeight = 0.3;
		
		RectangleDouble left = new RectangleDouble(-2, borderHeight * 0.4, -1, - borderHeight * 0.4);
		RectangleDouble right = new RectangleDouble(1, borderHeight * 0.4, 2, - borderHeight * 0.4);
		
		switch (this.mode) {
		case 0 :
			this.historyLevelsButton.setVisible(false);

			this.bonusLevelsButton.setVisible(true);
			this.bonusLevelsButton.setBoundsGL(left);
			
			this.playerLevelsButton.setVisible(true);
			this.playerLevelsButton.setBoundsGL(right);
			break;
		case 1 :
			this.playerLevelsButton.setVisible(false);
			
			this.historyLevelsButton.setVisible(true);
			this.historyLevelsButton.setBoundsGL(left);
			
			this.bonusLevelsButton.setVisible(true);
			this.bonusLevelsButton.setBoundsGL(right);
			break;
		case 2 :
			this.bonusLevelsButton.setVisible(false);
			
			this.playerLevelsButton.setVisible(true);
			this.playerLevelsButton.setBoundsGL(left);
			
			this.historyLevelsButton.setVisible(true);
			this.historyLevelsButton.setBoundsGL(right);
			break;
		}
	}
	
	/** Cette méthode réinitialise le container des boutons de niveaux,
	 * puis y ajoute les boutons correspondant à la section selectionnée
	 * par le joueur. */
	private void initLevelButtons() {
		int n = 0;//nombre de niveaux à afficher
		switch (this.mode) {
		case 0 :
			n = TerraMagnetica.theGame == null ? 0 : TerraMagnetica.theGame.nbFreeHistoryLevel();
			break;
		case 1 :
			ExternalFilesManager.updatePlayerLevelList();
			n = ExternalFilesManager.getPlayerLevelList().size();
			break;
		case 2 :
		default :
			break;
		}
		
		//Définition des dimensions du container. 
		this.lvlButtons = new GuiButtonFreeLevel[n];
		final double butHeight = 0.4; final double off = 0.03;
		//on ajoute une place pour le bouton "Ajouter"
		this.content = new GuiContainer(0, 0, 1, (n + 1) * (butHeight + off));
		this.content.setLayout(new GuiVerticalLayout(butHeight, off, off));
		RectangleDouble butBounds = new RectangleDouble(0, butHeight, 1, 0);
		
		//Ajout de chaque bouton au container.
		switch (this.mode) {
		default :
			break;
		case 0 :
			for (int i = 0 ; i < RessourcesManager.NB_LEVEL ; i++) {
				if (TerraMagnetica.theGame == null || !TerraMagnetica.theGame.available(i)) continue;
				this.lvlButtons[i] = new GuiButtonFreeLevel(butBounds, i);
				this.content.add(this.lvlButtons[i]);
			}
			break;
		case 1 :
			List<String> lvlList = ExternalFilesManager.getPlayerLevelList();
			for (int i = 0 ; i < lvlList.size() ; i++) {
				this.lvlButtons[i] = new GuiButtonFreeLevel(butBounds, i, lvlList.get(i));
				this.content.add(this.lvlButtons[i]);
			}
			
			this.addButton.setHeightGL(butHeight);
			this.content.add(this.addButton);
			break;
		case 2 :
			//On réinitialise les entrées sur les boutons (pour prévenir certains bugs)
			ArrayList<GuiButtonBonusLevel> bonusButList = this.bonusLvlManager.getAllButtons();
			for (GuiButtonBonusLevel bonusBut : bonusButList) {
				bonusBut.processLogic();
			}
		}
	}
	
	private void setMode(int mode) {
		this.mode = mode;
		this.initLevelButtons();
		
		GuiMovingPanel bonusLvlPanel = this.bonusLvlManager.getPanel();
		this.frameCont.remove(bonusLvlPanel);
		this.frameCont.remove(this.centerCont);
		if (this.mode == 0 || this.mode == 1) {
			this.frameCont.add(this.centerCont, GuiBorderLayout.CENTER);
			this.scroll.setContent(this.content);
		}
		if (this.mode == 2) {
			this.frameCont.add(bonusLvlPanel, GuiBorderLayout.CENTER);
		}
	}
	
	@Override
	protected void onAppear() {
		TexturesLoader.loadTextureSet(GameRessources.freeGameTextureSet);
		for (GuiButtonFreeLevel button : this.lvlButtons) {
			button.reloadTextures();
		}
	}
	
	@Override
	protected void onDestroy() {
		TexturesLoader.unloadTextureSet(GameRessources.freeGameTextureSet);
	}
	
	@Override
	public int timeToAppear() {
		return MILLIS_APPEAR_DEFAULT;
	}
	
	@Override
	public int timeToDestroy() {
		return 0;
	}
	
	@Override
	protected void drawComponent(Painter painter){
		this.drawDefaultBackground(painter);
	}

	@Override
	public GuiActionEvent processLogic() {
		if (this.getState() != VISIBLE) return super.processLogic();
		
		//BOUTON RETOUR
		if (this.backButton.processLogic() == GuiActionEvent.CLICK_EVENT) {
			this.nextPanel(new ScreenInGameMenu());
		}
		
		//BOUTON DES NIVEAUX DU MODE HISTOIRE
		if (this.historyLevelsButton.processLogic() == GuiActionEvent.CLICK_EVENT) {
			this.setMode(0);
			updateMenuButtonsPosition();
		}
		
		//BOUTON DES NIVEAUX DU JOUEUR
		if (this.playerLevelsButton.processLogic() == GuiActionEvent.CLICK_EVENT) {
			this.setMode(1);
			updateMenuButtonsPosition();
		}
		
		//BOUTON DES NIVEAUX BONUS
		if (this.bonusLevelsButton.processLogic() == GuiActionEvent.CLICK_EVENT) {
			this.setMode(2);
			updateMenuButtonsPosition();
		}
		
		//BOUTONS DES NIVEAUX
		for (int i = 0 ; i < this.lvlButtons.length ; i++) {
			if (this.lvlButtons[i].processLogic() == GuiActionEvent.CLICK_EVENT) {
				switch (this.mode) {
				default :
				case 0 :
					TerraMagnetica.theGame.runFreeLevel(this.lvlButtons[i].getLevelID());
					this.nextPanel(new ScreenGamePlaying());
					break;
				case 1 :
					Level lvl = ExternalFilesManager.getPlayerLevel(this.lvlButtons[i].getLevelID());
					if (lvl != null) {
						try {
							TerraMagnetica.theGame.runLevel(lvl);
							this.nextPanel(new ScreenGamePlaying());
						} catch (GameException e) {
							TerraMagnetica.theGame.displayMessage("Le niveau n'est pas complet !");
						}
					}
					
					break;
				}
			}
		}
		
		//BOUTONS DES NIVEAUX BONUS
		if (this.mode == 2) {
			ArrayList<GuiButtonBonusLevel> bonusButList = this.bonusLvlManager.getAllButtons();
			for (GuiButtonBonusLevel bonusBut : bonusButList) {
				if (bonusBut.processLogic() == GuiActionEvent.CLICK_EVENT) {
					Level bonusLvl = RessourcesManager.getBonusLevel(bonusBut.getFileName());
					
					if (bonusLvl == null) {
						TerraMagnetica.theGame.displayMessage("Le niveau sélectionné n'est pas encore disponible dans cette version du jeu.");
					}
					else {
						try {
							TerraMagnetica.theGame.runLevel(bonusLvl);
							this.nextPanel(new ScreenGamePlaying());
						} catch (GameException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		//BOUTON AJOUTER DES NIVEAUX
		if (this.addButton.processLogic() == GuiActionEvent.CLICK_EVENT) {
			JFileChooser jfc = new JFileChooser();
			jfc.setFileFilter(new ExtensionFileFilter(
					new String[]{"mlv"},
					"Niveaux Terra Magnetica"));
			
			int option = jfc.showDialog(null, "Ajouter");
			
			if (option == JFileChooser.APPROVE_OPTION) {
				File f = jfc.getSelectedFile();
				
				if (f == null) {
					TerraMagnetica.theGame.displayMessage("Aucun niveau selectionné");
				}
				else {
					try {
						ExternalFilesManager.importPlayerLevel(f);
						this.setMode(this.mode);
					} catch (FileFormatException e) {
						TerraMagnetica.theGame.displayMessage("Le fichier selectionné n'est pas un fichier de niveau.");
					} catch (IOException e) {
						e.printStackTrace();
						TerraMagnetica.theGame.displayMessage("Erreur lors de la copie du niveau. Veuillez réessayer.");
					}
				}
			}
		}
		
		return GuiActionEvent.NULL_EVENT;
	}
}
