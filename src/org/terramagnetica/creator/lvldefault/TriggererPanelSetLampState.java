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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.terramagnetica.game.lvldefault.EventSetLampState;
import org.terramagnetica.game.lvldefault.GameEvent;

import net.bynaryscode.util.swing.SwingUtil;

@SuppressWarnings("serial")
public class TriggererPanelSetLampState extends TriggererPanel {
	
	public static final String ON = "allumé", OFF = "éteint";
	
	private JComboBox<String> options = new JComboBox<String>(new String[] {
			ON, OFF
	});
	
	private JCheckBox checkTime = new JCheckBox("Temps (ms) : ");
	private JTextField txtTime = new JFormattedTextField(NumberFormat.getIntegerInstance());
	
	
	public TriggererPanelSetLampState() {
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		JPanel statePan = new JPanel();
		statePan.add(new JLabel("Etat des lampes : "));
		statePan.add(this.options);
		
		JPanel timePan = new JPanel();
		timePan.add(this.checkTime);
		timePan.add(this.txtTime);
		
		this.checkTime.addItemListener(new CheckTimeListener());
		this.checkTime.setSelected(false);
		
		this.txtTime.setColumns(5);
		this.txtTime.setEnabled(false);
		
		this.add(statePan);
		this.add(timePan);
	}
	
	@Override
	public GameEvent getEvent(int x, int y) {
		
		EventSetLampState evt = new EventSetLampState(ON.equals(this.options.getSelectedItem()));
		if (this.checkTime.isSelected())
				evt.setTimeStaying(SwingUtil.parseFormattedInteger(this.txtTime.getText()));
		
		return evt;
	}
	
	@Override
	public String getTriggererName() {
		return "Etat des lampes - définir";
	}
	
	private class CheckTimeListener implements ItemListener {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			txtTime.setEnabled(checkTime.isSelected());
		}
	}
}
