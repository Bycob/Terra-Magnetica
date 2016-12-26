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

package org.terramagnetica.opengl.gui;

import java.util.ArrayList;

import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Viewport;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.Vec2d;

public class GuiScrollPanel extends GuiComponent implements MouseListener {
	
	public enum ScrollBar {
		HORIZONTAL,
		VERTICAL,
		BOTH;
		
		public boolean contains(ScrollBar scrollBar) {
			return this == scrollBar || this == BOTH;
		}
		
		public ScrollBar intersection(ScrollBar other) {
			if (this == BOTH) {
				return other;
			}
			else if (other == BOTH) {
				return this;
			}
			else if (this == other) {
				return this;
			}
			else return null;
		}
	}
	
	private GuiContainer content;
	private RectangleDouble viewport;
	private double contentX;
	private double contentY;
	
	private double yVBar;
	private double xHBar;
	private ScrollBar scrolling;
	
	private double widthVBarGL = 0.05;
	private double heightHBarGL = 0.05;
	
	private ScrollBar scrollBarsUsed;
	private boolean autoWrap = false;
	
	private boolean fillBackground = true;
	
	public GuiScrollPanel() {
		this(new GuiContainer());
	}
	
	public GuiScrollPanel(GuiContainer content) {
		this(content,
				GuiWindow.getInstance().getOrtho().left,
				GuiWindow.getInstance().getOrtho().top,
				GuiWindow.getInstance().getOrtho().right,
				GuiWindow.getInstance().getOrtho().bottom);
	}
	
	public GuiScrollPanel(GuiContainer content, double xmin, double ymin, double xmax, double ymax) {
		this.content = content != null ? content : new GuiContainer();
		this.content.setParent(this);
		this.setColor(new Color4f(0.5f, 0.5f, 0.5f));
		this.scrollBarsUsed = ScrollBar.BOTH;
		this.setBoundsGL(new RectangleDouble(xmin, ymin, xmax, ymax));
		this.resetContentLocation();
	}
	
	@Override
	public void drawComponent(Painter p) {
		
		double wheel = this.theWindow.getMouseInput().getMouseDWheel();
		double glWheel = wheel * 0.1; //FIXME Ajuster ce paramètre
		this.setBarLocation(ScrollBar.VERTICAL, this.yVBar + glWheel);
		
		p.set2DConfig();
		p.setTexture(null);
		Color4f color = this.getColor().clone();
		
		ScrollBar barsUsed = getBarsUsedApplyingAutoWrap();
		
		boolean drawHBar = barsUsed == ScrollBar.BOTH || barsUsed == ScrollBar.HORIZONTAL;
		boolean drawVBar = barsUsed == ScrollBar.BOTH || barsUsed == ScrollBar.VERTICAL;
		boolean isBoth = barsUsed == ScrollBar.BOTH;
		
		//dessin des arrières-plan.
		if (this.fillBackground) {
			p.setColor(color);
			GLUtil.drawQuad2D(this.viewport.xmin, this.viewport.ymin, this.viewport.xmax, this.viewport.ymax, p);
		}
		
		color.setAlphaf(0.5f);
		p.setColor(color);
		if (drawVBar) {
			GLUtil.drawQuad2D(
					this.viewport.xmax, this.viewport.ymin,
					this.viewport.xmax + this.widthVBarGL, this.viewport.ymax, p);
		}
		if (drawHBar) {
			GLUtil.drawQuad2D(
					this.viewport.xmin, this.viewport.ymax,
					this.viewport.xmax, this.viewport.ymax - this.heightHBarGL, p);
		}
		
		if (isBoth) {//dessine le carré en bas à droite, qui reste si les deux barres sont dessinées.
			color.setAlphaf(0.75f);
			p.setColor(color);
			GLUtil.drawQuad2D(
					this.viewport.xmax, this.viewport.ymax,
					this.viewport.xmax + this.widthVBarGL, this.viewport.ymax - this.heightHBarGL, p);
		}
		
		//dessin des barres de défilement.
		color = this.getColor().clone();
		Color4f external = color.clone();
		external.setAlphaf(0f);
		Color4f internal = new Color4f(1, 1, 1, 0.5f);
		
		if (drawHBar) {
			GLUtil.drawDoubleGradientPaintedRectangle(
					this.xHBar - this.size(ScrollBar.HORIZONTAL) / 2d, this.viewport.ymax,
					this.xHBar + this.size(ScrollBar.HORIZONTAL) / 2d, this.viewport.ymax - this.heightHBarGL,
					external, internal, false, p);
		}
		if (drawVBar) {
			GLUtil.drawDoubleGradientPaintedRectangle(
					this.viewport.xmax, this.yVBar + this.size(ScrollBar.VERTICAL) / 2d,
					this.viewport.xmax + this.widthVBarGL, this.yVBar - this.size(ScrollBar.VERTICAL) / 2d,
					external, internal, true, p);
		}
		
		//dessin du contenu.
		Viewport oldViewport = p.getViewport();
		
		p.setViewport(new Viewport(this.viewport.clone()));
		this.content.draw(p);
		p.setViewport(oldViewport);
	}
	
	@Override
	public void eventMouse(MouseEvent event) {
		RectangleDouble recVBar = new RectangleDouble(
				this.viewport.xmax, this.viewport.ymin,
				this.viewport.xmax + this.widthVBarGL, this.viewport.ymax);
		RectangleDouble recHBar = new RectangleDouble(
				this.viewport.xmin, this.viewport.ymax,
				this.viewport.xmax, this.viewport.ymax - this.heightHBarGL);
		
		Vec2d mouseGL = new Vec2d(
				theWindow.getXOnGLOrtho(event.getX()),
				theWindow.getYOnGLOrtho(event.getY()));
		boolean scrolling = this.scrolling != null;
		boolean state = event.getState();
		int button = event.getButton();
		boolean eOnVBar = recVBar.contains(mouseGL);
		boolean eOnHBar = recHBar.contains(mouseGL);
		
		if (eOnVBar || eOnHBar) {
			if (!scrolling && state && button == BUTTON_LEFT) {
				this.scrolling = eOnVBar ? ScrollBar.VERTICAL : ScrollBar.HORIZONTAL;
				this.setBarLocation(this.scrolling, eOnVBar ? mouseGL.y : mouseGL.x);
			}
		}
		
		if (scrolling && !state && button == BUTTON_LEFT) {
			this.scrolling = null;
			scrolling = false;
		}
		
		if (scrolling) {
			this.setBarLocation(this.scrolling, this.scrolling == ScrollBar.VERTICAL ? mouseGL.y : mouseGL.x);
		}
	}
	
	@Override
	public MouseListener[] getMouseListeners() {
		ArrayList<MouseListener> listeners = Util.createList(super.getMouseListeners());
		
		listeners.addAll(Util.createList(this.content.getMouseListeners()));
		
		return listeners.toArray(new MouseListener[0]);
	}
	
	@Override
	public KeyboardListener[] getKeyboardListeners() {
		ArrayList<KeyboardListener> listeners = Util.createList(super.getKeyboardListeners());
		
		listeners.addAll(Util.createList(this.content.getKeyboardListeners()));
		
		return listeners.toArray(new KeyboardListener[0]);
	}
	
	private void setBarLocation(ScrollBar s, double xy) {
		double hsize = size(s) / 2d;
		
		if (s == ScrollBar.VERTICAL) {
			this.yVBar = MathUtil.valueInRange_d(xy, this.viewport.ymax + hsize, this.viewport.ymin - hsize);
		}
		if (s == ScrollBar.HORIZONTAL) {
			this.xHBar = MathUtil.valueInRange_d(xy, this.viewport.xmin + hsize, this.viewport.xmax - hsize);
		}
		
		this.updateContentLocation();
	}
	
	/**
	 * Donne la taille de la barre de scrolling passée en paramètre.
	 * @param s - La barre de scrolling dont on souhaite connaitre la
	 * taille. Seuls {@link ScrollBar#HORIZONTAL} et {@link ScrollBar#VERTICAL}
	 * sont autorisés.
	 * @return La taille de la barre de scrolling concernée, en unité
	 * openGL.
	 */
	public double size(ScrollBar s) {
		if (s == ScrollBar.VERTICAL) {
			return Math.min(
					this.viewport.getHeight() * this.viewport.getHeight() / this.content.getBoundsGL().getHeight(),
					this.viewport.getHeight());
		}
		else if (s == ScrollBar.HORIZONTAL) {
			return Math.min(
					this.viewport.getWidth() * this.viewport.getWidth() / this.content.getBoundsGL().getWidth(),
					this.viewport.getWidth());
		}
		else {
			throw new IllegalArgumentException("Only VERTICAL or HORIZONTAL");
		}
	}

	public GuiContainer getContent() {
		return this.content;
	}
	
	public void setContent(GuiContainer newContents) {
		if (newContents == null) newContents = new GuiContainer();
		this.content = newContents;
		this.content.setParent(this);
		this.resetContentLocation();
	}
	
	public ScrollBar getScrollBarsUsed() {
		return this.scrollBarsUsed;
	}
	
	/**
	 * Définit les barre de défilement utilisées. C'est à dire, celle
	 * qui vont apparaître au rendu : Celle sur la hauteur, celle sur la
	 * longueur, ou les deux. Par défaut, les deux apparaissent.
	 * @param scrollBarsUsed - Les barres de défilement utilisées.
	 */
	public void setScrollBarsUsed(ScrollBar scrollBarsUsed) {
		this.scrollBarsUsed = scrollBarsUsed;
		this.resetContentLocation();
	}
	
	/**
	 * Définit la propriété "autoWrap".
	 * <p>Celle-ci, si elle est activée, fait en sorte que les barres
	 * de défilement n'apparaissent que si elles sont nécessaires, donc
	 * que le contenu est trop grand. Par défaut, elle est désactivée.
	 * <p>Même si les barres n'apparaissent pas elle prennent quand même
	 * la place : le viewport est légèrement réduit par rapport aux limites
	 * réelles du composant.
	 * <p>{@literal <!!!>} SEMBLE NE PAS FONCTIONNER
	 * @param flag - {@code true} pour activer l'autoWrap, {@code false}
	 * pour le désactiver.
	 */
	public void setAutoWrap(boolean flag) {
		this.autoWrap = flag;
	}
	
	/**
	 * Permet de connaitre les effets de l'autoWrap sur les barres
	 * de défilement.
	 * @return Les barres de défilement qui apparraissent si l'autoWrap
	 * est activé.
	 * @see #setAutoWrap(boolean)
	 */
	private ScrollBar getBarsUsedApplyingAutoWrap() {
		if (this.autoWrap) {
			boolean usingVBar = this.scrollBarsUsed.contains(ScrollBar.VERTICAL) && this.content.getHeightGL() <= this.viewport.getHeight();
			boolean usingHBar = this.scrollBarsUsed.contains(ScrollBar.HORIZONTAL) && this.content.getWidthGL() <= this.viewport.getWidth();
			
			ScrollBar scrollBarsUsed = null;
			if (usingHBar) scrollBarsUsed = ScrollBar.HORIZONTAL;
			if (usingVBar) {
				scrollBarsUsed = scrollBarsUsed == ScrollBar.HORIZONTAL ? ScrollBar.BOTH : ScrollBar.VERTICAL;
			}
			return scrollBarsUsed;
		}
		return this.scrollBarsUsed;
	}
	
	/**
	 * Définit si on peint l'arrière-plan ou non. Par défaut, cette
	 * propriété est mise à {@code true}
	 * @param flag - {@code true} si un arrière-plan doit être
	 * peint, {@code false} sinon.
	 */
	public void fillBackground(boolean flag) {
		this.fillBackground = flag;
	}
	
	@Override
	void updateBounds() {
		super.updateBounds();
		
		RectangleDouble oldViewport = this.viewport == null ? new RectangleDouble() : this.viewport;
		this.viewport = getBoundsGL();
		switch (this.scrollBarsUsed) {
		case HORIZONTAL :
			this.viewport.ymax += this.heightHBarGL;
			break;
		case BOTH :
			this.viewport.ymax += this.heightHBarGL;
		case VERTICAL :
			this.viewport.xmax -= this.widthVBarGL;
		}
		
		if (!oldViewport.equals(this.viewport)) {
			double ratio = 0;
			if (oldViewport.getHeight() > 0) {
				ratio = (oldViewport.ymin - this.yVBar) / oldViewport.getHeight();
			}
			this.setBarLocation(ScrollBar.VERTICAL, this.viewport.ymin - this.viewport.getHeight() * ratio);
			
			ratio = 0;
			if (oldViewport.getWidth() > 0) {
				ratio = (oldViewport.xmin + this.xHBar) / oldViewport.getWidth(); 
			}
			this.setBarLocation(ScrollBar.HORIZONTAL, this.viewport.xmin + this.viewport.getWidth() * ratio);
		}
		
		this.updateContentLocation();
	}
	
	@Override
	RectangleDouble getChildBounds(GuiComponent child) {
		if (child == this.content && this.viewport != null) {
			RectangleDouble contBounds = this.content.getAbsoluteBoundsGL().clone();
			contBounds.moveAt(this.contentX, this.contentY);
			
			contBounds.xmax = Math.max(contBounds.xmax, this.viewport.xmax);
			contBounds.ymax = Math.min(this.viewport.ymax, contBounds.ymax);
			
			switch (this.scrollBarsUsed) {
			case HORIZONTAL :
				contBounds.ymax = this.viewport.ymax;
				break;
			case VERTICAL :
				contBounds.xmax = this.viewport.xmax;
				break;
			case BOTH :
			default :
				break;
			}
			
			return contBounds;
		}
		return super.getChildBounds(child);
	}
	
	protected void updateContentLocation() {
		double realSize;
		double sizeInBar;
		
		if (this.viewport.getHeight() > 0) {
			sizeInBar = this.viewport.ymin - this.yVBar - this.size(ScrollBar.VERTICAL) / 2d;
			realSize = sizeInBar * this.content.getBoundsGL().getHeight() / this.viewport.getHeight();
			this.contentY = Math.max(this.viewport.ymin + realSize, this.viewport.ymin);
		}
		else {
			this.contentY = this.viewport.ymin;
		}
		
		if (this.viewport.getWidth() > 0) {
			sizeInBar = this.viewport.xmin + this.size(ScrollBar.HORIZONTAL) / 2d - this.xHBar;
			realSize = sizeInBar * this.content.getBoundsGL().getWidth() / this.viewport.getWidth();
			this.contentX = Math.min(this.viewport.xmin - realSize, this.viewport.xmin);
		}
		else {
			this.contentX = this.viewport.xmin;
		}
		
		this.content.updateBounds();
	}
	
	protected void resetContentLocation() {
		this.contentX = this.viewport.xmin;
		this.contentY = this.viewport.ymin;
		
		this.content.updateBounds();
		
		this.setBarLocation(ScrollBar.HORIZONTAL, this.viewport.xmin);
		this.setBarLocation(ScrollBar.VERTICAL, this.viewport.ymin);
	}
	
	public void actualise() {
		this.resetContentLocation();
	}
	
	@Override
	public GuiScrollPanel clone() {
		GuiScrollPanel result = (GuiScrollPanel) super.clone();
		
		result.scrolling = this.scrolling;
		result.viewport = this.viewport.clone();
		result.content = (GuiContainer) this.content.clone();
		result.content.setParent(result);
		
		return result;
	}
}
