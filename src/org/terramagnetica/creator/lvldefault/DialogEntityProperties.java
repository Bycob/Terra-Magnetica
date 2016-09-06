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

import org.terramagnetica.game.lvldefault.Entity;

@SuppressWarnings("serial")
public class DialogEntityProperties extends DialogComponentProperties implements ActionListener {
	
	private Entity entity;
	private PanelEntityProperties propPan;
	private boolean creating = false;
	
	public DialogEntityProperties(Entity e, Frame parent, PaintingPropertiesMap propertyMap, boolean isCreatingEntity) {
		super(
				new ComponentInfos(e.getPositionf(), propertyMap.getPropertyPanel(e.getClass())),
				"Propriété de l'entité " + propertyMap.getName(e.getClass()) + " en " + e.getPositionf(),
				parent);
		
		this.creating = isCreatingEntity;
		this.entity = e;
		this.skinField.setText(e.getSkin());
		
		PanelEntityProperties p = propertyMap.getPropertyPanel(e.getClass());
		if (p != null) {
			p.setEntity(e);
			p.setCreate(this.creating);
			this.propPan = p;
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		this.setVisible(true);
		if (this.didCloseByOkButton()) {
			this.entity.setSkin(this.skinField.getText());
			if (this.propPan != null) this.propPan.onAccept();
		}
	}
}
