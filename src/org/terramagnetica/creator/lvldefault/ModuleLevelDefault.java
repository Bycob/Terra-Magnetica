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

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.terramagnetica.creator.Creator;
import org.terramagnetica.creator.CreatorModule;
import org.terramagnetica.creator.PaintMenu;
import org.terramagnetica.creator.PaintingListener;
import org.terramagnetica.creator.Pinceau;
import org.terramagnetica.creator.Tool;
import org.terramagnetica.creator.ToolButton;
import org.terramagnetica.creator.lvldefault.PaintingPropertiesMap.Property;
import org.terramagnetica.creator.lvldefault.lvl2.PanelCreatureProperties;
import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.Aimant;
import org.terramagnetica.game.lvldefault.DecorType;
import org.terramagnetica.game.lvldefault.DecorationEntity;
import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.GroundTile;
import org.terramagnetica.game.lvldefault.IDirectionnalEntity;
import org.terramagnetica.game.lvldefault.Lampe;
import org.terramagnetica.game.lvldefault.LampePerturbatrice;
import org.terramagnetica.game.lvldefault.LandscapeTile;
import org.terramagnetica.game.lvldefault.LevelDefault;
import org.terramagnetica.game.lvldefault.MagneticFieldGenerator;
import org.terramagnetica.game.lvldefault.MagneticFieldPerturbateur;
import org.terramagnetica.game.lvldefault.MagneticWavesGenerator;
import org.terramagnetica.game.lvldefault.Mark;
import org.terramagnetica.game.lvldefault.Portal;
import org.terramagnetica.game.lvldefault.PropertyDirectionnalEntity;
import org.terramagnetica.game.lvldefault.Room;
import org.terramagnetica.game.lvldefault.Triggerer;
import org.terramagnetica.game.lvldefault.UnusedRoomException;
import org.terramagnetica.game.lvldefault.VirtualWall;
import org.terramagnetica.game.lvldefault.WallTile;
import org.terramagnetica.game.lvldefault.lvl2.Rock;
import org.terramagnetica.game.lvldefault.lvl2.TheCreature;
import org.terramagnetica.game.lvldefault.lvl2.Trap;
import org.terramagnetica.ressources.ImagesLoader;

import net.bynaryscode.util.Boussole;
import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec2f;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.swing.DrawUnit;
import net.bynaryscode.util.swing.SwingInputData;
import net.bynaryscode.util.swing.SwingUtil;

/**
 * Module permettant de modifier des niveaux de type {@link LevelDefault}.
 * @author Louis JEAN
 *
 */
public class ModuleLevelDefault extends CreatorModule {
	
	private Creator parent;
	
	public final PaintingPropertiesMap properties = new PaintingPropertiesMap();
	
	{
		//Initialisation des propriétés : nom, filtre, listener, panneau de propriétés
		this.properties.getProperty(Aimant.class).setName("Cristal magnétique").setPaintingListener(new PaintEntityListener());
		this.properties.getProperty(Lampe.class).setName("Lampe magnétique").setPaintingListener(new PaintEntityListener()).setPropertyPanel(new PanelLampProperties());
		this.properties.getProperty(LampePerturbatrice.class).setName("Lampe magnétique à champ variable").setPaintingListener(new PaintEntityListener()).setPropertyPanel(new PanelLampProperties());
		this.properties.getProperty(MagneticFieldGenerator.class).setName("Générateur de champ magnétique").setPaintingListener(new PaintEntityListener()).setPropertyPanel(new PanelFieldGeneratorProperties());
		this.properties.getProperty(MagneticWavesGenerator.class).setName("Générateur d'ondes magnétique").setPaintingListener(new PaintEntityListener().withModule(new ModuleDirectionnalEntity())).setPropertyPanel(new PanelWaveGeneratorProperties());
		this.properties.getProperty(VirtualWall.class).setName("Mur virtuel").setPaintingListener(new PaintEntityListener());
		this.properties.getProperty(Portal.class).setName("Portail").setPaintingListener(new PaintPortalEntityListener()).setPropertyPanel(new PanelPortalProperties());
		this.properties.getProperty(Trap.class).setName("Piège").setFilter(new PinceauFilterLevelDefault(null, 2)).setPaintingListener(new PaintEntityListener());
		this.properties.getProperty(Rock.class).setName("Rocher").setFilter(new PinceauFilterLevelDefault(null, 2)).setPaintingListener(new PaintEntityListener());
		this.properties.getProperty(TheCreature.class).setName("Créature").setFilter(new PinceauFilterLevelDefault(null, 2)).setPaintingListener(new PaintEntityListener()).setPropertyPanel(new PanelCreatureProperties());
		this.properties.getProperty(Triggerer.class).setName("Déclencheur").setPaintingListener(new PaintTriggererListener());
		this.properties.getProperty(Mark.class).setName("Marqueur").setPaintingListener(new PaintEntityListener());
		this.properties.getProperty(MagneticFieldPerturbateur.class).setName("Perturbateur de champ").setPaintingListener(new PaintEntityListener());

		this.properties.getProperty(GroundTile.class).setName("Sol").setPaintingListener(new PaintLandscapeListener());
		this.properties.getProperty(WallTile.class).setName("Murs").setPaintingListener(new PaintLandscapeListener());
	}
	
	private LevelDefault theLevel;
	private Room actualRoom;

	private LevelDefaultView view;
	private JScrollPane scroll;
	private LevelDefaultController paint;
	
	
	private PaintMenu<PinceauLandscape> landscapeMenu = new PaintMenu<PinceauLandscape>();
	private PaintMenu<PinceauEntity> entityMenu = new PaintMenu<PinceauEntity>();
	private PaintButton<PinceauLandscape> landscapePaintButton;
	private PaintButton<PinceauEntity> entityPaintButton;
	
	private PaintButton<Tool> removePaintButton = new PaintButton<Tool>(
					new Tool(ImagesLoader.get(ImagesLoader.effacer), "Supprimer"));
	private PaintButton<Tool> movePaintButton = new PaintButton<Tool>(
					new Tool(ImagesLoader.get(ImagesLoader.deplacer), "Déplacer"));
	private PaintButton<Tool> persoPaintButton = new PaintButton<Tool>(
					new Tool(ImagesLoader.get(GameRessources.PATH_PLAYER), "Placer le personnage"));
	
	private ToolButton[] onDrawToolbar;
	
	private JToolBar paintBar = new JToolBar("Dessiner");
	private JToolBar toolsBar = new JToolBar("Outils");
	
	private JComboBox<String> zoomComboBox = new JComboBox<String>(),
			roomComboBox = new JComboBox<String>();
	
	/** Items du menu "Outils" */
	private JMenuItem addItem = new JMenuItem("Ajouter une salle..."),
			remRoomItem = new JMenuItem("Supprimer la salle"),
			importRoomItem = new JMenuItem("Importer des salles..."),
			optimSizeItem = new JMenuItem("Optimiser la place"),
			growItem = new JMenuItem("Agrandir la salle..."),
			renameItem = new JMenuItem("Renommer la salle..."),
			setMainItem = new JMenuItem("Définir comme salle principale"),
			changeDecorTypeItem = new JMenuItem("Changer le décor..."), 
			roomPropertiesItem = new JMenuItem("Propriétés de la salle...");
	
	private JCheckBoxMenuItem showCases = new JCheckBoxMenuItem("Afficher les cases");
	
	public ModuleLevelDefault(Creator parent, LevelDefault level) {
		this.init(level, parent);
	}
	
	private void init(LevelDefault level, Creator parent) {
		this.parent = parent;
		this.theLevel = level;
		
		if (this.theLevel != null) {
			
			this.view = new LevelDefaultView(this.actualRoom);
			this.scroll = new JScrollPane(this.view);
			this.paint = new LevelDefaultController(this.actualRoom);
			
			
			this.scroll.getHorizontalScrollBar().addAdjustmentListener(new ClipListener());
			this.scroll.getVerticalScrollBar().addAdjustmentListener(new ClipListener());
			this.scroll.addMouseWheelListener(new MoletteListener());
			
			SwingInputData.setAnchor(this.view);
			this.view.addKeyListener(new EchapListener());
			this.view.addMouseListener(new RightClickListener());
			
			this.view.addMouseListener(new DecorationListener());
		}

		initPaintToolBar();
		initToolsToolBar();
		initToolsMenu();
		
		//initialisation de la configuration (outils sélectionnés)
		if (this.theLevel != null) {
			setRoom(this.theLevel.getMainRoom());
			updateRoomBox();
			
			this.setOptionSelected(this.landscapePaintButton);
			this.landscapePaintButton.updateIcon();
		}
	}
	
	public static LevelDefault createLevel(Creator dialogParent) {
		DialogCreateRoomDefault create = new DialogCreateRoomDefault(dialogParent);
		create.ask();
		
		LevelDefault result = null;
		
		if (create.hasProperties()) {
			DimensionsInt dims = create.getDimensions();
			Room mainRoom = new Room(create.getDecorType(), dims.getWidth(), dims.getHeight());
			result = new LevelDefault(mainRoom);
		}
		
		return result;
	}
	
	class ClipListener implements AdjustmentListener {
		@Override
		public void adjustmentValueChanged(AdjustmentEvent arg0) {
			view.setClip(scroll);
			view.repaint();
		}
	}
	
	class MoletteListener implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent arg0) {
			scroll.getVerticalScrollBar().setValue(
					scroll.getVerticalScrollBar().getValue() + arg0.getWheelRotation() * 50);
		}
	}
	
	
	private void initPaintToolBar() {
		
		//Traitement spécifique
		this.persoPaintButton.getPinceau().setAction(new MovePlayerListener());
		this.movePaintButton.getPinceau().setAction(new MoveEntityListener());
		this.removePaintButton.getPinceau().setAction(new RemoveListener());
		
		for (Property prop : this.properties.getAllProperties()) {
			if (Entity.class.isAssignableFrom(prop.getKeyClass())) {
				@SuppressWarnings("unchecked")
				PinceauEntity pinceau = new PinceauEntity((Class<? extends Entity>) prop.getKeyClass(), prop.getName());
				if (prop.getFilter() != null) pinceau.setFilter(prop.getFilter());
				this.entityMenu.addPinceau(pinceau);
			}
			else if (LandscapeTile.class.isAssignableFrom(prop.getKeyClass())) {
				@SuppressWarnings("unchecked")
				PinceauLandscape pinceau = new PinceauLandscape((Class<? extends LandscapeTile>) prop.getKeyClass(), prop.getName());
				if (prop.getFilter() != null) pinceau.setFilter(prop.getFilter());
				this.landscapeMenu.addPinceau(pinceau);
			}
		}
		
		this.landscapePaintButton = new PaintButton<PinceauLandscape>(landscapeMenu.getPinceau());
		this.entityPaintButton = new PaintButton<PinceauEntity>(entityMenu.getPinceau());
		
		this.landscapeMenu.setPaintButton(this.landscapePaintButton);
		this.entityMenu.setPaintButton(this.entityPaintButton);
		
		//Traitement groupé
		this.onDrawToolbar = new ToolButton[] {
			this.landscapePaintButton,
			this.landscapeMenu,
			this.entityPaintButton,
			this.entityMenu,
			this.persoPaintButton,
			this.removePaintButton,
			this.movePaintButton
		};
		
		PinceauListener listen = new PinceauListener();
		MenuPinceauListener listen2 = new MenuPinceauListener();
		
		for (int i = 0 ; i < this.onDrawToolbar.length ; i++) {
			ToolButton at = this.onDrawToolbar[i];
			
			if (at instanceof PaintButton) {
				at.addActionListener(listen);
				at.addPropertyChangeListener(listen);
			}
			else if (at instanceof PaintMenu) {
				at.addActionListener(listen2);
			}
			
			this.paintBar.add(at);
		}
		
		if (this.theLevel == null) {
			this.enableOption(false);
		}
	}
	
	private void initToolsToolBar() {
		//Zoom
		for (int i = 3 ; i > -5 ; i--) {
			double lvlZoom = 100 * Math.pow(2, i);
			this.zoomComboBox.addItem(new String(lvlZoom + " %"));
		}
		
		this.zoomComboBox.setSelectedItem("12.5 %");
		this.zoomComboBox.setPreferredSize(this.zoomComboBox.getSize());
		this.zoomComboBox.addItemListener(new ZoomListener());
		
		this.toolsBar.add(this.zoomComboBox);
		
		//Salle selectionnée
		this.toolsBar.add(this.roomComboBox);
	}
	
	private void initToolsMenu() {
		this.addItem.addActionListener(new AddListener());						this.addItem.setToolTipText("Ajoute une salle au niveau actuel");
		this.remRoomItem.addActionListener(new DelListener());					this.remRoomItem.setToolTipText("Supprime la salle courante");
		this.importRoomItem.addActionListener(new ImportRoomListener());		this.importRoomItem.setToolTipText("Importer des salles depuis d'autres fichiers niveaux");
		
		this.optimSizeItem.addActionListener(new OptimizeListener());			this.optimSizeItem.setToolTipText("Optimise la salle courante, en supprimant les zones innaccessible en bordure.");
		this.growItem.addActionListener(new GrowListener());					this.growItem.setToolTipText("Ajoute du paysage sur les bords de la salle courante, pour l'agrandir.");
		
		this.renameItem.addActionListener(new RenameListener());				this.renameItem.setToolTipText("Renomme la salle.");
		this.setMainItem.addActionListener(new SetMainListener());				this.setMainItem.setToolTipText("Définit la salle courante comme première salle du niveau.");
		this.changeDecorTypeItem.addActionListener(new ChangeDecorTypeListener()); this.changeDecorTypeItem.setToolTipText("Change le type de décor de la salle.");
		this.roomPropertiesItem.addActionListener(new RoomPropertiesListener()); this.roomPropertiesItem.setToolTipText("Modifie certaines propriétés de la salle");
		
		this.showCases.addItemListener(new ShowCasesListener());				this.showCases.setToolTipText("Affiche les cases pour plus de lisibilité");
	}
	
	/** Met à jour les items et {@link ItemListener} de la liste
	 * de sélection des salles.
	 * <p>Cette méthode doit être appelée <i>après</i> un changement de
	 * salle géré de façon interne ou un changement de nom de salle. */
	private void updateRoomBox() {
		//Suppression des listeners et des items
		ItemListener[] listeners = this.roomComboBox.getItemListeners();
		for (ItemListener l : listeners) {
			this.roomComboBox.removeItemListener(l);
		}
		
		this.roomComboBox.removeAllItems();
		//---
		
		if (this.theLevel != null) {
			String actualRoomItemTxt = "";
			
			List<Room> roomList = this.theLevel.getRoomList();
			for (Room r : roomList) {
				String rName = "".equals(r.getUserName()) ? "" : " \"" + r.getUserName() + "\" ";
				String itemTxt = "Salle " + (r.getID() + 1) + rName + getRoomSuffix(r);
				this.roomComboBox.addItem(itemTxt);
				
				if (actualRoom != null && r.getID() == actualRoom.getID()) {
					actualRoomItemTxt = itemTxt;
				}
			}
			
			//rétablissement du listener
			this.roomComboBox.setSelectedItem(actualRoomItemTxt);
			this.roomComboBox.addItemListener(new ChangeRoomListener());
		}
	}
	
	private String getRoomSuffix(Room r) {
		return r.getID() == theLevel.getMainRoom().getID() ? " (salle principale)" : "";
	}
	
	/** Cette méthode remplace la salle en cours d'édition par
	 * la salle passée en paramètre, et effectue toutes les mises
	 * à jour d'affichage et de variable nécessaires.
	 *<p>Important : la liste de sélection des salles n'est pas mise
	 *à jour. */
	protected void setRoom(Room room) {
		this.actualRoom = room;
		this.paint.setRoom(this.actualRoom);
		this.view.setRoom(this.actualRoom);

		//Mise à jour des filtres
		if (this.theLevel != null) {
			this.entityMenu.setFilter(new PinceauFilterLevelDefault(this.actualRoom.getDecorType(), this.theLevel.levelID));
		}
		else {
			this.entityMenu.setFilter(null);
		}
		
		//Mise à jour du type de décor des pinceaux
		for (PinceauLandscape pinceau : this.landscapeMenu.getPinceauxList()) {
			pinceau.setDecorType(this.actualRoom.getDecorType());
		}
		this.landscapePaintButton.updateIcon();
		
		this.view.setClip(this.scroll);
		this.view.repaint();
		this.scroll.revalidate();
	}
	
	@Override
	public JToolBar getPaintToolBar() {
		return paintBar;
	}
	
	@Override
	public JToolBar getToolsToolBar() {
		return this.toolsBar;
	}
	
	@Override
	public JMenuItem[] getToolsMenu() {
		return new JMenuItem[] {
			this.addItem,
			this.remRoomItem,
			this.importRoomItem,
			null,
			this.growItem,
			this.optimSizeItem,
			null,
			this.renameItem,
			this.setMainItem,
			this.changeDecorTypeItem,
			this.roomPropertiesItem
		};
	}
	
	@Override
	public JMenuItem[] getDisplayMenu() {
		return new JMenuItem[] {
			this.showCases
		};
	}
	
	@Override
	public JComponent getMainPanel() {
		return scroll;
	}
	
	@Override
	public LevelDefault getLevel() {
		return this.theLevel;
	}
	
	@Override
	public void refreshData() {
		if (this.theLevel != null) {
			this.theLevel.putRoom(this.actualRoom.getID(), this.actualRoom);
			this.paintBar.revalidate();
			this.toolsBar.revalidate();
			this.scroll.revalidate();
		}
	}
	
	public void setOptionSelected(ToolButton what){
		for (ToolButton button : this.onDrawToolbar) {
			button.setFocused(false);
		}
		
		Pinceau option = what.getPinceau();
		
		if (option instanceof Tool) {
			this.view.setPaintingListener(((Tool) option).getAction());
		}
		else {
			this.view.setPaintingListener(this.properties.getPaintingListener(what.getPinceau().getElementPaintedClass()));
		}
		what.setFocused(true);
	}
	
	protected void enableOption(boolean enable) {
		for (ToolButton button : this.onDrawToolbar) {
			button.setEnabled(enable);
		}
	}
	
	/*
	 * ********* ECOUTEURS DES COMPOSANTS DE LA BARRE "OUTILS" ***********
	 */
	
	class ZoomListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				double newZoom = view.getZoom();
				String strZoom = zoomComboBox.getSelectedItem().toString();
				strZoom = strZoom.substring(0, strZoom.length() - 2);
				
				try {
					newZoom = Double.parseDouble(strZoom);
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				}
				
				view.setZoom(newZoom);
				view.setClip(scroll);
				view.repaint();
				scroll.revalidate();
			}
		}
	}
	
	class ChangeRoomListener implements ItemListener {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				refreshData();
				Room newRoom = theLevel.getRoom(parseIndexRoom(roomComboBox.getSelectedItem().toString()));
				if (newRoom == null) {
					return;
				}
				setRoom(newRoom);
			}
		}
	}
	
	/**
	 * Analyse la chaine contenue dans le menu déroulant
	 * {@link ModuleLevelDefault#roomComboBox} et retourne l'ID de la salle
	 * correspondante.
	 */
	public static int parseIndexRoom(String inComboBox) {
		return Util.extractIntegers(inComboBox)[0] - 1;
	}
	
	/*
	 ************ ECOUTEURS DES ITEMS DU MENU "OUTILS" *******************
	 */
	
	class AddListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			DialogCreateRoomDefault create = new DialogCreateRoomDefault(ModuleLevelDefault.this.parent);
			create.ask();
			
			if (create.hasProperties()) {
				DimensionsInt dims = create.getDimensions();
				Room room = new Room(create.getDecorType(), dims.getWidth(), dims.getHeight());
				
				int id = ModuleLevelDefault.this.theLevel.addRoom(room);
				ModuleLevelDefault.this.setRoom(theLevel.getRoom(id));
				updateRoomBox();
			}
		}
	}
	
	class DelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int option = JOptionPane.showConfirmDialog(ModuleLevelDefault.this.parent,
					"Voulez-vous vraiment suprrimer la salle ?",
					"Supprimer la salle", JOptionPane.YES_NO_OPTION);
			if (option != JOptionPane.YES_OPTION) return;
			
			try {
				theLevel.delRoom(actualRoom.getID());
			} catch (IllegalArgumentException e1) {
				Toolkit.getDefaultToolkit().beep();
				JOptionPane.showMessageDialog(ModuleLevelDefault.this.parent,
						"Impossible de supprimer la salle principale !",
						"Supprimer la salle", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			setRoom(theLevel.getMainRoom());
			updateRoomBox();
		}
	}
	
	class OptimizeListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				actualRoom = Room.scaledRoom(actualRoom);
			} catch (UnusedRoomException e1) {
				Toolkit.getDefaultToolkit().beep();
				return;
			}
			theLevel.putRoom(actualRoom.getID(), actualRoom);
			setRoom(actualRoom);//mise à jour
		}
	}
	
	/**
	 * Le listener du bouton "Agrandir la salle".
	 * @author Louis JEAN
	 */
	class GrowListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			DialogRoomGrows growDialog = new DialogRoomGrows(ModuleLevelDefault.this.parent);
			growDialog.ask();
			if (growDialog.hasProperties()) {
				actualRoom = Room.grownRoom(actualRoom,
						growDialog.getGrowing(DialogRoomGrows.TOP),
						growDialog.getGrowing(DialogRoomGrows.BOTTOM),
						growDialog.getGrowing(DialogRoomGrows.LEFT),
						growDialog.getGrowing(DialogRoomGrows.RIGHT));
				theLevel.putRoom(actualRoom.getID(), actualRoom);
				setRoom(actualRoom);//mise à jour
			}
		}
	}
	
	/** Renomme la salle. */
	class RenameListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String newName = JOptionPane.showInputDialog(parent,
					"Entrez un nouveau nom pour la salle",
					actualRoom.getUserName());
			
			if (newName != null) {
				actualRoom.setName(newName);
			}
			
			updateRoomBox();
		}
	}
	
	/** Définit la salle comme salle principale  */
	class SetMainListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			theLevel.setMainRoom(actualRoom.getID());
			updateRoomBox();
		}
	}
	
	class ChangeDecorTypeListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String[] choices = new String[DecorType.values().length];
			for (int i = 0 ; i < choices.length ; i++) {
				choices[i] = DecorType.values()[i].getName();
			}
			
			String selected = (String) JOptionPane.showInputDialog(ModuleLevelDefault.this.parent,
					"Selectionnez le nouveau décor", "Changement de décor",
					JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
			
			if (selected != null) {
				actualRoom.setDecorType(DecorType.getForName(selected));
				
				setRoom(actualRoom);
				view.repaint();
			}
		}
	}
	
	class RoomPropertiesListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent evt) {
			DialogRoomProperties dialog = new DialogRoomProperties(actualRoom, ModuleLevelDefault.this.parent);
			dialog.ask();
		}
	}
	
	
	class ImportRoomListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent evt) {
			
			DialogImportRoom dialog = new DialogImportRoom(ModuleLevelDefault.this.parent);
			dialog.ask();
			
			ArrayList<Room> importedRooms = dialog.getImportedRoomList();
			
			boolean somethingHappened = false;
			for (Room r : importedRooms) {
				ArrayList<Entity> entities = r.getEntities();
				
				//Suppression de tous les portails qui ne finissent pas le niveau
				for (Entity e : entities) {
					
					if (e instanceof Portal) {
						Portal p = (Portal) e;
						
						if (!p.isLevelEnd()) {
							r.removeEntity(p);
							somethingHappened = true;
						}
					}
				}
				
				theLevel.addRoom(r);
			}
			
			if (somethingHappened) {
				JOptionPane.showMessageDialog(parent,
						"Attention, certaines salles ont été modifiées pour pouvoir être intégrées dans le niveau.",
						"Importation des salles", JOptionPane.WARNING_MESSAGE);
			}
			
			if (importedRooms.size() != 0) {
				updateRoomBox();
			}
		}
	}

	/*
	 ************ ECOUTEURS DES ITEMS DU MENU "AFFICHAGE" *******************
	 */
	
	class ShowCasesListener implements ItemListener {
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			view.drawCases(showCases.isSelected());
			view.repaint();
		}
	}
	
	
	/* 
	 ******************* ECOUTEURS DES BOUTONS ***************************
	 */
	
	class PinceauListener implements ActionListener, PropertyChangeListener {
		@Override
		public void actionPerformed(ActionEvent event){
			Object listened = event.getSource();
			if (listened instanceof ToolButton) {
				setOptionSelected(((ToolButton) listened));
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(PaintButton.PINCEAU_PROPERTY)) {
				this.actionPerformed(new ActionEvent(evt.getSource(), ActionEvent.ACTION_PERFORMED, null));
			}
		}
	}
	
	class MenuPinceauListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event){
			PaintMenu<?> menu = (PaintMenu<?>)event.getSource();
			menu.getMenu().show(paintBar,
					menu.getBounds().x,
					menu.getBounds().y + menu.getBounds().height);
		}
	}
	
	/*
	 ************************ PINCEAUX ***********************************
	 */
	
	class DecorationListener implements MouseListener {

		@Override public void mouseClicked(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON2) return;
			Vec2i c = view.pointInRoom(e.getX(), e.getY());
			String tex = JOptionPane.showInputDialog("Nom de texture : ");
			if ("".equals(tex)) return;
			actualRoom.addEntity(new DecorationEntity(c.x, c.y, tex));
			view.repaint();
		}
		
		@Override public void mousePressed(MouseEvent e) {} 
		@Override public void mouseReleased(MouseEvent e) {}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
	}
	
	class RightClickListener implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON3) return;
			
			JPopupMenu menu = getMenuOn(e.getX(), e.getY());
			if (menu != null) menu.show(view, e.getX(), e.getY());
		}
		
		@Override public void mousePressed(MouseEvent e) {}
		@Override public void mouseReleased(MouseEvent e) {}
		@Override public void mouseEntered(MouseEvent e) {}
		@Override public void mouseExited(MouseEvent e) {}
	}
	
	public JPopupMenu getMenuOn(int locX, int locY) {
		JPopupMenu result = new JPopupMenu();

		Vec2i c1 = view.pointInRoom(locX, locY);
		Vec2i c2 = view.caseInRoom(locX, locY);
		DimensionsInt dim = actualRoom.getDimensions();
		
		if (c2.x >= dim.getWidth() || c2.y >= dim.getHeight()) {
			return null;
		}
		
		LandscapeTile l = this.actualRoom.getLandscapeAt(c2.x, c2.y);
		Entity e = this.actualRoom.getEntityAt(c1);
		
		JMenuItem prop = new JMenuItem("Propriétés du décor...");
		prop.addActionListener(new DialogLandscapeProperties(l, this.parent));
		result.add(prop);
		
		if (e != null) {
			JMenuItem entProp = new JMenuItem("Propriétés de l'entité...");
			entProp.addActionListener(new DialogEntityProperties(e, this.parent, this.properties, false));
			result.add(entProp);
		}
		
		return result;
	}
	
	class EchapListener extends PaintingListener implements KeyListener {
		
		private PaintingListener stopedListener;
		private boolean moving = false;
		
		@Override
		public void mouseReleased(MouseEvent arg0) {
			view.setPaintingListener(this.stopedListener);
			this.stopedListener = null;
			this.moving = false;
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			if (!moving && arg0.getKeyCode() == KeyEvent.VK_ESCAPE && SwingInputData.isMousePressed()){
				this.stopedListener = view.getPaintingListener();
				view.setPaintingListener(this);
				view.stopSelecting();
				view.stopDrag();
				view.repaint();
				this.moving = true;
			}
		}
		
		@Override
		public void keyReleased(KeyEvent arg0) {}
		@Override
		public void keyTyped(KeyEvent arg0) {}
	}
	
	
	abstract class AbstractSelectionListener extends PaintingListener {
		
		protected boolean onlyLeftClick = true;
		protected Vec2i début;
		
		@Override
		public void mousePressed(MouseEvent arg0) {
			if (this.onlyLeftClick && arg0.getButton() != MouseEvent.BUTTON1) return;
			
			début = new Vec2i(arg0.getX(), arg0.getY());
			view.setSelection(début.x, début.y, début.x, début.y);
			view.repaint();
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			view.extendsSelection(e.getX(), e.getY());
			view.repaint();
		}
		
		@Override
		public void mouseReleased(MouseEvent arg0) {
			if (this.onlyLeftClick && arg0.getButton() != MouseEvent.BUTTON1) return;
			
			view.stopSelecting();
			view.repaint();
		}
	}
	
	
	class PaintLandscapeListener extends AbstractSelectionListener implements Runnable {
		
		private Vec2i fin = new Vec2i();
		
		@Override
		public void mouseReleased(MouseEvent arg0) {
			super.mouseReleased(arg0);
			if (arg0.getButton() != MouseEvent.BUTTON1)
				return;
			fin = new Vec2i(arg0.getX(), arg0.getY());
			new Thread(this).start();
		}
		
		@Override
		public void run(){
			if (début != null && fin != null){
				final double sf = view.getScaleFactor();
				paint.paintDecor(
						(int) (début.x / sf), (int) (début.y  / sf),
						(int) (fin.x / sf), (int) (fin.y / sf), landscapePaintButton.getPinceau());
			}
			début = null;
			fin = null;
			view.repaint();
		}
	}
	
	
	class PaintEntityListener extends PaintingListener {
		
		private PinceauEntity pinceau;
		private boolean painting;
		private Entity dragged = null;
		
		@Override
		public void mousePressed(MouseEvent arg0) {
			
			if (arg0.getButton() != MouseEvent.BUTTON1)
				return;
			
			if (this.pinceau == null) {
				this.pinceau = entityPaintButton.getPinceau();
			}
			
			//Si une entité est présente à l'endroit du clic, on la déplace
			Entity here = actualRoom.getEntityAt(view.pointInRoom(arg0.getX(), arg0.getY()));
			
			if (here != null && this.pinceau.getElementPaintedClass().equals(here.getClass())){
				actualRoom.removeEntity(here);
				this.dragged = here;
			}
			
			if (SwingInputData.isKeyPressed(KeyEvent.VK_CONTROL)) {
				arg0 = toCase(arg0);
			}
			
			//mise à jour de la vue
			final double sf = view.getScaleFactor();
			view.setDragged(this.pinceau.createInstance((int) (arg0.getX() / sf), (int) (arg0.getY() / sf), false));
			this.painting = true;
			view.repaint();
		}
		
		@Override
		public void mouseReleased(MouseEvent arg0) {
			view.stopDrag();
			
			if (arg0.getButton() != MouseEvent.BUTTON1)
				return;
			
			//Placement de l'entité dans la salle
			final double sf = view.getScaleFactor();
			boolean painted = false;
			if (this.dragged != null) {
				painted = paint.paintEntity((int)(arg0.getX() / sf), (int)(arg0.getY() / sf), this.dragged);
			}
			else {
				painted = paint.paintEntity((int)(arg0.getX() / sf), (int)(arg0.getY() / sf), this.pinceau);
			}
			
			//Préparation pour le prochain appel / l'execution du module
			this.lastEntityCreated = paint.getLastEntityPainted();
			
			this.painting = false;
			this.pinceau = null;
			
			view.repaint();

			if (painted && SwingInputData.isKeyPressed(KeyEvent.VK_CONTROL)) {
				Vec2i entCase = this.lastEntityCreated.getCasePosition();
				this.lastEntityCreated.setCasePosition(entCase.x, entCase.y);
			}
			
			//Activation du module facultatif
			if (this.next != null && painted) {
				this.next.setCaller(this);
				this.next.setActivated();
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent arg0) {
			if (SwingInputData.isKeyPressed(KeyEvent.VK_CONTROL)) {
				arg0 = toCase(arg0);
			}
			
			final double sf = view.getScaleFactor();
			view.moveDrag((int) (arg0.getX() / sf), (int) (arg0.getY() / sf));
			view.repaint();
		}
		
		public void setPinceau(PinceauEntity pinceau) {
			if (!this.painting) {
				this.pinceau = pinceau;
			}
		}
		
		/** Ce champ contient un Pinceau de dessin qui s'applique juste
		 * après la création de la nouvelle entité, pour régler un
		 * paramètre de façon graphique. */
		private ModuleDessinEntite next;
		
		public PaintEntityListener withModule(ModuleDessinEntite next) {
			this.next = next;
			return this;
		}
		
		/** La dernière entité créée par cet objet. */
		private Entity lastEntityCreated = null;
		
		public Entity getLastEntityCreated() {
			return this.lastEntityCreated;
		}
		
		protected MouseEvent toCase(MouseEvent event) {
			final int CASE_SIZE = view.getTailleCase();
			
			Vec2i caseLocation = view.caseInRoom(event.getX(), event.getY());
			caseLocation = new Vec2i(
					(int) ((caseLocation.x + Entity.DEMI_CASE_F) * CASE_SIZE),
					(int) ((caseLocation.y + Entity.DEMI_CASE_F) * CASE_SIZE));
			
			return SwingUtil.createMouseEvent(event, caseLocation.x, caseLocation.y);
		}
	}
	
	
	
	
	class PaintPortalEntityListener extends PaintEntityListener {
		
		private Portal moved = null; 
		
		@Override
		public void mousePressed(MouseEvent e) {
			
			if (e.getButton() != MouseEvent.BUTTON1) return;
			
			Entity here = actualRoom.getEntityAt(view.pointInRoom(e.getX(), e.getY()));
			if (here instanceof Portal) {
				this.moved = (Portal) here;
			}
			super.mousePressed(e);
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			
			if (e.getButton() != MouseEvent.BUTTON1) return;
			
			MouseEvent e1 = toCase(e);
			Vec2i location = view.pointInRoom(e1.getX(), e1.getY());
			Vec2i case0 = view.caseInRoom(e1.getX(), e1.getY());
			Portal newPortal = null;
			
			LandscapeTile l = actualRoom.getLandscapeAt(case0.x, case0.y);
			if (l != null && (l.isEnabled() || l instanceof WallTile)) {//Vérifie si on peut placer le portail.
				
				boolean shouldPlace = true;
				
				if (this.moved != null) {
					newPortal = this.moved;
				}
				else {
					newPortal = new Portal();
					
					DialogPortalCreation asking = new DialogPortalCreation(parent, theLevel, actualRoom.getID());
					asking.ask();
					
					if (asking.hasProperties()) {
						if (asking.lvlEnd()) {
							newPortal.goToEnd();
						}
						else {
							newPortal.goToRoom(theLevel.getRoom(asking.nextRoomID()));
						}
					}
					else {
						shouldPlace = false;
					}
				}
				
				if (shouldPlace) {
					if (l instanceof WallTile && ((WallTile) l).getOrientation() != WallTile.PLANE) {
						newPortal.setOnWall(((WallTile) l).getOrientation());
						paint.paintEntity(location.x, location.y, newPortal, true);
					}
					else {
						newPortal.setOnGround();
						paint.paintEntity(location.x, location.y, newPortal);
					}
				}
			}
			
			ModuleLevelDefault.this.view.stopDrag();
			ModuleLevelDefault.this.view.repaint();
			
			this.moved = null;
		}
	}
	
	
	
	
	class PaintTriggererListener extends PaintEntityListener {
		
		private Triggerer moved;
		
		@Override
		public void mousePressed(MouseEvent e) {
			
			if (e.getButton() != MouseEvent.BUTTON1) return;
			
			Entity here = actualRoom.getEntityAt(view.pointInRoom(e.getX(), e.getY()));
			if (here instanceof Triggerer) {
				this.moved = (Triggerer) here;
			}
			super.mousePressed(e);
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {

			if (e.getButton() != MouseEvent.BUTTON1) return;
			
			MouseEvent e1 = toCase(e);
			
			Vec2i caseLoc = view.caseInRoom(e1.getX(), e1.getY());
			boolean shouldPlace = actualRoom.getLandscapeAt(caseLoc.x, caseLoc.y).isEnabled();
			
			if (this.moved != null) {
				if (!shouldPlace) {
					actualRoom.removeEntity(this.moved);
				}
				else {
					this.moved.setCasePosition(caseLoc.x, caseLoc.y);
				}
			}
			else if (shouldPlace) {
				DialogTriggerer dialog = new DialogTriggerer(parent, actualRoom);
				Entity t = dialog.createTriggererFromUser(caseLoc.x, caseLoc.y);
				if (t != null) actualRoom.addEntity(t);
			}
			
			this.moved = null;
			
			ModuleLevelDefault.this.view.stopDrag();
			ModuleLevelDefault.this.view.repaint();
		}
	}
	
	
	
	class MovePlayerListener extends PaintingListener {
		
		private int mouseButton = 0;
		
		public void placePlayer(MouseEvent event) {
			if (this.mouseButton != MouseEvent.BUTTON1) return;
			actualRoom.setPlayerLocation(
					event.getX() / view.getTailleCase(),
					event.getY() / view.getTailleCase());
			view.repaint();
		}
		
		@Override
		public void mousePressed(MouseEvent event) {
			this.mouseButton = event.getButton();
			placePlayer(event);
		}
		
		@Override
		public void mouseDragged(MouseEvent event) {
			placePlayer(event);
		}
		
		@Override
		public void mouseReleased(MouseEvent event) {
			this.mouseButton = 0;
		}
	}
	
	
	
	class RemoveListener extends AbstractSelectionListener {
		
		@Override
		public void mouseReleased(MouseEvent event){
			super.mouseReleased(event);
			if (event.getButton() != MouseEvent.BUTTON1)
				return;
			
			Rectangle roomSelection = view.getRoomSelection();
			float minSize = Entity.CASE * 0.4f;
			
			if (roomSelection.width < minSize) {
				roomSelection.x = roomSelection.x + roomSelection.width / 2 - (int) minSize / 2;
				roomSelection.width = (int) minSize;
			}
			if (roomSelection.height < minSize) {
				roomSelection.y = roomSelection.y + roomSelection.height / 2 - (int) minSize / 2;
				roomSelection.height = (int) minSize;
			}
			
			actualRoom.removeEntityIn(roomSelection);
		}
	}
	
	
	
	class MoveEntityListener extends PaintingListener {
		
		private PaintEntityListener movedListener;
		
		@Override
		public void mousePressed(MouseEvent e) {
			if (e.getButton() != MouseEvent.BUTTON1)
				return;
			
			final double sf = view.getScaleFactor();
			Entity moved = actualRoom.getEntityAt( new Vec2i((int)(e.getX() / sf), (int) (e.getY() / sf)));
			
			view.repaint();
			
			if (moved != null) {
				PaintingListener l = properties.getPaintingListener(moved.getClass());
				if (l instanceof PaintEntityListener) {
					this.movedListener = (PaintEntityListener) l;
				}
			}
			
			if (this.movedListener != null) {
				this.movedListener.setPinceau(new PinceauEntity(moved.getClass(), ""));
				this.movedListener.mousePressed(e);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (this.movedListener != null){
				this.movedListener.mouseReleased(e);
			}
			this.movedListener = null;
		}
		
		@Override
		public void mouseDragged(MouseEvent arg0) {
			if (this.movedListener != null){
				this.movedListener.mouseDragged(arg0);
			}
		}
	}
	
	
	/**
	 * Module pouvant être ajouté à un pinceau d'entité afin
	 * de régler graphiquement des paramètres, plutôt que dans
	 * une boîte de dialogue.
	 * @author Louis JEAN
	 */
	class ModuleDessinEntite extends PaintingListener {
		
		protected PaintEntityListener caller = null;
		
		protected ModuleDessinEntite(PaintEntityListener caller) {
			this.caller = caller;
		}
		
		public void setCaller(PaintEntityListener caller) {
			this.caller = caller;
		}
		
		public void setActivated() {
			view.setPaintingListener(this);
		}
		
		protected void backToCaller() {
			if (this.caller == null) {
				throw new NullPointerException("impossible de remettre l'ancien pinceau");
			}
			view.setPaintingListener(this.caller);
		}
	}
	
	
	class ModuleDirectionnalEntity extends ModuleDessinEntite implements DrawUnit {
		
		private Vec2d origine;
		private PropertyDirectionnalEntity direction;
		
		public ModuleDirectionnalEntity() {
			super(null);
		}
		
		@Override
		public void setActivated() {
			super.setActivated();
			view.addDrawUnit(this);
		}
		
		@Override
		protected void backToCaller() {
			super.backToCaller();
			view.removeDrawUnit(this);
			view.repaint();
		}
		
		@Override
		public void setCaller(PaintEntityListener caller) {
			super.setCaller(caller);
			getDirectionObjectFromCaller();
			
			Entity lastEnt = this.caller.getLastEntityCreated();
			if (lastEnt != null) {
				this.origine = lastEnt.getPositionf().asDouble();
			}
		}
		
		/** Cette méthode permet de mettre à jour la valeur du champs
		 * {@link #direction} en le faisant correspondre avec la direction
		 * de l'entité dessinée par le pinceau appelant. */
		private void getDirectionObjectFromCaller() {
			if (this.caller == null) return;
			
			Entity entity = this.caller.getLastEntityCreated();
			if (entity == null) {
				return;
			}
			if (!(entity instanceof IDirectionnalEntity)) {
				throw new IllegalArgumentException("L'entité dessinée par le pinceau" + 
						" n'est pas compatible avec le module");
			}
			
			this.direction = ((IDirectionnalEntity) entity).getDirectionObject();
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
			Vec2f cmouse = view.pointInRoom(e.getX(), e.getY()).asFloat();
			cmouse.x /= (float) Entity.CASE; cmouse.y /= (float) Entity.CASE;
			
			//<!>La direction est opposée car les repères ne sont pas les mêmes.
			this.direction.setDirection((float) MathUtil.angleMainValue(-
					Boussole.getPointCardinalPourAngle(
					Boussole.getDirection(this.origine, cmouse)).getOrientation()));
			
			view.repaint();
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			backToCaller();
		}
		
		@Override
		public void draw(Graphics2D g2d) {
			//dessin de la flèche
			Image img = ImagesLoader.get(ImagesLoader.flèche);
			
			g2d.translate(origine.x * view.getTailleCase(), origine.y * view.getTailleCase());
			g2d.rotate(-this.direction.getDirection());//<!>La direction est opposée.
			g2d.drawImage(img, 0, -view.getTailleCase() / 2, view.getTailleCase() * 3, view.getTailleCase(), null);
		}
	}
}
