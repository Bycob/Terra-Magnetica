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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.terramagnetica.creator.lvldefault.PaintButton;
import org.terramagnetica.ressources.ImagesLoader;

/**
 * Un menu contextuel qui permet de sélectionner un pinceau parmi
 * une liste proposée. Se présente sous la forme d'un bouton qui,
 * au clic, déroule le menu contextuel.
 * @author Louis JEAN
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class PaintMenu<T extends Pinceau> extends ToolButton {
	
	private ArrayList<T> pinceaux;
	private T pinceauCourant;
	
	private PaintButton<T> listener;
	
	private PinceauFilter filter = null;
	
	public PaintMenu(T... pinceaux) {
		super(new ImageIcon(ImagesLoader.get(ImagesLoader.popupToolbar)));
		this.pinceaux = new ArrayList<T>();
		
		for (T pinceau : pinceaux){
			this.addPinceau(pinceau);
		}
	}
	
	public void addPinceau(T painter) {
		this.pinceaux.add(painter);
		
		if (this.pinceauCourant == null) {
			this.pinceauCourant = pinceaux.get(0);
		}
	}
	
	public void removePinceaux() {
		this.pinceaux = new ArrayList<T>();
		this.pinceauCourant = null;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<T> getPinceauxList() {
		return (ArrayList<T>) this.pinceaux.clone();
	}
	
	@Override
	public T getPinceau() {
		return pinceauCourant;
	}
	
	public T getPinceauSelected() {
		return getPinceau();
	}

	public void setPinceauSelected(T selected) {
		this.pinceauCourant = selected;
		this.listener.changePinceau(this.pinceauCourant);
	}
	
	public PinceauFilter getFilter() {
		return filter;
	}
	
	public void setFilter(PinceauFilter filter) {
		this.filter = filter;
	}
	
	public JPopupMenu getMenu(){
		JPopupMenu popup = new JPopupMenu();
		
		for(Pinceau pinceau : pinceaux){
			if (this.filter != null && !pinceau.getFilter().matches(this.filter)) {
				continue;
			}
			JMenuItem item = new JMenuItem(pinceau.getName());
			item.setIcon(pinceau.getIcon());
			item.addActionListener(new ChangeOptionListener());
			popup.add(item);
		}
		
		return popup;
	}
	
	@Override
	public void setFocused(boolean focused) {
		this.listener.setFocused(focused);
	}
	
	public void setPaintButton(PaintButton<T> listener) {
		this.listener = listener;
	}
	
	class ChangeOptionListener implements ActionListener {
		
		@SuppressWarnings("unchecked")
		@Override
		public void actionPerformed(ActionEvent event) {
			
			for (Pinceau pinceau : pinceaux) {
				
				if (((JMenuItem)event.getSource()).getText().equals(pinceau.getName())) {
					
					setPinceauSelected((T) pinceau);
					return;
				}
			}
		}
	}
}
