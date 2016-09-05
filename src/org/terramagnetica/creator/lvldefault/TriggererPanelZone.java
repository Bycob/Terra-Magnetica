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

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.terramagnetica.game.lvldefault.GameEvent;
import org.terramagnetica.game.lvldefault.RectangleZone;
import org.terramagnetica.game.lvldefault.ZoneStateTriggerer;

import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.swing.SwingUtil;

@SuppressWarnings("serial")
public abstract class TriggererPanelZone extends TriggererPanel {
	
	protected JPanel zonePanel = new JPanel();
	
	protected JTextField xminTxt = new JFormattedTextField(NumberFormat.getIntegerInstance()),
			yminTxt = new JFormattedTextField(NumberFormat.getIntegerInstance()),
			xmaxTxt = new JFormattedTextField(NumberFormat.getIntegerInstance()),
			ymaxTxt = new JFormattedTextField(NumberFormat.getIntegerInstance());
	
	
	public TriggererPanelZone() {
		
		for (JTextField t : new JTextField[] {this.xminTxt, this.yminTxt, this.xmaxTxt, this.ymaxTxt}) {
			t.setColumns(5);
		}
		
		this.zonePanel.add(new JLabel("La zone rectangulaire s'étend de la case ("));
		this.zonePanel.add(this.xminTxt);
		this.zonePanel.add(new JLabel(";"));
		this.zonePanel.add(this.yminTxt);
		this.zonePanel.add(new JLabel(") à la case ("));
		this.zonePanel.add(this.xmaxTxt);
		this.zonePanel.add(new JLabel(";"));
		this.zonePanel.add(this.ymaxTxt);
		this.zonePanel.add(new JLabel(")"));
	}
	
	public void configTriggerer(ZoneStateTriggerer t) {
		RectangleDouble rz = new RectangleDouble();
		rz.xmin = SwingUtil.parseFormattedInteger(this.xminTxt.getText());
		rz.ymin = SwingUtil.parseFormattedInteger(this.yminTxt.getText());
		rz.xmax = SwingUtil.parseFormattedInteger(this.xmaxTxt.getText());
		rz.ymax = SwingUtil.parseFormattedInteger(this.ymaxTxt.getText());
		
		t.setZone(new RectangleZone(rz));
	}
	
	@Override
	public GameEvent getEvent(int x, int y) {
		return null;
	}
}
