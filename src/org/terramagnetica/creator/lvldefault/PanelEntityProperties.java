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
	 * Cette m�thode est appel�e � la confirmation de l'utilisateur, pour
	 * modifier l'entit� selectionn�e par le-dit utilisateur.
	 */
	public abstract void onAccept();
	
	/** D�finit si l'extension est utilis�e en mode "cr�ation d'entit�" ou
	 * en mode "propri�t�s de l'entit�". En fonction de cela, des param�tres
	 * peuvent �tre  */
	public void setCreate(boolean create) {
		this.create = create;
	}
	
	public boolean isCreate() {
		return this.create;
	}
	
	/**
	 * D�finit l'entit� modifi�e par le panneau. Ainsi, le panneau
	 * peut connaitre les param�tres sp�cifiques pr�c�demment
	 * appliqu�s sur l'entit� � modifier.
	 * @param e - L'entit� dont on modifie les propri�t�s � travers
	 * ce panneau.
	 */
	@SuppressWarnings("unchecked")
	public void setEntity(Entity ent) {
		String exeptStr = "Le param�tre \"ent\" est nul ou n'est pas du bon type !";
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
	 * Pr�pare le panneau � accueillir de nouvelles donn�es. Cette
	 * m�thode est appell�e � chaque fois que la fen�tre se ferme,
	 * afin d'effacer les donn�es de l'entit� qui vient d'�tre modifi�e.
	 */
	public void reset() {}
}
