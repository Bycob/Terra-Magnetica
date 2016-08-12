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

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.terramagnetica.game.lvldefault.EventForceLampState;
import org.terramagnetica.game.lvldefault.ZoneStateTriggerer;

@SuppressWarnings("serial")
public class TriggererPanelForceLampState extends TriggererPanelZone {
	
	public static final String ON = "allumé", OFF = "éteint";
	
	private JComboBox<String> options = new JComboBox<String>(new String[] {
			ON, OFF
	});
	
	public TriggererPanelForceLampState() {
		super();
		
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		JPanel statePan = new JPanel();
		
		statePan.add(new JLabel("Etat des lampes : "));
		statePan.add(this.options);
		
		this.add(statePan);
		this.add(this.zonePanel);
	}
	
	@Override
	public String getTriggererName() {
		return "Etat des lampes - zone";
	}
	
	@Override
	public void configTriggerer(ZoneStateTriggerer t) {
		super.configTriggerer(t);
		
		boolean on = ON.equals(this.options.getSelectedItem());
		
		t.setEnterEvent(new EventForceLampState(on ? EventForceLampState.FORCE_ON : EventForceLampState.FORCE_OFF));
		t.setExitEvent(new EventForceLampState(EventForceLampState.FORCE_NOTHING));
	}
}
