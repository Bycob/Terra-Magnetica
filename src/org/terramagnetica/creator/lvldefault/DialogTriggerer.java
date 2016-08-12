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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.Room;
import org.terramagnetica.game.lvldefault.Triggerer;
import org.terramagnetica.game.lvldefault.ZoneStateTriggerer;

@SuppressWarnings("serial")
public class DialogTriggerer extends JDialog {
	
	private HashMap<String, TriggererPanel> panelMap = new HashMap<String, TriggererPanel>();
	
	private void addPanelMapping(TriggererPanel pan) {
		this.panelMap.put(pan.getTriggererName(), pan);
	}
	
	{
		this.addPanelMapping(new TriggererPanelCheckPoint());
		this.addPanelMapping(new TriggererPanelForceLampState());
		this.addPanelMapping(new TriggererPanelSetLampState());
		this.addPanelMapping(new TriggererPanelScrolling());
	}
	
	private JList<String> triggererList;
	private String lData[];
	private TriggererPanel current = null;
	
	//panneau central
	private CardLayout cardLayout = new CardLayout();
	private JPanel cardParent = new JPanel(this.cardLayout);
	
	private Room theRoom;
	
	public DialogTriggerer(Frame parent, Room r) {
		super(parent, "Ajouter un déclencheur", true);
		
		this.setRoom(r);
		this.setLayout(new BorderLayout());
		
		//EN-TÊTE
		JPanel title = new JPanel();
		title.setBackground(Color.WHITE);
		title.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		JLabel lblTitle = new JLabel("Ajouter un déclencheur");
		title.add(lblTitle);
		
		//PANNEAU RESPECTIFS DES DECLENCHEURS
		for (Map.Entry<String, TriggererPanel> e : this.panelMap.entrySet()) {
			this.cardParent.add(e.getValue(), e.getKey());
		}
		this.add(this.cardParent, BorderLayout.CENTER);
		
		//LISTE DES DECLENCHEUR DISPONIBLES
		this.triggererList = new JList<String>();
		this.triggererList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.lData = this.panelMap.keySet().toArray(new String[0]);
		this.triggererList.setListData(lData);
		this.triggererList.addListSelectionListener(new SwitchPanelListener());
		this.triggererList.setSelectedIndex(0);
		
		//BOUTON "OK"
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				approved = true;
				dispose();
			}
		});
		
		//MISE EN FORME GLOBALE
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.add(title, BorderLayout.NORTH);
		this.add(triggererList, BorderLayout.WEST);
		this.add(ok, BorderLayout.SOUTH);
		
		this.setSize(new Dimension(400, 300));
		this.setLocationRelativeTo(parent);
		this.setResizable(false);
	}
	
	public void setRoom(Room r) {
		if (r == null) throw new NullPointerException();
		
		this.theRoom = r;
		
		for (Entry<String, TriggererPanel> e : this.panelMap.entrySet()) {
			e.getValue().setRoom(r);
		}
	}
	
	private void setTriggerer(String panelName) {
		TriggererPanel t = this.panelMap.get(panelName);
		
		this.cardLayout.show(this.cardParent, panelName);
		
		this.current = t;
		this.repaint();
	}
	
	private boolean approved = false;
	
	/**
	 * Crée un déclencheur avec les options de l'utilisateur.
	 * @param x - La position en x du déclencheur, en cases.
	 * @param y - La position en y du déclencheur, en cases.
	 * @return Un déclencheur ayant pour coordonnées de cases
	 * (x ; y) et possédant les propriétés choisie par l'utilisateur.
	 * <p>Note : le type retourné est {@link Entity} car le
	 * déclencheur peut être soit un {@link Triggerer}, soit
	 * un {@link ZoneStateTriggerer}, or ceux-ci ne sont pas
	 * affiliés.
	 */
	public Entity createTriggererFromUser(int x, int y) {
		this.setVisible(true);
		
		if (this.approved) {
			if (this.current instanceof TriggererPanelZone) {
				ZoneStateTriggerer t = new ZoneStateTriggerer();
				t.setCoordonnéesCase(x, y);
				
				((TriggererPanelZone) this.current).configTriggerer(t);
				
				return t;
			}
			else {
				Triggerer t = new Triggerer();
				t.setCoordonnéesCase(x, y);
				t.setEvent(this.current.getEvent(x, y));
				return t;
			}
		}
		else {
			return null;
		}
	}
	
	class SwitchPanelListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			setTriggerer(lData[triggererList.getLeadSelectionIndex()]);
		}
	}
 }
