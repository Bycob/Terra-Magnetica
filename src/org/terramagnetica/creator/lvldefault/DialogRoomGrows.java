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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.ressources.ImagesLoader;

import net.bynaryscode.util.Util;

@SuppressWarnings("serial")
public class DialogRoomGrows extends JDialog {
	
	private JComboBox<Integer> left = new JComboBox<Integer>(),
			right = new JComboBox<Integer>(),
			top = new JComboBox<Integer>(),
			bottom = new JComboBox<Integer>();
	
	private int leftaddin, rightaddin, topaddin, bottomaddin;
	
	private boolean hasProperties = false;
	
	public DialogRoomGrows(Frame parent) {
		super(parent, "Agrandir la salle", true);
		
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		JPanel content = new JPanel(new BorderLayout());
		
		JPanel pan1 = new JPanel();
		pan1.setLayout(new GridLayout(3, 3));
		
		pan1.add(new JPanel());
		pan1.add(this.top);
		pan1.add(new JPanel());
		pan1.add(this.left);
		pan1.add(new JLabel(new ImageIcon(ImagesLoader.get(
				Util.formatDecimal(GameRessources.SPEC_PATH_TERRAIN, 1) + GameRessources.TEX_SOL))));
		pan1.add(this.right);
		pan1.add(new JPanel());
		pan1.add(this.bottom);
		pan1.add(new JPanel());
		
		@SuppressWarnings("unchecked")
		JComboBox<Integer>[] directions = new JComboBox[]{
				this.left,
				this.right,
				this.top,
				this.bottom
		};
		
		for (JComboBox<Integer> combo : directions) {
			for (int i = 0 ; i < 51 ; i++) {
				combo.addItem(i);
			}
			combo.setSelectedItem(0);
		}
		
		JButton bok = new JButton("OK");
		bok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				topaddin = (Integer) top.getSelectedItem();
				bottomaddin = (Integer) bottom.getSelectedItem();
				leftaddin = (Integer) left.getSelectedItem();
				rightaddin = (Integer) right.getSelectedItem();
				hasProperties = true;
				dispose();
			}
		});
		
		content.add(pan1, BorderLayout.CENTER);
		content.add(bok, BorderLayout.SOUTH);
		
		this.setContentPane(content);
		
		this.setSize(new Dimension(300, 320));
		this.setLocationRelativeTo(parent);
	}
	
	public void ask() {
		this.setVisible(true);
	}
	
	public boolean hasProperties() {
		return this.hasProperties;
	}
	
	public static final int TOP = 0, BOTTOM = 1, LEFT = 2, RIGHT = 3;
	
	public int getGrowing(int location) {
		if (!this.hasProperties) {
			throw new IllegalStateException("Pas de propriétés encore.");
		}
		switch (location) {
		case TOP :
			return this.topaddin;
		case BOTTOM :
			return this.bottomaddin;
		case LEFT :
			return this.leftaddin;
		case RIGHT :
			return this.rightaddin;
		default :
			return 0;
		}
	}
}
