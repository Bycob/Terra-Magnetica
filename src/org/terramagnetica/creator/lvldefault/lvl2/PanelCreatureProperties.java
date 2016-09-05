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

package org.terramagnetica.creator.lvldefault.lvl2;

import java.awt.Dimension;
import java.text.NumberFormat;

import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.terramagnetica.creator.lvldefault.PanelEntityProperties;
import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.lvl2.TheCreature;

import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.RectangleInt;
import net.bynaryscode.util.swing.SwingUtil;

@SuppressWarnings("serial")
public class PanelCreatureProperties extends PanelEntityProperties<TheCreature> {
	
	private JCheckBox hasBoundsBox = new JCheckBox("Limiter les mouvements de l'entité dans la zone allant de");
	private JTextField txtX1 = new JFormattedTextField(NumberFormat.getIntegerInstance()),
			txtY1 = new JFormattedTextField(NumberFormat.getIntegerInstance()),
			txtX2 = new JFormattedTextField(NumberFormat.getIntegerInstance()),
			txtY2 = new JFormattedTextField(NumberFormat.getIntegerInstance());
	
	public PanelCreatureProperties() {
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, 100));
		
		final int columnWidth = 5;
		this.txtX1.setColumns(columnWidth);
		this.txtY1.setColumns(columnWidth);
		this.txtX2.setColumns(columnWidth);
		this.txtY2.setColumns(columnWidth);
		
		this.add(this.hasBoundsBox);
		this.add(this.txtX1);
		this.add(new JLabel(";"));
		this.add(this.txtY1);
		this.add(new JLabel("à"));
		this.add(this.txtX2);
		this.add(new JLabel(";"));
		this.add(this.txtY2);
	}
	
	@Override
	public void setEntity(Entity e) {
		super.setEntity(e);
		
		this.entity = (TheCreature) e;
		RectangleInt zoneBounds = this.entity.getZoneBounds();
		this.hasBoundsBox.setSelected(zoneBounds != null);
		
		this.txtX1.setText(zoneBounds == null ? "" : String.valueOf(zoneBounds.xmin));
		this.txtY1.setText(zoneBounds == null ? "" : String.valueOf(zoneBounds.ymin));
		this.txtX2.setText(zoneBounds == null ? "" : String.valueOf(zoneBounds.xmax));
		this.txtY2.setText(zoneBounds == null ? "" : String.valueOf(zoneBounds.ymax));
	}
	
	@Override
	public void onAccept() {
		this.entity.setZoneBounds(this.hasBoundsBox.isSelected() ? 
				new RectangleInt(SwingUtil.parseFormattedInteger(this.txtX1.getText()),
						SwingUtil.parseFormattedInteger(this.txtY1.getText()),
						SwingUtil.parseFormattedInteger(this.txtX2.getText()),
						SwingUtil.parseFormattedInteger(this.txtY2.getText()))
				: null);
	}

	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(DEFAULT_WIDTH, 100);
	}
}
