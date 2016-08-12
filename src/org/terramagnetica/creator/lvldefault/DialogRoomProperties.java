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
import java.awt.Frame;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.terramagnetica.creator.DialogCreator;
import org.terramagnetica.game.lvldefault.Room;
import org.terramagnetica.game.lvldefault.Room.RoomTag;

import net.bynaryscode.util.swing.VerticalLayout;

@SuppressWarnings("serial")
public class DialogRoomProperties extends DialogCreator {
	
	private JCheckBox limVisionCheck = new JCheckBox("Salle en vision limitée");
	//Les chaines de caractères utilisées dans la combo sont aussi utilisée dans la méthode onOKButton();
	private JComboBox<String> lampModeComboBox = new JComboBox<String>(new String[] {"mode normal", "mode enfers"});
	
	private Room myRoom;
	
	public DialogRoomProperties(Room room, Frame parent) {
		super(parent, "Propriétés de la salle");
		
		this.myRoom = room;
		if (this.myRoom == null) 
			throw new NullPointerException("DialogRoomProperties : aucune salle pour appliquer les paramètres.");
		
		JPanel panContent = new JPanel();
		
		this.limVisionCheck.setSelected(this.myRoom.hasTag(RoomTag.LIMITED_VISION));
		
		//mode des lampes
		JPanel lampModePan = new JPanel();
		
		if (this.myRoom.hasTag(RoomTag.LAMP_STATE_MODE_3)) this.lampModeComboBox.setSelectedIndex(1);
		
		lampModePan.add(new JLabel("Comportement des lampes : "));
		lampModePan.add(this.lampModeComboBox);
		
		//construction du panneau final et ajout à la boite de dialogue
		panContent.setLayout(new VerticalLayout(panContent, 10));
		
		panContent.add(this.limVisionCheck);
		panContent.add(lampModePan);
		this.add(panContent, BorderLayout.CENTER);
		
		this.setSize(400, 200);
		this.setLocationRelativeTo(parent);
	}
	
	/** Affiche la boite de dialogue. Celle-ci est modale, elle bloque
	 * le programme lorsqu'elle est affichée. A la fin, si le bouton OK
	 * est selectionné par l'utilisateur, les paramètres définis par lui
	 * sont automatiquement appliqués à la salle. */
	@Override
	public void ask() {
		super.ask();
	}
	
	@Override
	public void onOKButton() {
		//Vision limitée
		if (this.limVisionCheck.isSelected()) {
			this.myRoom.addTag(RoomTag.LIMITED_VISION);
		}
		else {
			this.myRoom.removeTag(RoomTag.LIMITED_VISION);
		}
		
		//Mode des lampes
		String lampModeUserInput = this.lampModeComboBox.getSelectedItem().toString();
		if ("mode normal".equals(lampModeUserInput)) {
			this.myRoom.removeTag(RoomTag.LAMP_STATE_MODE_3);
		}
		else if ("mode enfers".equals(lampModeUserInput)) {
			this.myRoom.addTag(RoomTag.LAMP_STATE_MODE_3);
		}
	}
}
