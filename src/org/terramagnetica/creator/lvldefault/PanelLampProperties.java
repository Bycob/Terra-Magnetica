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

import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;

import org.terramagnetica.game.lvldefault.AbstractLamp;
import org.terramagnetica.game.lvldefault.Entity;

import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.swing.VerticalLayout;

@SuppressWarnings("serial")
public class PanelLampProperties extends PanelEntityProperties<AbstractLamp> {
	
	private JCheckBox invertedCheckBox = new JCheckBox("Inverser l'état de la lampe");
	private JCheckBox lockedCheckBox = new JCheckBox("Verrouiller l'état de la lampe à son état de départ");
	
	public PanelLampProperties() {
		this.setLayout(new VerticalLayout(this, Alignment.LEADING));
		this.add(this.invertedCheckBox);
		this.add(this.lockedCheckBox);
	}
	
	@Override
	public void setEntity(Entity ent) {
		super.setEntity(ent);
		
		this.invertedCheckBox.setSelected(this.entity.isInverted());
		this.lockedCheckBox.setSelected(this.entity.isLocked());
	}
	
	@Override
	public void onAccept() {
		this.entity.setInverted(this.invertedCheckBox.isSelected());
		this.entity.setLocked(this.lockedCheckBox.isSelected());
	}

	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(DEFAULT_WIDTH, 50);
	}

}
