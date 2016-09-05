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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.terramagnetica.game.lvldefault.DecorType;
import org.terramagnetica.game.lvldefault.Room;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

@SuppressWarnings("serial")
public class DialogCreateRoomDefault extends JDialog {
	
	private boolean hasProperties = false;
	
	private int parWidth, parHeight;
	private DecorType parDecorType;
	
	private JButton buttonOK = new JButton("Valider");
	private JComboBox<Object> boxWidth = new JComboBox<Object>(),
			boxHeight = new JComboBox<Object>(),
			boxDecorType = new JComboBox<Object>();
	
	public DialogCreateRoomDefault(Frame parent) {
		super(parent, "Création d'une salle", true);
		
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		for (int i = 3 ; i < Room.TAILLE_DEFAULT + 51 ; i++) {
			this.boxWidth.addItem(i);
			this.boxHeight.addItem(i);
		}
		
		this.boxWidth.setSelectedItem(Room.TAILLE_DEFAULT);
		this.boxHeight.setSelectedItem(Room.TAILLE_DEFAULT);
		
		for (DecorType type : DecorType.values()) {
			this.boxDecorType.addItem(type.getName());
		}
		
		this.boxDecorType.setSelectedIndex(0);
		
		this.buttonOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				DialogCreateRoomDefault.this.dispose();
				parWidth = (Integer) boxWidth.getSelectedItem();
				parHeight = (Integer) boxHeight.getSelectedItem();
				parDecorType = DecorType.getForName((String) boxDecorType.getSelectedItem());
				hasProperties = true;
			}
		});
		
		this.setLayout(new BorderLayout());
		
		JPanel panGlobal = new JPanel(),
				panTitle = new JPanel(),
				panDimensions = new JPanel(),
				panWidth = new JPanel(),
				panHeight = new JPanel(),
				panDecorType = new JPanel();
		JLabel lblTitle = new JLabel("Création d'une salle"),
				lblWidth = new JLabel("Largeur : "),
				lblHeight = new JLabel("Hauteur : "),
				lblDecorType = new JLabel("Type de décor : ");
		
		panTitle.setBackground(Color.WHITE);
		panTitle.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 10, 0, new Color(240, 240, 240)),
				BorderFactory.createBevelBorder(BevelBorder.RAISED)));
		panTitle.setPreferredSize(new Dimension(400, 40));

		panTitle.add(lblTitle);
		
		
		panDimensions.setLayout(new GridLayout(2, 1, 0, 5));
		panWidth.setLayout(new FlowLayout());
		panHeight.setLayout(new FlowLayout());
		
		panDecorType.setLayout(new FlowLayout());
		panGlobal.setLayout(new GridLayout(2, 1, 0, 5));
		
		
		panWidth.add(lblWidth);
		panWidth.add(this.boxWidth);
		
		panHeight.add(lblHeight);
		panHeight.add(this.boxHeight);
		
		panDimensions.add(panWidth);
		panDimensions.add(panHeight);
		
		panDimensions.setBorder(BorderFactory.createTitledBorder("Dimensions de la salle."));
		
		
		panDecorType.add(lblDecorType);
		panDecorType.add(this.boxDecorType);
		
		panDecorType.setBorder(BorderFactory.createTitledBorder("Type de décor"));
		
		
		panGlobal.add(panDimensions);
		panGlobal.add(panDecorType);
		
		this.add(panTitle, BorderLayout.NORTH);
		this.add(this.buttonOK, BorderLayout.SOUTH);
		this.add(panGlobal, BorderLayout.CENTER);
		
		this.pack();
		this.setLocationRelativeTo(this.getParent());
	}
	
	public void ask() {
		this.setVisible(true);
	}
	
	public boolean hasProperties() {
		return this.hasProperties;
	}
	
	public DimensionsInt getDimensions() {
		checkParameters();
		return new DimensionsInt(this.parWidth, this.parHeight);
	}
	
	public DecorType getDecorType() {
		checkParameters();
		return this.parDecorType;
	}
	
	private void checkParameters() {
		if (!this.hasProperties) {
			throw new NullPointerException("Les propriétés ne sont pas initialisées.");
		}
	}
}
