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
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.terramagnetica.game.lvldefault.EventScrolling;
import org.terramagnetica.game.lvldefault.EventScrolling.ScrollingType;
import org.terramagnetica.game.lvldefault.GameEvent;

import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.swing.SwingUtil;

@SuppressWarnings("serial")
public class TriggererPanelScrolling extends TriggererPanel {
	
	private JTextField txtCaseX = new JFormattedTextField(NumberFormat.getIntegerInstance()),
			txtCaseY = new JFormattedTextField(NumberFormat.getIntegerInstance()),
			txtDuration = new JFormattedTextField(NumberFormat.getIntegerInstance());
	private JComboBox<String> comboScrollingType = new JComboBox<String>(new String[] {
			"Rectiligne, fluide", "Rectiligne uniforme"
	});
	private JCheckBox checkRepetable = new JCheckBox("Répétable");
	
	public TriggererPanelScrolling() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		this.txtCaseX.setColumns(5);
		this.txtCaseY.setColumns(5);
		this.txtDuration.setColumns(5);
		this.comboScrollingType.setEditable(false);
		
		//Valeurs par défaut
		this.txtDuration.setText("3000");
		this.checkRepetable.setSelected(true);
		
		JPanel panCase = new JPanel();
		panCase.add(new JLabel("Scrolling vers la case de coordonnées "));
		panCase.add(new JLabel("x : "));
		panCase.add(this.txtCaseX);
		panCase.add(new JLabel(" et y : "));
		panCase.add(this.txtCaseY);
		
		JPanel panType = new JPanel();
		panType.add(new JLabel("Type de mouvement : "));
		panType.add(this.comboScrollingType);
		
		JPanel panDuration = new JPanel();
		panDuration.add(new JLabel("Durée du scrolling (en ms) : "));
		panDuration.add(this.txtDuration);
		
		JPanel panRepetable = new JPanel();
		panRepetable.add(this.checkRepetable);
		
		this.add(panCase);
		this.add(panType);
		this.add(panDuration);
		this.add(panRepetable);
	}
	
	@Override
	public GameEvent getEvent(int x, int y) {
		EventScrolling evt = new EventScrolling(new ArrayList<Vec2i>());
		
		Vec2i c = new Vec2i();
		c.x = SwingUtil.parseFormattedInteger(this.txtCaseX.getText());
		c.y = SwingUtil.parseFormattedInteger(this.txtCaseY.getText());
		
		evt.addVisitedCase(c);
		evt.setType(ScrollingType.values()[this.comboScrollingType.getSelectedIndex()]);
		evt.setDuration(SwingUtil.parseFormattedInteger(this.txtDuration.getText()));
		evt.setEventRepetable(this.checkRepetable.isSelected());
		
		return evt;
	}
	
	@Override
	public String getTriggererName() {
		return "Scrolling démonstratif";
	}
}
