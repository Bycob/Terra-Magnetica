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

package org.terramagnetica.opengl.gui;

import java.util.ArrayList;

import org.terramagnetica.opengl.engine.Viewport;
import org.terramagnetica.opengl.engine.Painter;

import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

/**
 * Panneau flottant, déplaçable à la souris, sur lequel des éléments peuvent
 * être accrochés (avec des coordonnées spécifiés).
 * <p>La taille du panneau est spécifiée à sa création et modifiable ensuite
 * avec les méthode appropriées.
 */
public class GuiMovingPanel extends GuiComponent implements MouseListener {
	
	private class MovePanelElement {
		public GuiComponent element;
		/** Les coordonnées de l'élément en unité openGL. Le point (0;0) est
		 * situé en bas à gauche du panneau. */
		public double x, y;
		
		public MovePanelElement(GuiComponent element, double x, double y) {
			this.element = element;
			this.x = x;
			this.y = y;
		}
		
		public MovePanelElement() {
			
		}
	}
	
	/** Les coordonnees du coin haut droit du viewport, en unité
	 * openGL. Le point (0;0) est situé en bas à gauche du panneau.*/
	private double viewXMin = 0, viewYMin = 0;
	/** Les dimensions du panneau, en unité openGL */
	private double width = 10, height = 10;
	
	private ArrayList<MovePanelElement> elements = new ArrayList<MovePanelElement>();
	
	
	public GuiMovingPanel(double width, double height) {
		this.width = width;
		this.height = height;
		
		//Ajustement au coin haut gauche
		this.viewYMin = this.height - this.getHeightGL();
	}
	
	@Override
	public void updateBounds() {
		if (this.getParent() == null) return;
		
		checkViewBounds(this.getParent().getChildBounds(this));
		super.updateBounds();
	}
	
	private void checkViewBounds(RectangleDouble bounds) {
		if (bounds.getWidth() > this.width - this.viewXMin) {
			this.viewXMin = this.width - bounds.getWidth();
		}
		if (bounds.getHeight() > this.height - this.viewYMin) {
			this.viewYMin = this.height - bounds.getHeight();
		}
	}
	
	@Override
	public RectangleDouble getChildBounds(GuiComponent child) {
		MovePanelElement mpe = null;
		
		for (int i = 0 ; i < this.elements.size() ; i++) {
			if (this.elements.get(i).element.equals(child)) {
				mpe = this.elements.get(i);
			}
		}
		
		if (mpe != null) {
			// Obtention de l'origine du repère du panneau.
			RectangleDouble thisBounds = this.getBoundsGL();
			Vec2d origin = new Vec2d(thisBounds.xmin - this.viewXMin, thisBounds.ymax - this.viewYMin);
			
			RectangleDouble childBounds = mpe.element.getBoundsGL();
			
			RectangleDouble newChildBounds = RectangleDouble.createRectangleFromCenter(
					origin.x + mpe.x, origin.y + mpe.y,
					childBounds.getWidth(), childBounds.getHeight());
			
			return newChildBounds;
		}
		
		return super.getChildBounds(child);
	}
	
	@Override
	public void draw() {
		Painter p = Painter.instance;
		
		Viewport oldViewport = p.getViewport();
		p.setViewport(new Viewport(this.getBoundsGL()));
		
		super.draw();
		
		p.setViewport(oldViewport);
	}
	
	/**
	 * Ajoute un élément au panneau.
	 * @param element - Le composant à ajouter.
	 * @param x - l'abscisse de son centre, en unité openGL. Le point (0;0) est
	 * situé en bas à gauche du panneau.
	 * @param y - l'ordonnée de son centre, en unité openGL. Le point (0;0) est
	 * situé en bas à gauche du panneau.
	 */
	public void addElement(GuiComponent element, double x, double y) {
		MovePanelElement elemObject = new MovePanelElement(element, x, y);
		this.elements.add(elemObject);
		this.add(element);
	}
	
	public GuiComponent[] getAllElement() {
		GuiComponent[] ret = new GuiComponent[this.elements.size()];
		
		for (int i = 0 ; i < this.elements.size() ; i++) {
			ret[i] = this.elements.get(i).element;
		}
		
		return ret;
	}
	
	/** Les coordonnées de départ de la souris lors du déplacement du panneau. */
	private Vec2d dragStart = null;
	private Vec2d startingViewLoc = null;
	
	@Override
	public void eventMouse(MouseEvent event) {
		boolean buttonLeft = event.getButton() == BUTTON_LEFT;
		
		double glX = GuiWindow.getInstance().getYOnGLOrtho(event.getX());
		double glY = GuiWindow.getInstance().getXOnGLOrtho(event.getY());
		
		if (buttonLeft && event.getState() && this.dragStart == null) {//Début du déplacement
			this.dragStart = new Vec2d(glX, glY);
			this.startingViewLoc = new Vec2d(this.viewXMin, this.viewYMin);
			updateBounds();
		}
		else if (this.dragStart != null) {//Pendant le déplacement
			Vec2d dragStop = new Vec2d(glX, glY);
			
			this.viewXMin = Math.max(0, Math.min(this.width - this.getWidthGL(), this.dragStart.x - dragStop.x + this.startingViewLoc.x));
			this.viewYMin = Math.max(0, Math.min(this.height - this.getHeightGL(), this.dragStart.y - dragStop.y + this.startingViewLoc.y));
			
			if (buttonLeft && !event.getState()) {//Si le joueur a relaché le bouton de la souris
				this.dragStart = null;
				this.startingViewLoc = null;
			}
			
			updateBounds();
		}
		else {
			this.dragStart = null;
		}
	}
}
