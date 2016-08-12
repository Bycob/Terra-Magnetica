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

import java.awt.Color;
import java.awt.event.ActionEvent;

import org.terramagnetica.creator.Pinceau;
import org.terramagnetica.creator.ToolButton;

/**
 * Bouton qui, une fois sélectionné, va activer un pinceau spécifique
 * pour peindre une salle de niveau.
 * <p>Cette action consistera à changer le décor ou ajouter des entités
 * dans la salle par le biais de l'interface graphique.
 * @author Louis JEAN
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class PaintButton<T extends Pinceau> extends ToolButton {
	
	public static final String PINCEAU_PROPERTY = "Pinceau";
	
	private T pinceau;
	private boolean focus;
	
	public PaintButton(T pinceau){
		super(pinceau.getIcon());
		this.pinceau = pinceau;
		this.setToolTipText(pinceau.getName());
	}
	
	@Override
	public T getPinceau() {
		return pinceau;
	}

	public void setPinceau(T pinceau) {
		this.pinceau = pinceau;
		updateIcon();
	}
	
	public void updateIcon() {
		setIcon(this.pinceau.getIcon());
	}

	public boolean isFocused() {
		return focus;
	}

	@Override
	public void setFocused(boolean focus) {
		this.focus = focus;
		if (this.focus){
			setBackground(Color.orange);
		}else{
			setBackground(null);
		}
	}

	@SuppressWarnings("unchecked")
	public void changePinceau(Pinceau newPinceau) {
		T oldPinceau = this.pinceau;
		this.setPinceau((T) newPinceau);
		this.setToolTipText(newPinceau.getName());
		
		this.firePropertyChange(PINCEAU_PROPERTY, oldPinceau, this.pinceau);
		this.fireActionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
	}
}
