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

import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Painter.Primitive;
import org.terramagnetica.opengl.miscellaneous.Timer;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public abstract class GuiDialog extends GuiComponent {
	
	private static final int ANIMATION_TIME = 200;
	
	private boolean appearing = true;
	private boolean destroying = false;
	
	private Timer animationChrono = new Timer();
	
	public GuiDialog() {
		
	}
	
	@Override
	public void draw(Painter painter) {
		if ((this.appearing || this.destroying) && !this.animationChrono.isTiming()) {
			this.animationChrono.restart();
		}
		
		if (this.animationChrono.getTime() >= ANIMATION_TIME) {
			if (this.appearing) {
				this.appearing = false;
			}
			else if (this.destroying) {
				this.shouldPerformClose = true;
			}
			
			this.animationChrono.pause();
		}
		
		if (!this.appearing && !this.destroying) {
			super.draw(painter);
		}
		else {
			this.drawComponent(painter);
		}
	}
	
	@Override
	public void drawComponent(Painter painter) {
		drawDecorations(painter);
	}
	
	protected void drawDecorations(Painter painter) {
		//Initialisation
		GuiWindow window = GuiWindow.getInstance();
		
		painter.ensure2D();
		painter.setPrimitive(Primitive.QUADS);
		painter.setTexture(null);
		
		//Mise à jour des dimensions de la boite de dialogue
		setBoundsGL(Util.margeRectangleDouble(window.getOrtho().getBounds2D().clone(), 1));
		
		//Petit écran sombre pour faire ressortir la boite de dialogue oklm
		painter.setColor(new Color4f(0, 0, 0, 0.8f));
		GLUtil.drawQuad2D(window.getOrtho().getBounds2D(), painter);
		
		//Définition des dimensions de la boite de dialogue : si celle-ci est en cours d'apparition ou de 
		//disparition, elle grandit (apparition) ou rétrécit (disparition)
		RectangleDouble boundsGL = this.getBoundsGL().clone();
		float scale = 1;
		if (this.appearing) {
			scale = this.animationChrono.getTime() / (float) ANIMATION_TIME;
		}
		else if (this.destroying) {
			scale = 1 - (this.animationChrono.getTime() / (float) ANIMATION_TIME);
		}
		boundsGL.scaleFromCenter(scale, scale);
		
		
		final double marge = 0.03;
		RectangleDouble boundsGLWithoutBorder = Util.margeRectangleDouble(boundsGL.clone(), marge);
		
		//dessin des bordures
		painter.setColor(new Color4f(0, 0, 0, 0.8f));
		
		//bordure haute
		GLUtil.drawQuad2D(boundsGL.xmin, boundsGL.ymin, boundsGL.xmax, boundsGLWithoutBorder.ymin, painter);
		//bordure basse
		GLUtil.drawQuad2D(boundsGL.xmin, boundsGLWithoutBorder.ymax, boundsGL.xmax, boundsGL.ymax, painter);
		//bordure gauche
		GLUtil.drawQuad2D(boundsGL.xmin, boundsGLWithoutBorder.ymin, boundsGLWithoutBorder.xmin, boundsGLWithoutBorder.ymax, painter);
		//bordure droite
		GLUtil.drawQuad2D(boundsGLWithoutBorder.xmax, boundsGLWithoutBorder.ymin, boundsGL.xmax, boundsGLWithoutBorder.ymax, painter);
		
		//dessin du fond
		painter.setColor(new Color4f(0, 0, 0, 0.5f));
		GLUtil.drawQuad2D(boundsGLWithoutBorder, painter);
	}
	
	private boolean shouldPerformClose = false;
	
	/**
	 * Indique à la boite de dialogue qu'elle doit se fermer.
	 */
	public void close() {
		this.destroying = true;
	}
	
	/**
	 * Indique à la fenêtre, lorsque celle-ci appelle cette méthode,
	 * si la boite de dialogue doit être fermée ou non.
	 * @return {@code true} si la boite de dialogue demande à être
	 * fermée, {@code false} sinon.
	 */
	public boolean shouldPerformClose() {
		return this.shouldPerformClose;
	}
}
