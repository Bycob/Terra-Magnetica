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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.terramagnetica.creator.PaintingListener;
import org.terramagnetica.game.lvldefault.DecorType;
import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.LandscapeTile;
import org.terramagnetica.game.lvldefault.Room;

import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.swing.DrawUnit;

@SuppressWarnings("serial")
public class LevelDefaultView extends JPanel {
	
	private static HashMap<DecorType, Color> selectionColorMap = new HashMap<DecorType, Color>();
	private static HashMap<DecorType, Color> casesColorMap = new HashMap<DecorType, Color>();
	
	static {
		//selection
		selectionColorMap.put(DecorType.MONTS, Color.BLACK);
		selectionColorMap.put(DecorType.GROTTE, Color.LIGHT_GRAY);
		
		selectionColorMap.put(DecorType.ENFERS, new Color(150, 91, 47));
		
		//cases
		casesColorMap.put(DecorType.MONTS, Color.DARK_GRAY);
		casesColorMap.put(DecorType.GROTTE, Color.GRAY);
		
		casesColorMap.put(DecorType.ENFERS, new Color(103, 79, 61));
	}
	
	
	
	protected Room theRoom;
	/** Le rectangle sur lequel on repeint. On ne repeint pas tout, pour économiser des performance. */
	protected Rectangle clip;
	
	/** La selection est en cours */
	private boolean isSelecting;
	/** Les coordonnées du point qui commence la sélection (l'origine) */
	private Vec2i startSelection;
	/** Le rectangle qui représente la sélection. */
	private Rectangle selection;
	
	/** le drag est activé */
	private boolean isDragging;
	private Entity dragged;
	
	/** {@code true} si on doit dessiner les cases. */
	private boolean drawCases = false;
	
	private int dessinCase = 32;
	private double scaleFactor = (double) dessinCase / (double) Entity.CASE;
	
	private ArrayList<DrawUnit> drawUnits = new ArrayList<DrawUnit>();
	
	/** @param room - La salle dessinée, qui pourra valoir {@code null}. */
	public LevelDefaultView(Room room){
		super();
		this.setRoom(room);
		this.addMouseListener(new Focuser());
	}
	
	public void addDrawUnit(DrawUnit drawUnit) {
		if (drawUnit == null) throw new NullPointerException();
		this.drawUnits.add(drawUnit);
	}
	
	public void removeDrawUnit(DrawUnit drawUnit) {
		if (drawUnit == null) return;
		this.drawUnits.remove(drawUnit);
	}
	
	@Override
	public void paint(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.scale(scaleFactor, scaleFactor);
		
		
		LandscapeTile[][] decor = this.theRoom.getLandscape();
		List<Entity> entities = this.theRoom.getEntities();
		
		ArrayList<Rectangle> casesList = new ArrayList<Rectangle>();
		
		//dessin du décor
		for (int i = clip.x ; i < clip.x + clip.width ; i++){
			for (int j = clip.y ; j < clip.y + clip.height ; j++){
				Rectangle landBounds = new Rectangle(
						decor[i][j].getCoordonnéesToDraw().x, 
						decor[i][j].getCoordonnéesToDraw().y, 
						decor[i][j].getDimensions().getWidth(), 
						decor[i][j].getDimensions().getHeight());
				
				g2d.drawImage(decor[i][j].getImage(theRoom.getDecorType()),
						landBounds.x, landBounds.y, landBounds.width, landBounds.height,
						this);
				
				if (this.drawCases) casesList.add(landBounds);
			}
		}
		
		//dessin des cases
		if (this.drawCases) {
			Color casesColor = casesColorMap.get(this.theRoom.getDecorType());
			if (casesColor == null) casesColor = Color.DARK_GRAY;//couleur par défaut : gris foncé
			g2d.setColor(casesColor);
			
			for (Rectangle caze : casesList) {
				g2d.drawRect(caze.x, caze.y, caze.width, caze.height);
			}
		}
		
		//dessin des entités
		for (Entity objet : entities){
			g2d.drawImage(objet.getImage(),
					objet.getCoordonnéesToDraw().x,
					objet.getCoordonnéesToDraw().y,
					objet.getImgDimensions().getWidth(),
					objet.getImgDimensions().getHeight(),
					this);
		}
		
		//dessin de l'entité déplacée sur l'image, s'il y en a une
		if (isDragging) {
			if (dragged != null) {
				g2d.drawImage(dragged.getImage(),
						dragged.getCoordonnéesToDraw().x,
						dragged.getCoordonnéesToDraw().y,
						dragged.getImgDimensions().getWidth(),
						dragged.getImgDimensions().getHeight(),
						this);
			}
		}
		
		g2d.scale(1 / scaleFactor, 1 / scaleFactor);
		
		//dessin de la selection
		if (isSelecting){
			Color selectionColor = selectionColorMap.get(this.theRoom.getDecorType());
			if (selectionColor == null) selectionColor = Color.BLACK;//couleur par défaut : noir
			
			g2d.setColor(selectionColor);
			g2d.draw(selection);
		}
		
		//Extensions complémentaires
		for (DrawUnit d : this.drawUnits) {
			d.draw(g2d);
		}
		
		this.paintChildren(g);
	}
	
	public void setRoom(Room newRoom) {
		this.stopDrag();
		this.stopSelecting();
		this.theRoom = newRoom;
		this.setPreferredSize(theRoom != null ?
				new Dimension(
						theRoom.getDimensions().getWidth() * dessinCase,
						theRoom.getDimensions().getHeight() * dessinCase) :
				new Dimension(0, 0));
	}
	
	public Vec2i pointInRoom(int pointOnScreenX, int pointOnScreenY) {
		return new Vec2i((int) (pointOnScreenX / scaleFactor), (int) (pointOnScreenY / scaleFactor));
	}
	
	public Vec2i caseInRoom(int pointOnScreenX, int pointOnScreenY) {
		return new Vec2i(pointOnScreenX / dessinCase, pointOnScreenY / dessinCase);
	}
	
	/**
	 * Définit l'outil de dessin. Il ne peut y avoir qu'un seul outil
	 * de dessin activé à la fois.
	 * @param l - Un listener qui écoute les entrées souris et réagit
	 * en conséquence, par exemple en ajoutant des entités à la salle, 
	 * en modifiant le décor...
	 */
	public void setPaintingListener(PaintingListener l){
		for (MouseListener listener : this.getMouseListeners()){
			if (listener instanceof PaintingListener)
				this.removeMouseListener(listener);
		}
		for (MouseMotionListener listener : this.getMouseMotionListeners()){
			if (listener instanceof PaintingListener)
				this.removeMouseMotionListener(listener);
		}
		
		this.addMouseListener(l);
		this.addMouseMotionListener(l);
	}
	
	/**
	 * Donne l'outil de dessin actuellement utilisé.
	 * @return
	 */
	public PaintingListener getPaintingListener(){
		MouseListener ml = null;
		MouseMotionListener mml = null;
		
		for (MouseListener listener : this.getMouseListeners()){
			if (listener instanceof PaintingListener)
				ml = listener;
		}
		for (MouseMotionListener listener : this.getMouseMotionListeners()){
			if (listener instanceof PaintingListener)
				mml = listener;
		}
		
		if (ml != null && mml != null){
			if (ml.equals(mml) && ml instanceof PaintingListener){
				return (PaintingListener) ml;
			}
		}
			
		return null;
	}
	
	public void setClip(JScrollPane scroll){
		LandscapeTile[][] decor = this.theRoom.getLandscape();
		int rWidth = theRoom != null ? theRoom.getDimensions().getWidth() : 0;
		int rHeight = theRoom != null ? theRoom.getDimensions().getHeight() : 0;
		
		this.clip = scroll.getViewport().getBounds();
		this.clip.x = scroll.getHorizontalScrollBar().getValue() / dessinCase;
		this.clip.y = scroll.getVerticalScrollBar().getValue() / dessinCase;
		this.clip.height = clip.height / dessinCase + 3;
		this.clip.width = clip.width / dessinCase + 3;
		
		if (clip.x < 0 || clip.x >= rWidth) clip.x = 0;
		if (clip.y < 0 || clip.y >= rHeight) clip.y = 0;
		
		if (this.clip.getMaxX() > rWidth) clip.width = decor.length - clip.x;
		if (this.clip.getMaxY() > rHeight) clip.height = decor[0].length - clip.y;
	}
	
	/**
	 * Définit le niveau de zoom actuel de l'affichage.
	 * @param zoom - Le nouveau niveau de zoom à adopter, en % de la
	 * taille réelle (1 case = {@link Entity#CASE} pixels)
	 */
	public void setZoom(double zoom) {
		this.dessinCase = (int) (Entity.CASE * (zoom / 100.0));
		this.scaleFactor = (double) this.dessinCase / (double) Entity.CASE;
		
		this.setPreferredSize(theRoom != null ?
				new Dimension(
						theRoom.getDimensions().getWidth() * dessinCase,
						theRoom.getDimensions().getHeight() * dessinCase) :
				new Dimension(0, 0));
	}
	
	public double getZoom() {
		return this.dessinCase * 100.0 / Entity.CASE;
	}
	
	/**
	 * Donne l'échelle de la représentation du niveau.
	 * <p>Un <i>scale factor</i> de {@code 1} signifie qu'une case est
	 * dessinée avec une taille de {@link Entity#CASE} pixels.
	 * <p>Par défaut le <i>scale factor</i> est défini à 0.125 et peut
	 * être modifié via la méthode {@link #setZoom(double)}.
	 * @return
	 */
	public double getScaleFactor() {
		return this.scaleFactor;
	}
	
	/** donne la taille en pixels d'une case sur la représentation à l'écran. */
	public int getTailleCase() {
		return this.dessinCase;
	}
	
	public void setSelection(int x1, int y1, int x2, int y2){
		isSelecting = true;
		selection = new Rectangle((x1 > x2)? x2 : x1,
				(y1 > y2)? y2 : y1,
				(x1 > x2)? x1 - x2 : x2 - x1,
				(y1 > y2)? y1 - y2 : y2 - y1);
		startSelection = new Vec2i(x1 , y1);
	}
	
	public void extendsSelection(int x, int y){
		if (isSelecting){
			if (x > startSelection.x){
				selection.width = x - selection.x;
				selection.x = startSelection.x;
			}
			else{
				selection.width = startSelection.x - x;
				selection.x = x;
			}
			
			if (y > startSelection.y){
				selection.height = y - selection.y;
				selection.y = startSelection.y;
			}
			else{
				selection.height = startSelection.y - y;
				selection.y = y;
			}
		}
	}
	
	public Rectangle getSelection(){
		return selection;
	}
	
	public Rectangle getRoomSelection(){
		Rectangle result = new Rectangle((int) (selection.x / scaleFactor),
				(int) (selection.y / scaleFactor),
				(int) (selection.width / scaleFactor),
				(int) (selection.height / scaleFactor));
		return result;
	}
	
	public void stopSelecting(){
		this.isSelecting = false;
	}
	
	public void setDragged(Entity dragged){
		this.isDragging = true;
		if (dragged == null) 
			throw new NullPointerException(this.getClass().getName() + ".setDragged() : dragged == null");
		this.dragged = dragged;
	}
	
	public void moveDrag(int x, int y){
		if (isDragging == true)
			this.dragged.setCoordonnées(x, y);
	}
	
	public void stopDrag(){
		this.isDragging = false;
		this.dragged = null;
	}
	
	/**
	 * Determine si les cases doivent être dessinées ou non.
	 * Lorsque les cases sont dessinées leur contour s'affiche
	 * à l'écran : cela peut être utile notament dans les niveaux
	 * où on ne peut pas voir les bords d'un morceau de décor
	 * inaccessible  (ex : décor de grotte).
	 * @param flag
	 */
	public void drawCases(boolean flag) {
		this.drawCases = flag;
	}
	
	/**
	 * Permet de prendre le focus à chaque évenement souris sur le
	 * panel.
	 * @author Louis JEAN
	 *
	 */
	private class Focuser implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			LevelDefaultView.this.requestFocus();
		}
		@Override
		public void mousePressed(MouseEvent e) {
			LevelDefaultView.this.requestFocus();
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			LevelDefaultView.this.requestFocus();
		}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}
}
