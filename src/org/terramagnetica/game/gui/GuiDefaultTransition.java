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

package org.terramagnetica.game.gui;

import java.util.ArrayList;
import java.util.List;

import org.terramagnetica.opengl.engine.GLOrtho;
import org.terramagnetica.opengl.gui.GuiBorderLayout;
import org.terramagnetica.opengl.gui.GuiComponent;
import org.terramagnetica.opengl.gui.GuiWindow;

import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

/**
 * La transition par défaut, utilisée lors d'un changement d'écran
 * interne à l'interface graphique.
 * <p>Cette transition consiste à faire rentrer tous les composants
 * depuis l'exterieur de l'image, à partir de la direction choisie
 * par l'utilisateur.
 * @author Louis JEAN
 *
 */
public class GuiDefaultTransition implements TransitionManager {
	
	public static final int GAUCHE = 1, BAS = 2, DROITE = 4, HAUT = 8;
	
	protected List<Entry> components = new ArrayList<Entry>();
	protected GuiBorderLayout border = null;
	private double layoutBorderDefault = 0;
	protected GameScreen parent;
	
	protected double speed = 1.0;
	
	public GuiDefaultTransition(GameScreen parent) {
		if (parent == null) 
			throw new IllegalArgumentException("le GamePanel parent est null !");
		this.parent = parent;
	}
	
	@Override
	public void appear(int appearCount) {
		int var0 = parent.timeToAppear();
		if (var0 == 0) return;
		
		double progression = 1.0 - (double) appearCount / (double) var0;
		if (progression < 0.0) {
			progression = 0.0;
		}
		
		double var1 = progression * this.speed;
		
		for (Entry entry : this.components) {
			entry.locationDefault();
			entry.component.translateGL(
					entry.translationX * var1,
					entry.translationY * var1);
		}
		
		if (this.border != null) {
			this.border.setBorderHeight((1 - var1) * this.layoutBorderDefault);
		}
	}
	
	@Override
	public void destroy(int destroyCount) {
		int var0 = parent.timeToDestroy();
		
		if (var0 == 0) return;
		
		if (destroyCount > var0) {
			destroyCount = var0;
		}
		
		double var1 = (double) destroyCount / var0 * this.speed;
		
		for (Entry entry : this.components) {
			entry.locationDefault();
			entry.component.translateGL(
					entry.translationX * var1,
					entry.translationY * var1);
		}
		
		if (this.border != null) {
			this.border.setBorderHeight((1 - var1) * this.layoutBorderDefault);
		}
	}
	
	public void add(GuiComponent component, int direction) {
		this.components.add(new Entry(component, direction));
	}
	
	public void remove(GuiComponent component) {
		for (int i = 0 ; i < this.components.size() ; i++) {
			if (this.components.get(i).component == component) {
				this.components.remove(i);
			}
		}
	}
	
	public void removeAll() {
		this.components = new ArrayList<Entry>();
	}
	
	public void setSpeed(double speed) {
		this.speed = MathUtil.valueInRange_d(speed, 0, Double.MAX_VALUE);
	}
	
	/**
	 * On peut définir un {@link GuiBorderLayout} qui fera partie
	 * de l'animation. La taille de ses bordures augmenteront pendant
	 * l'apparition (d'une taille nulle à une taille normale) et
	 * réduiront pendant la disparition (d'une taille normale à une
	 * taille nulle).
	 * @param border - Le {@link GuiBorderLayout} qui doit jouer l'animation.
	 */
	public void setBorderLayout(GuiBorderLayout border) {
		this.border = border;
		if (this.border != null) {
			this.layoutBorderDefault = this.border.getBorderHeight();
		}
		else { this.layoutBorderDefault = 0; }
	}
	
	protected class Entry {
		
		protected GuiComponent component;
		protected RectangleDouble locationDefault;
		protected int direction;
		protected double translationX;
		protected double translationY;
		
		public Entry(GuiComponent component, int direction) {
			this.component = component;
			this.direction = direction;
			this.locationDefault = component.getAbsoluteBoundsGL().clone();
			
			Vec2d origin = new Vec2d(0, 0);
			Vec2d destination = new Vec2d(0, 0);
			GLOrtho r = GuiWindow.getInstance().getOrtho();
			
			if ((direction & GAUCHE) == GAUCHE) {
				origin.x = this.locationDefault.xmax;
				destination.x = r.left;
			}
			if ((direction & BAS) == BAS) {
				origin.y = this.locationDefault.ymin;
				destination.y = r.bottom;
			}
			if ((direction & DROITE) == DROITE) {
				origin.x = this.locationDefault.xmin;
				destination.x = r.right;
			}
			if ((direction & HAUT) == HAUT) {
				origin.y = this.locationDefault.ymax;
				destination.y = r.top;
			}
			
			this.translationX = destination.x - origin.x;
			this.translationY = destination.y - origin.y;
		}
		
		public void locationDefault() {
			this.component.setBoundsGL(this.locationDefault.clone());
		}
	}
}
