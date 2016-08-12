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

import javax.swing.GroupLayout.Alignment;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.MagneticWavesGenerator;

import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.swing.VerticalLayout;

@SuppressWarnings("serial")
public class PanelWaveGeneratorProperties extends PanelEntityProperties<MagneticWavesGenerator> {
	
	private JTextField freqTxt = new JFormattedTextField(NumberFormat.getNumberInstance());
	private JTextField speedTxt = new JFormattedTextField(NumberFormat.getNumberInstance());
	private JTextField distTxt = new JFormattedTextField(NumberFormat.getNumberInstance());
	
	public PanelWaveGeneratorProperties() {
		this.setLayout(new VerticalLayout(this, 3, Alignment.LEADING));
		
		JPanel pan1 = new JPanel(), pan2 = new JPanel(), pan3 = new JPanel();
		
		this.freqTxt.setColumns(5);
		pan1.add(new JLabel("Fréquence des vagues : "));
		pan1.add(this.freqTxt);
		
		this.speedTxt.setColumns(5);
		pan2.add(new JLabel("Vitesse des vagues : "));
		pan2.add(this.speedTxt);
		
		this.distTxt.setColumns(5);
		pan3.add(new JLabel("Distance parcourue par les vagues : "));
		pan3.add(this.distTxt);
		
		this.add(pan1);
		this.add(pan2);
		this.add(pan3);
	}
	
	@Override
	public void setEntity(Entity e) {
		super.setEntity(e);
		
		this.freqTxt.setText(String.valueOf(this.entity.getFreq()));
		this.speedTxt.setText(String.valueOf(this.entity.getSpeed()));
		this.distTxt.setText(String.valueOf(this.entity.getDistance()));
	}

	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(DEFAULT_WIDTH, 100);
	}
	
	@Override
	public void onAccept() {
		this.entity.setFreq(this.freqTxt.getText().equals("") ? 
				MagneticWavesGenerator.DEFAULT_FREQUENCY : 
				Float.valueOf(this.freqTxt.getText()));
		this.entity.setSpeed(this.speedTxt.getText().equals("") ? 
				MagneticWavesGenerator.DEFAULT_SPEED : 
				Float.valueOf(this.speedTxt.getText()));
		this.entity.setDistance(this.distTxt.getText().equals("") ? 
				MagneticWavesGenerator.DEFAULT_DISTANCE : 
				Float.valueOf(this.distTxt.getText()));
	}
}
