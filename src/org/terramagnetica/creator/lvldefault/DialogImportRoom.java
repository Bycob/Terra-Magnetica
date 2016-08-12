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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.terramagnetica.creator.Creator;
import org.terramagnetica.creator.NiveauSaver;
import org.terramagnetica.game.Level;
import org.terramagnetica.game.lvldefault.LevelDefault;
import org.terramagnetica.game.lvldefault.Room;

import net.bynaryscode.util.swing.SwingUtil;
import net.bynaryscode.util.swing.VerticalLayout;

@SuppressWarnings("serial")
public class DialogImportRoom extends JDialog {
	
	private boolean hasProperties = false;
	
	private Creator parent = null;
	private NiveauSaver saver = null;
	
	private LevelDefault toImport = null;
	/** Contient toutes les salles du niveau chargé, dans l'ordre. */
	private ArrayList<Room> allRooms = new ArrayList<Room>();
	
	//INTERFACE GRAPHIQUE
	private JPanel panRoomsSelect = new JPanel(),
			panFilePath = new JPanel();
	
	private JLabel lblTitle = new JLabel("Importer des salles"),
			lblFilePath = new JLabel("Chemin du niveau : "),
			lblSelectRoom = new JLabel("Selectionnez les salles à importer : ");
	
	private JTextField txtFieldFilePath = new JTextField(50);
	
	private JList<String> listRoomsSelected = new JList<String>();
	
	private JButton buttonBrowse = new JButton("Parcourir"),
			buttonOk = new JButton("OK");
	
	
	
	public DialogImportRoom(Creator parent) {
		super(parent, "Importer des salles", true);
		
		this.parent = parent;
		if (this.parent != null) {
			this.saver = new NiveauSaver(this.parent);
		}
		else throw new IllegalArgumentException("DialogImportRoom doit avoir une frame parente.");
		
		this.setResizable(false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.setLayout(new BorderLayout());
		
		//Panneau titre
		JPanel panTitle = new JPanel(),
				panContent = new JPanel();
		
		
		panTitle.setBackground(Color.WHITE);
		panTitle.setBorder(BorderFactory.createRaisedBevelBorder());
		
		panTitle.add(this.lblTitle);
		
		
		panContent.setLayout(new VerticalLayout(panContent, 3));
		panContent.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		
		//Panneau du chemin du niveau.
		this.buttonBrowse.addActionListener(new BrowseListener());
		this.txtFieldFilePath.getDocument().addDocumentListener(new FilePathListener());
		
		this.panFilePath.add(this.lblFilePath);
		this.panFilePath.add(this.txtFieldFilePath);
		this.panFilePath.add(this.buttonBrowse);
		
		//Panneau de selection des salles.
		this.listRoomsSelected.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		this.listRoomsSelected.setPreferredSize(new Dimension(300, 250));
		
		this.panRoomsSelect.setLayout(new VerticalLayout(this.panRoomsSelect, 3));
		
		this.panRoomsSelect.add(this.lblSelectRoom);
		this.panRoomsSelect.add(this.listRoomsSelected);
		
		//Bouton OK
		this.buttonOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				DialogImportRoom.this.dispose();
				hasProperties = true;
			}
		});
		
		//Articulation globale
		panContent.add(this.panFilePath);
		panContent.add(SwingUtil.getSeparator());
		panContent.add(this.panRoomsSelect);
		
		this.add(panTitle, BorderLayout.NORTH);
		this.add(panContent, BorderLayout.CENTER);
		this.add(buttonOk, BorderLayout.SOUTH);
		
		this.setSize(new Dimension(650, 400));
		this.setLocationRelativeTo(parent);
	}
	
	public void ask() {
		this.setVisible(true);
	}
	
	public ArrayList<Room> getImportedRoomList() {
		ArrayList<Room> result = new ArrayList<Room>();
		//Si l'utilisateur a fermé la fenêtre, aucune salle n'a été importée.
		if (! this.hasProperties) return result;
		
		int[] indexSelected = this.listRoomsSelected.getSelectedIndices();
		for (int i = 0 ; i < indexSelected.length ; i++) {
			result.add(this.allRooms.get(indexSelected[i]));
		}
		return result;
	}
	
	private void enableSelectRoomPanel(boolean enable) {
		if (enable) {
			this.panFilePath.setEnabled(true);
		}
		else {
			this.allRooms.clear();
			this.listRoomsSelected.setListData(new String[0]);
			this.panFilePath.setEnabled(false);
		}
	}
	
	/**
	 * Cette classe écoute le champ de texte du fichier pour activer
	 * ou désactiver le panneau "Choisir une salle"
	 */
	private class FilePathListener implements DocumentListener {
		
		@Override
		public void insertUpdate(DocumentEvent e) {
			checkPath();
		}
		
		@Override
		public void removeUpdate(DocumentEvent e) {
			checkPath();
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			checkPath();
		}
		
		private void checkPath() {
			String path = txtFieldFilePath.getText();
			File file = new File(path);
			
			if (file.exists()) {
				Level level = saver.open(file);
				
				if (level instanceof LevelDefault) {
					enableSelectRoomPanel(true);
					
					toImport = (LevelDefault) level;
					
					allRooms = toImport.getRoomList();
					String[] nameArray = new String[allRooms.size()];
					
					for (int i = 0 ; i < allRooms.size() ; i++) {
						nameArray[i] = allRooms.get(i).getInGameName();
					}
					listRoomsSelected.setListData(nameArray);
				}
				else {
					txtFieldFilePath.setText("");
					enableSelectRoomPanel(false);
				}
			}
			else {
				enableSelectRoomPanel(false);
			}
		}
	}
	
	private class BrowseListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent evt) {
			int option = saver.showOpenDialog(parent);
			
			if (option == JFileChooser.APPROVE_OPTION) {
				File selected = saver.getSelectedFile();
				txtFieldFilePath.setText(selected == null ? "" : selected.getPath());
			}
		}
	}
}
