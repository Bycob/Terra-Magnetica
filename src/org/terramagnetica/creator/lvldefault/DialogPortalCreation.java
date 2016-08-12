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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.terramagnetica.creator.DialogCreator;
import org.terramagnetica.game.lvldefault.LevelDefault;
import org.terramagnetica.game.lvldefault.Room;

@SuppressWarnings("serial")
public class DialogPortalCreation extends DialogCreator {
	
	private boolean lvlEnd = true;
	private int nextRoomID;
	
	private JRadioButton radioEnd = new JRadioButton("Fin du niveau"),
			radioChangeRoom = new JRadioButton("Changement de salle");
	private JComboBox<String> comboOtherRoom = new JComboBox<String>();
	
	public DialogPortalCreation(Frame parent, LevelDefault level, int portalRoomID) {
		super(parent, "Création d'un portail");
		
		this.comboOtherRoom.setPreferredSize(new Dimension(75, 25));
		
		ButtonGroup group = new ButtonGroup();
		group.add(this.radioEnd);
		group.add(this.radioChangeRoom);
		
		this.radioEnd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				comboOtherRoom.setEnabled(false);
			}
		});
		
		this.radioChangeRoom.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				comboOtherRoom.setEnabled(true);
			}
		});
		
		this.radioEnd.setSelected(true);
		this.comboOtherRoom.setEnabled(false);
		
		for (Room r : level.getRoomList()) {
			if (r.getID() != portalRoomID) {
				this.comboOtherRoom.addItem("Salle " + (r.getID()+ 1));
			}
		}
		
		if (this.comboOtherRoom.getItemCount() == 0) {
			this.radioChangeRoom.setEnabled(false);
		}
		
		JPanel centerPan = new JPanel();
		centerPan.setLayout(new GridLayout(2, 1));
		
		JPanel endPan = new JPanel();
		endPan.add(this.radioEnd);
		
		JPanel changePan = new JPanel();
		changePan.add(this.radioChangeRoom);
		changePan.add(this.comboOtherRoom);
		
		centerPan.add(endPan);
		centerPan.add(changePan);
		
		this.add(centerPan, BorderLayout.CENTER);
		
		this.setSize(300, 200);
		this.setLocationRelativeTo(parent);
	}
	
	public boolean hasProperties() {
		return this.didCloseByOkButton();
	}
	
	@Override
	public void onOKButton() {
		lvlEnd = radioEnd.isSelected();
		if (!lvlEnd) {
			nextRoomID = ModuleLevelDefault.parseIndexRoom(comboOtherRoom.getSelectedItem().toString());
		}
	}
	
	public boolean lvlEnd() {
		if (!this.hasProperties()) throw new IllegalArgumentException("Les propriétés n'ont pas été initialisées.");
		return this.lvlEnd;
	}
	
	public int nextRoomID() {
		if (this.lvlEnd()) throw new IllegalStateException("Le portail correspond à la fin du niveau.");
		return this.nextRoomID;
	}
}
