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

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.terramagnetica.game.lvldefault.Entity;

/**
 * Un type de pinceau particulier : il ne peint pas la salle
 * (c'est-à-dire qu'il ne dessine pas le terrain ou les entité)
 * mais il effectue une autre action : aucune classe d'entité
 * ou de décor ne lui sera donc attribuée.
 * @author Louis JEAN
 *
 */
public class Tool extends Pinceau {
	
	private ImageIcon icon;
	private PaintingListener action;
	
	public Tool(Image img, String name) {
		super(name);
		if (img != null)
			this.icon = new ImageIcon(img.getScaledInstance(32, 32, Image.SCALE_DEFAULT));
	}
	
	@Override
	public Icon getIcon(){
		return icon;
	}
	
	@Override
	public Class<? extends Entity> getElementPaintedClass(){
		return null;
	}
	
	public void setAction(PaintingListener action) {
		this.action = action;
	}
	
	/**
	 * Donne le listener qui correspond à l'action exercée par le
	 * pinceau-outil.
	 * @return
	 */
	public PaintingListener getAction() {
		return action;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
