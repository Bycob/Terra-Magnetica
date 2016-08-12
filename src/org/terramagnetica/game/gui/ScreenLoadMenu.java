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

package org.terramagnetica.game.gui;

import java.util.ArrayList;

import org.terramagnetica.game.TerraMagnetica;
import org.terramagnetica.opengl.gui.GuiActionEvent;
import org.terramagnetica.opengl.gui.GuiBorderLayout;
import org.terramagnetica.opengl.gui.GuiButtonText1;
import org.terramagnetica.opengl.gui.GuiContainer;
import org.terramagnetica.opengl.gui.GuiFrameContainer;
import org.terramagnetica.opengl.gui.GuiLabel;
import org.terramagnetica.opengl.gui.GuiLayout002;
import org.terramagnetica.opengl.gui.GuiScrollPanel;
import org.terramagnetica.opengl.gui.GuiScrollPanel.ScrollBar;
import org.terramagnetica.opengl.gui.GuiVerticalLayout;
import org.terramagnetica.ressources.ExternalFilesManager;
import org.terramagnetica.ressources.SaveData;
import org.terramagnetica.ressources.SavesInfo;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Color4f;

public class ScreenLoadMenu extends GameScreen {

	private GuiScrollPanel scroll;
	private GuiContainer content;
	private GuiButtonLoadGame[] savesButtons;
	private int selectedButton = -1;
	
	private GuiButtonText1 returnButton;
	private GuiButtonText1 removeButton;
	private GuiButtonText1 selectButton;
	private GuiButtonText1 renameButton;
	
	public ScreenLoadMenu() {
		
		this.initContent();
		this.scroll = new GuiScrollPanel(this.content, -1, 0.5, 1, 0.5);
		this.scroll.setScrollBarsUsed(ScrollBar.VERTICAL);
		this.scroll.setColor(new Color4f(209, 182, 0));
		this.scroll.fillBackground(false);

		this.selectButton = new GuiButtonText1(-1.45, 0.2, -0.55, 0.05, "jouer");
		this.renameButton = new GuiButtonText1(-0.45, 0.2, 0.45, 0.05, "renommer");
		this.removeButton = new GuiButtonText1(0.55, 0.2, 1.45, 0.05, "supprimer");
		this.returnButton = new GuiButtonText1(-0.45, -0.05, 0.45, -0.2, "retour");
		
		GuiContainer panGlobal = new GuiFrameContainer();
		GuiContainer panScroll = new GuiContainer();
		GuiContainer panButton = new GuiContainer();
		GuiContainer panTitle = new GuiContainer();
		GuiLabel title = new GuiLabel("Choisissez votre partie", 20, 0, 0);
		
		Color4f background = new Color4f(0, 0, 0, 25);
		
		GuiBorderLayout mainLayout = new GuiBorderLayout();
		panGlobal.setLayout(mainLayout);
		panScroll.setLayout(new GuiLayout002());
		panButton.setBackground(background);
		panTitle.setBackground(background);
		title.setColor(this.returnButton.getTextColor());
		
		panGlobal.add(panTitle, GuiBorderLayout.TOP);
		panGlobal.add(panScroll, GuiBorderLayout.CENTER);
		panGlobal.add(panButton, GuiBorderLayout.BOTTOM);
		
		panScroll.add(this.scroll);
		panButton.add(this.returnButton);
		panButton.add(this.removeButton);
		panButton.add(this.selectButton);
		panButton.add(this.renameButton);
		panTitle.add(title);
		
		
		GuiDefaultTransition tt = new GuiDefaultTransition(this);
		tt.setSpeed(SPEED_DEFAULT);
		tt.add(this.selectButton, GuiDefaultTransition.GAUCHE);
		tt.add(this.renameButton, GuiDefaultTransition.BAS);
		tt.add(this.removeButton, GuiDefaultTransition.DROITE);
		tt.add(this.returnButton, GuiDefaultTransition.BAS);
		
		this.setTransition(tt);
		
		this.add(panGlobal);
	}
	
	private void initContent() {
		//Recherche et tri des sauvegarde disponibles, en fonction de la date.
		SavesInfo infos = ExternalFilesManager.infos;
		SaveData[] datas = infos.getSavesData();
		ArrayList<SaveData> dataList = new ArrayList<SaveData>();
		
		for (SaveData data : datas) {
			boolean added = false;
			
			for (int i = 0 ; i < dataList.size() ; i ++) {
				if (dataList.get(i).getLastModified() < data.getLastModified()) {
					dataList.add(i, data);
					added = true;
					break;
				}
			}
			
			if (!added) dataList.add(data);
		}
		
		//Ajout des boutons
		double buttonHeight = 0.4;
		
		this.content = new GuiContainer(0, 0, 1, (buttonHeight + 0.03) * infos.getNbSaves() + 0.03);
		this.content.setLayout(new GuiVerticalLayout(buttonHeight, 0.03, 0.03));
		this.savesButtons = new GuiButtonLoadGame[infos.getNbSaves()];
		
		int i = 0;
		for (SaveData data : dataList) {
			this.savesButtons[i] = new GuiButtonLoadGame(data);
			this.content.add(this.savesButtons[i]);
			i++;
		}
	}
	
	@Override
	public int timeToAppear() {
		return MILLIS_APPEAR_DEFAULT;
	}
	
	@Override
	public int timeToDestroy() {
		return MILLIS_DESTROY_DEFAULT;
	}
	
	@Override
	protected void drawComponent() {
		drawDefaultBackground();
	}
	
	@Override
	public GuiActionEvent processLogic() {
		if (getState() != VISIBLE) {
			return super.processLogic();
		}
		
		if (returnButton.processLogic() == GuiActionEvent.CLICK_EVENT) {
			this.nextPanel(new ScreenMainMenu());
		}
		
		int i = 0;
		for (GuiButtonLoadGame b : this.savesButtons) {
			if (b.processLogic() == GuiActionEvent.CLICK_EVENT) {
				if (b.selected) {
					this.loadGame(b);
				}
				else {
					for (GuiButtonLoadGame button : this.savesButtons) {
						button.selected = false;
					}
					b.selected = true;
					this.selectedButton = i;
				}
				return GuiActionEvent.NULL_EVENT;
			}
			i++;
		}
		
		if (this.selectButton.processLogic() == GuiActionEvent.CLICK_EVENT && this.selectedButton != -1) {
			this.loadGame(this.savesButtons[this.selectedButton]);
		}
		
		if (this.removeButton.processLogic() == GuiActionEvent.CLICK_EVENT && this.selectedButton != -1) {
			ExternalFilesManager.removeSauvegarde(this.savesButtons[this.selectedButton].getData());
			this.initContent();
			this.scroll.setContent(this.content);
			this.selectedButton = -1;
		}
		
		if (this.renameButton.processLogic() == GuiActionEvent.CLICK_EVENT && this.selectedButton != -1) {
			
		}
		
		return GuiActionEvent.NULL_EVENT;
	}
	
	private void loadGame(GuiButtonLoadGame b) {
		try {
			TerraMagnetica.theGame.save = ExternalFilesManager.loadSauvegarde(b.getData());
		} catch (GameIOException e) {
			e.printStackTrace();
			this.initContent();
			this.scroll.setContent(this.content);
			this.selectedButton = -1;
			
			TerraMagnetica.theGame.displayMessage("La sauvegarde " + b.getData().getName() + " n'a pas pu être chargée.");
			
			return;
		}
		this.nextPanel(new ScreenInGameMenu());
	}
}
