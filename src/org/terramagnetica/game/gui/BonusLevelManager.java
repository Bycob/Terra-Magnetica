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

import static org.terramagnetica.game.GameRessources.*;

import java.util.ArrayList;

import org.terramagnetica.opengl.gui.GuiComponent;
import org.terramagnetica.opengl.gui.GuiMovingPanel;

/**
 * Cette classe gère les niveaux bonus et leur affichage dans
 * l'interface graphique.
 * @author Louis
 */
public class BonusLevelManager {
	
	public BonusLevelManager() {
		
	}
	
	private GuiMovingPanel panel;
	
	public GuiMovingPanel getPanel() {
		if (this.panel == null) this.buildPanel();
		return this.panel;
	}
	
	private void buildPanel() {
		this.panel = new GuiMovingPanel(8, 5);
		
		this.panel.addElement(new GuiButtonBonusLevel(PATH_BONUS_LEVEL_BUTTONS + TEX_BONUS_BUTTON_0_ENFERS, "0_gorge_des_enfers.mlv", "Gorge des enfers"), 2, 3.5);
	}
	
	public ArrayList<GuiButtonBonusLevel> getAllButtons() {
		ArrayList<GuiButtonBonusLevel> butList = new ArrayList<GuiButtonBonusLevel>();
		
		GuiComponent[] butArray = getPanel().getAllElement();
		for (GuiComponent c : butArray) {
			if (c instanceof GuiButtonBonusLevel) {
				butList.add((GuiButtonBonusLevel) c);
			}
		}
		return butList;
	}
}
