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

import java.text.NumberFormat;

import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.Portal;

import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.swing.SwingUtil;
import net.bynaryscode.util.swing.VerticalLayout;

@SuppressWarnings("serial")
public class PanelPortalProperties extends PanelEntityProperties<Portal> {
	
	private JFormattedTextField scaleX = new JFormattedTextField(NumberFormat.getNumberInstance());
	private JFormattedTextField scaleY = new JFormattedTextField(NumberFormat.getNumberInstance());
	
	private JCheckBox sendSpecialCheckBox = new JCheckBox("Envoyer le joueur à la case : ");
	private JFormattedTextField caseX = new JFormattedTextField(NumberFormat.getIntegerInstance());
	private JFormattedTextField caseY = new JFormattedTextField(NumberFormat.getIntegerInstance());
	
	public PanelPortalProperties() {

		this.scaleX.setColumns(5);
		this.scaleY.setColumns(5);
		this.caseX.setColumns(5);
		this.caseY.setColumns(5);
		
		JPanel panScaleX = new JPanel();
		JPanel panScaleY = new JPanel();
		JPanel panSendTo = new JPanel();
		
		panScaleX.add(new JLabel("proportions en abscisse"));
		panScaleX.add(this.scaleX);
		
		panScaleY.add(new JLabel("proportions en ordonnée"));
		panScaleY.add(this.scaleY);
		
		panSendTo.add(this.sendSpecialCheckBox);
		panSendTo.add(this.caseX);
		panSendTo.add(new JLabel(";"));
		panSendTo.add(this.caseY);
		
		this.setLayout(new VerticalLayout(this, Alignment.LEADING));
		this.add(panScaleX);
		this.add(panScaleY);
		this.add(panSendTo);
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(DEFAULT_WIDTH, 150);
	}
	
	@Override
	public void setEntity(Entity e) {
		super.setEntity(e);
		
		this.scaleX.setText(String.valueOf(this.entity.getScaleX()).replace('.', ','));
		this.scaleY.setText(String.valueOf(this.entity.getScaleY()).replace('.', ','));
		
		this.sendSpecialCheckBox.setSelected(this.entity.hasPlayerSpecificLocation());
		Vec2i playerSpecificLocation = this.entity.getPlayerSpecificLocation();
		this.caseX.setText(this.entity.hasPlayerSpecificLocation() ? String.valueOf(playerSpecificLocation.x) : "");
		this.caseY.setText(this.entity.hasPlayerSpecificLocation() ? String.valueOf(playerSpecificLocation.y) : "");
	}
	
	@Override
	public void onAccept() {
		String sXStr = this.scaleX.getText();
		String sYStr = this.scaleY.getText();
		
		if (!"".equals(sXStr) && !"".equals(sYStr)) {
			double sX = Double.parseDouble(sXStr.replace(',', '.'));
			double sY = Double.parseDouble(sYStr.replace(',', '.'));
			
			this.entity.setScale(sX, sY);
		}
		
		if (this.sendSpecialCheckBox.isSelected()) {
			this.entity.setWherePlayerSent(
					Math.max(0, SwingUtil.parseFormattedInteger(this.caseX.getText())),
					Math.max(0, SwingUtil.parseFormattedInteger(this.caseY.getText())));
		}
		else {
			this.entity.setWherePlayerSent(-1, -1);
		}
	}
}
