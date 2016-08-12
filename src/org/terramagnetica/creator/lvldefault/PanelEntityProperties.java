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

package org.terramagnetica.creator.lvldefault;

import javax.swing.JPanel;

import org.terramagnetica.game.lvldefault.Entity;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

@SuppressWarnings("serial")
public abstract class PanelEntityProperties<T extends Entity> extends JPanel {
	
	public static final int DEFAULT_WIDTH = DialogComponentProperties.DEFAULT_WIDTH;
	
	protected T entity;
	private boolean create;
	
	public PanelEntityProperties() {
		
	}
	
	/**
	 * Cette méthode est appelée à la confirmation de l'utilisateur, pour
	 * modifier l'entité selectionnée par le-dit utilisateur.
	 */
	public abstract void onAccept();
	
	/** Définit si l'extension est utilisée en mode "création d'entité" ou
	 * en mode "propriétés de l'entité". En fonction de cela, des paramètres
	 * peuvent être  */
	public void setCreate(boolean create) {
		this.create = create;
	}
	
	public boolean isCreate() {
		return this.create;
	}
	
	/**
	 * Définit l'entité modifiée par le panneau. Ainsi, le panneau
	 * peut connaitre les paramètres spécifiques précédemment
	 * appliqués sur l'entité à modifier.
	 * @param e - L'entité dont on modifie les propriétés à travers
	 * ce panneau.
	 */
	@SuppressWarnings("unchecked")
	public void setEntity(Entity ent) {
		String exeptStr = "Le paramètre \"ent\" est nul ou n'est pas du bon type !";
		if (ent == null) throw new NullPointerException(exeptStr);
		
		try {
			this.entity = (T) ent;
		} catch (ClassCastException e) {
			throw new NullPointerException(exeptStr);
		}
	}
	/**
	 * Donne les dimensions du panneau.
	 * @return Un objet {@link DimensionsInt} contenant les dimensions du 
	 * panneau.
	 */
	public abstract DimensionsInt getDimensions();
	/**
	 * Prépare le panneau à accueillir de nouvelles données. Cette
	 * méthode est appellée à chaque fois que la fenêtre se ferme,
	 * afin d'effacer les données de l'entité qui vient d'être modifiée.
	 */
	public void reset() {}
}
