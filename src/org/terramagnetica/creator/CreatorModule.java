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

package org.terramagnetica.creator;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import org.terramagnetica.game.Level;

public abstract class CreatorModule {
	
	//Barres d'outils
	public abstract JToolBar getPaintToolBar();
	public abstract JToolBar getToolsToolBar();
	
	//Menus
	public abstract JMenuItem[] getToolsMenu();
	public abstract JMenuItem[] getDisplayMenu();
	
	//Panneau principal
	public abstract JComponent getMainPanel();
	
	//Données
	public abstract Level getLevel();
	
	/** Cette méthode est utilisée à chaque fois que le niveau
	 * doit être rafraichi, afin de l'enregistrer par exemple. */
	public void refreshData() {}
}
