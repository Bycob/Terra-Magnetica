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

import org.terramagnetica.game.Page;
import org.terramagnetica.game.Page.Legende;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Painter.Primitive;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.gui.GuiContainer;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class GuiPage extends GuiContainer {
	
	private Page displayPage;
	private RectangleDouble oldBounds;
	
	private int fontSize = 12;
	private double margin = 0.1;
	private Color4f textColor = new Color4f(255, 234, 0);
	
	public GuiPage() {
		this(null);
	}
	
	public GuiPage(Page contents) {
		this.displayPage = contents;
	}
	
	@Override
	public void drawComponent(Painter p) {
		checkBounds();
		
		p.set2DConfig();
		p.flush();
		RectangleDouble bounds = getBoundsGL();
		RectangleDouble screenBounds = this.theWindow.getOrtho().getBounds2D();
		
		p.setPrimitive(Primitive.QUADS);
		p.setColor(new Color4f());
		
		//TODO ça rame ! (framebuffer ?)
		
		if (this.displayPage != null) {
			Object[] contents = this.displayPage.getContents();
			double sizeGL = 0;
			RectangleDouble withOffsets = bounds.clone();
			//Utiliser uniquement xmin et xmax pour les marges, les autres valeurs sont susceptibles d'être modifiées.
			final double offset = Math.min(margin, bounds.getWidth() * 0.05);
			withOffsets.xmin += offset;
			withOffsets.xmax -= offset;
			boolean added = true;
			this.theTextPainter.setLineSpace(0.07);
			
			for (Object content : contents) {
				
				if (content == null) continue;
				
				if (added) sizeGL += 2 * margin;
				added = true;
				
				if (content instanceof String) {
					withOffsets.ymin = bounds.ymin - sizeGL;
					withOffsets.ymax = withOffsets.ymin;
					this.theTextPainter.setColor(textColor);
					int nbl = this.theTextPainter.getLineCount(content.toString(), withOffsets, this.fontSize);
					sizeGL += (this.theTextPainter.heightOnGL(this.fontSize) + this.theTextPainter.getLineSpace()) * nbl;
					
					// Optimisation : on ne dessine pas en dessus ou en dessous de l'écran.
					if (withOffsets.ymin < screenBounds.ymax || bounds.ymin - sizeGL > screenBounds.ymin) {
						continue;
					}
					
					// Dessin
					this.theTextPainter.drawPlainText2D(content.toString(), withOffsets, this.fontSize);
				}
				else if (content instanceof TextureQuad) {
					TextureQuad tex = (TextureQuad) content;
					RectangleDouble texBounds = tex.getSTBounds();
					double texWidth = Math.min(1, withOffsets.getWidth() - 2 * offset);
					double texHeight = texBounds.getHeight() / texBounds.getWidth() * texWidth;
					double texX = withOffsets.center().x - texWidth / 2d;
					double texY = bounds.ymin - sizeGL;
					sizeGL += texHeight;

					// Optimisation : on ne dessine pas en dessus ou en dessous de l'écran.
					if (texY < screenBounds.ymax || texY - texHeight > screenBounds.ymin) {
						continue;
					}
					
					// Dessin
					p.setColor(new Color4f(255, 255, 255, 255));
					tex.drawQuad2D(texX, texY, texX + texWidth, texY - texHeight, true, p);
				}
				else if (content instanceof Legende) {
					Legende leg = (Legende) content;
					final int fs = 8;
					double x = bounds.center().x;
					double y = bounds.ymin - sizeGL - this.theTextPainter.heightOnGL(fs) / 2d;
					this.theTextPainter.setColor(textColor);
					this.theTextPainter.drawCenteredString2D(leg.légende, x, y, fs);
					sizeGL += this.theTextPainter.heightOnGL(fs);
				}
				else {
					added = false;
				}
			}
			
			this.setHeightGL(sizeGL);
		}
	}
	
	public void setPage(Page page) {
		this.displayPage = page;
	}
	
	private void checkBounds() {
		if (this.oldBounds == null) {
			this.oldBounds = getBoundsGL();
		}
		else {
			if (!boundsEquals(this.oldBounds)) {
				this.oldBounds = getBoundsGL();
			}
		}
	}
}
