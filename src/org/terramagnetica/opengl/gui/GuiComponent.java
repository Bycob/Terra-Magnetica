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

import org.terramagnetica.opengl.engine.Painter;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.MathUtil;
import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.RectangleInt;

public abstract class GuiComponent implements Cloneable {
	
	protected final Painter thePainter = Painter.instance;
	protected final GuiWindow theWindow = GuiWindow.getInstance();
	protected final GuiTextPainter theTextPainter = new GuiTextPainter(this.thePainter);
	
	private GuiComponent parent;
	protected ArrayList<GuiComponent> children = new ArrayList<GuiComponent>();
	
	private RectangleDouble bounds = new RectangleDouble();
	private RectangleDouble boundsDefault = new RectangleDouble();
	
	private ArrayList<Behavior> behavior = new ArrayList<Behavior>();
	
	private Color4f color = new Color4f();
	private boolean visible = true;
	
	void setParent(GuiComponent parent) {
		this.parent = parent;
		this.updateBounds();
	}
	
	GuiComponent getParent() {
		return this.parent;
	}
	
	/** Cette m�thode se sert de son environnement
	 * pour d�duire les actions � faire. <br>
	 * Si elle n'est pas comp�tente pour ces actions,
	 * elle retourne un event qui indique ce qu'il faut faire. */
	public GuiActionEvent processLogic() {
		return GuiActionEvent.NULL_EVENT;
	}
	
	/** dessine le composant, puis appelle  la m�thode {@link #draw()}
	 * des sous-composants. */
	public void draw(){
		for (Behavior b : this.behavior) {
			b.update();
		}
		
		if (isVisible()) {
			drawComponent();
			drawChildren();
		}
	}
	
	/** Dessine le composant. */
	protected void drawComponent() {}
	
	protected void drawChildren() {
		for (GuiComponent child : children) {
			child.draw();
		}
	}
	
	public void add(GuiComponent c) {
		if (c != null) {
			c.setParent(this);
			this.children.add(c);
		}
	}
	
	public void remove(GuiComponent c) {
		if (c == null) throw new NullPointerException("Impossible de supprimer.");
		if (c != null) c.setParent(null);
		this.children.remove(c);
		this.children.contains(c);
	}
	
	public void removeAll(){
		for (GuiComponent child : this.children) {
			child.setParent(null);
		}
		this.children = new ArrayList<GuiComponent>();
	}
	
	protected static RectangleDouble checkedBoundsGL(RectangleDouble bounds) {
		RectangleDouble result = bounds.clone();
		
		result.xmin = Math.min(bounds.xmin, bounds.xmax); 
		result.xmax = Math.max(bounds.xmin, bounds.xmax);
		result.ymin = Math.max(bounds.ymin, bounds.ymax);
		result.ymax = Math.min(bounds.ymin, bounds.ymax);
		
		return result;
	}
	
	protected static RectangleInt checkedBoundsDisp(RectangleInt bounds) {
		return checkedBoundsGL(bounds.asDouble()).asInteger();
	}
	
	/**
	 * D�finit la place du composant sur le rep�re openGL.
	 * @param boundsGL - le rectangle dans lequel est th�oriquement
	 * dessin� le composant, sur le rep�re openGL.
	 */
	public void setBoundsGL(RectangleDouble boundsGL) {
		this.boundsDefault = checkedBoundsGL(boundsGL);
		this.updateBounds();
	}
	
	/**
	 * Donne les limites actuelles de ce composant dans le rep�re
	 * openGL. Par convention : 
	 * <ul> <li>xmin est la limite gauche
	 * <li> xmax la limite droite
	 * <li> ymin la limite haute
	 * <li> ymax la limite basse 
	 * </ul>(xmin, xmax, ymin et ymax sont les champs du rectangle retourn�.)
	 * @return Un rectangle dont les champs sont d�crits plus haut.
	 */
	public RectangleDouble getBoundsGL() {
		return bounds.clone();
	}
	
	/**
	 * Donne les limites de ce composants, telles qu'elles sont
	 * d�finies par l'utilisateur de l'objet. Cela exclut les
	 * modification de limites apport�es par le composant parent,
	 * telles que celles d'un GuiLayout par exemple.
	 * @return Les limites de ce composants lorsqu'il n'est affili�
	 * � aucun autre.
	 */
	public RectangleDouble getAbsoluteBoundsGL() {
		return this.boundsDefault.clone();
	}
	
	/** D�finit directement les limites de ce composants. Cette m�thode
	 * court-circuite les autres m�thodes habituelles de d�finition des
	 * limites du composant, telles que {@link #updateBounds()} par exemple. */
	void setRealBoundsGL(RectangleDouble boundsGL) {
		this.bounds = boundsGL.clone();
	}
	
	/**
	 * D�finit la place du composant sur la fen�tre.
	 * @param boundsDisplay - le rectangle dans lequel est
	 * th�oriquement dessin� le composant, sur la fen�tre.
	 */
	public void setBoundsDisp(RectangleInt boundsDisplay) {
		RectangleDouble newBounds = boundsDispToGL(checkedBoundsDisp(boundsDisplay));
		this.boundsDefault = newBounds.clone();
		this.updateBounds();
	}
	
	public RectangleInt getBoundsDisp() {
		return boundsGLToDisp(this.bounds);
	}
	
	public void moveAtGL(double x, double y) {
		double xdif = x - this.boundsDefault.xmin;
		double ydif = y - this.boundsDefault.ymin;
		
		translateGL(xdif, ydif);
	}
	
	public void translateGL(double x, double y) {
		this.boundsDefault.translate(x, y);
		this.updateBounds();
	}
	
	public void setWidthGL(double width) {
		this.boundsDefault.xmax = this.boundsDefault.xmin + width;
		this.updateBounds();
	}
	
	public double getWidthGL() {
		return this.bounds.getWidth();
	}

	/** 
	 * D�fini la limite basse du composant comme :
	 * <blockquote>{@code ymin - height}</blockquote>
	 * (avec <code>ymin</code> comme limite haute du composant.)
	 * <p>Ce faisant, la hauteur du composant vaut maintenant {@code height}.
	 * @param height - La nouvelle hauteur du composant.
	 */
	public void setHeightGL(double height) {
		this.boundsDefault.ymax = this.boundsDefault.ymin - height;
		this.updateBounds();
	}
	
	public double getHeightGL() {
		return this.bounds.getHeight();
	}

	/**
	 * Cette m�thode est appel�e � chaque fois que le composant change
	 * de taille. Elle permet, d'une part, de mettre � jour les dimensions
	 * du composant notament en fonction de son composant parent, et d'autre
	 * part de mettre � jour tous les composants enfants, en fonction
	 * du changement de taille effectu� sur ce composant.
	 */
	void updateBounds() {
		if (this.parent == null) {
			this.bounds = this.boundsDefault.clone();
		}
		else {
			this.bounds = this.parent.getChildBounds(this);
		}
		
		for (GuiComponent child : this.children) {
			child.updateBounds();
		}
	}
	
	/**
	 * Chaque composant ayant un parent peut avoir des restrictions
	 * de dimensions, provoqu�es par le parent, notament dans le cas
	 * des {@link GuiLayout}. Dans ce cas, � chaque changement de
	 * dimensions, direct ou indirect, des composants enfants, cette
	 * m�thode est appel�e par la suite pour appliqu�s lesdites
	 * restrictions.
	 * <p>Attention, tout appel � la m�thode {@link #updateBounds()}
	 * dans la m�thode {@link #getChildBounds(GuiComponent)} est susceptible
	 * de faire planter le programme, car la premi�re m�thode appelle
	 * tr�s souvent la seconde, et il existe donc le risque d'une boucle
	 * sans fin.
	 * @param c - Le composant enfant dont les dimensions doivent �tre
	 * v�rifi�es.
	 * @return Les dimensions devant normalement �tre appliqu�es au
	 * composant enfant pass� en param�tre.
	 * @throws IllegalArgumentException Si le composant pass� en param�tre
	 * n'est pas un composant enfant du composant parent qui execute
	 * cette m�thode.
	 */
	RectangleDouble getChildBounds(GuiComponent c) {
		if (c.parent != this) {
			throw new IllegalArgumentException("c n'est pas un composant enfant de this");
		}
		
		return c.getAbsoluteBoundsGL().clone();
	}

	public Color4f getColor(){
		return this.color.clone();
	}
	
	public void setColor(Color4f color){
		this.color = color.clone();
	}
	
	public boolean isVisible() {
		if (this.parent != null && ! this.parent.isVisible()) {
			return false;
		}
		return this.visible;
	}
	
	public void setVisible(boolean flag) {
		this.visible = flag;
	}
	
	/**
	 * Permet d'ajouter un "comportement" � ce composant.
	 * <br>Les comportements permettent aux composants d'effectuer
	 * des actions programm�es, lors d'un �venement par exemple, ou
	 * de se mettre � jour automatiquement...
	 * @param b - Le comportement � ajouter.
	 * @throws NullPointerException Si le comportement vaut <code>null</code>
	 */
	public void addBehavior(Behavior b) {
		if (b == null) throw new NullPointerException();
		this.behavior.add(b);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Behavior> getAllBehavior() {
		return (ArrayList<Behavior>) this.behavior.clone();
	}
	
	public void removeAllBehavior() {
		this.behavior.clear();
	}
	
	public KeyboardListener[] getKeyboardListeners() {
		ArrayList<KeyboardListener> listeners = new ArrayList<KeyboardListener>();
		if (this instanceof KeyboardListener) listeners.add((KeyboardListener) this);
		
		for (GuiComponent child : this.children) {
			listeners.addAll(Util.createList(child.getKeyboardListeners()));
		}
		
		return listeners.toArray(new KeyboardListener[0]);
	}
	
	public MouseListener[] getMouseListeners() {
		ArrayList<MouseListener> listeners = new ArrayList<MouseListener>();
		if (this instanceof MouseListener) listeners.add((MouseListener) this);
		
		for (GuiComponent child : this.children) {
			listeners.addAll(Util.createList(child.getMouseListeners()));
		}
		
		return listeners.toArray(new MouseListener[0]);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>Attention ! Dans cette impl�mentation, les composants parents et
	 * enfants ne sont pas copi�s. La hi�rarchie doit donc �tre recr��e apr�s
	 * le clonage.
	 */
	@Override
	public GuiComponent clone() {
		GuiComponent result = null;
		try {
			result = (GuiComponent) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		result.bounds = this.bounds.clone();
		result.boundsDefault = this.boundsDefault.clone();
		result.color = this.color.clone();
		result.parent = null;
		result.children = new ArrayList<GuiComponent>();
		
		return result;
	}
	
	protected static RectangleInt boundsGLToDisp(RectangleDouble boundsGL) {
		GuiWindow window = GuiWindow.getInstance();
		return new RectangleInt(
				window.getXOnDisplay(boundsGL.xmin),
				window.getYOnDisplay(boundsGL.ymin),
				window.getXOnDisplay(boundsGL.xmax),
				window.getYOnDisplay(boundsGL.ymax));
	}
	
	protected static RectangleDouble boundsDispToGL(RectangleInt boundsDisplay) {
		GuiWindow window = GuiWindow.getInstance();
		return new RectangleDouble(
				window.getXOnGLOrtho(boundsDisplay.xmin),
				window.getXOnGLOrtho(boundsDisplay.ymin),
				window.getXOnGLOrtho(boundsDisplay.xmax),
				window.getXOnGLOrtho(boundsDisplay.ymax));
	}
	
	/** Teste si les limites de ce composant sont � peu pr�s les m�mes que
	 * celles pass�es en param�tres. */
	public boolean boundsEquals(RectangleDouble boundsOther) {
		boundsOther = checkedBoundsGL(boundsOther);
		return MathUtil.aproximatelyEquals(boundsOther.xmin, bounds.xmin, 0.01) &&
				MathUtil.aproximatelyEquals(boundsOther.ymin, bounds.ymin, 0.01) &&
				MathUtil.aproximatelyEquals(boundsOther.xmax, bounds.xmax, 0.01) &&
				MathUtil.aproximatelyEquals(boundsOther.ymax, bounds.ymax, 0.01);
	}
}
