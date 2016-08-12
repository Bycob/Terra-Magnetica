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

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.MagneticFieldGenerator;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

@SuppressWarnings("serial")
public class PanelFieldGeneratorProperties extends PanelEntityProperties<MagneticFieldGenerator> {
	
	private JTextField speedTextField = new JFormattedTextField(NumberFormat.getNumberInstance());
	
	public PanelFieldGeneratorProperties() {
		this.speedTextField.setColumns(5);
		
		this.add(new JLabel("Vitesse de rotation (tour/min) : "));
		this.add(this.speedTextField);
	}
	
	@Override
	public void setEntity(Entity e) {
		super.setEntity(e);
		
		this.speedTextField.setText(String.valueOf(this.entity.getRotationSpeed()));
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(DEFAULT_WIDTH, 50);
	}
	
	@Override
	public void onAccept() {
		float speed = this.speedTextField.getText().equals("") ?
					MagneticFieldGenerator.DEFAULT_ROTATE_SPEED : //par défaut
					Float.parseFloat(this.speedTextField.getText()); //sinon
		
		this.entity.setRotationSpeed(speed);
	}
}
