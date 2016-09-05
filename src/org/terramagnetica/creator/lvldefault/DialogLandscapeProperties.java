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

package org.terramagnetica.creator.lvldefault;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.terramagnetica.game.lvldefault.LandscapeTile;

@SuppressWarnings("serial")
public class DialogLandscapeProperties extends DialogComponentProperties implements ActionListener {
	
	private LandscapeTile land;
	
	public DialogLandscapeProperties(LandscapeTile l, Frame parent) {
		super(
				new ComponentInfos(l.getCoordonnéesCase()),
				"Propriété du paysage en " + l.getCoordonnéesCase(),
				parent);
		
		this.land = l;
		this.skinField.setText(this.land.getSkin());
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		this.setVisible(true);
		if (this.didCloseByOkButton()) {
			this.land.setSkin(this.skinField.getText());
		}
	}
}
