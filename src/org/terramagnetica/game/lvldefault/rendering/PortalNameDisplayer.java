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

package org.terramagnetica.game.lvldefault.rendering;

import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.gui.FontSizeManager;
import org.terramagnetica.opengl.gui.FontSizeRelativeToRectangle;
import org.terramagnetica.opengl.gui.GuiComponent;
import org.terramagnetica.opengl.miscellaneous.Animation;
import org.terramagnetica.opengl.miscellaneous.Timer;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class PortalNameDisplayer extends GuiComponent implements Animation {
	
	private String oldName = null;
	private String name;
	private Timer animTimer = new Timer();
	private boolean animation = false;
	private FontSizeManager fm = new FontSizeRelativeToRectangle(this.theTextPainter);
	
	private static final int APPEAR_TIME = 1000;
	private static final double LARGE = 1.0;
	
	public PortalNameDisplayer() {
		this("Sortie");
	}
	
	public PortalNameDisplayer(String name) {
		this.setPortalName(name);
	}
	
	public void setPortalName(String name) {
		if ("".equals(name)) {
			name = null;
		}

		if (Util.equals(this.name, name)) return;

		this.oldName = this.name;
		this.name = name;
		
		if (!Util.equals(this.oldName, this.name)) {
			this.animTimer.restart();
			if (!this.animation) this.animTimer.pause();
		}
	}
	
	@Override
	public void drawComponent() {
		if (this.name == null && (!isChanging() || this.oldName == null)) return;
		
		//Détermination des limites
		RectangleDouble bounds = this.getBoundsGL().clone();
		bounds.scaleFromCenter(0.4, 1);
		bounds.translate(0, 0.2);
		
		Painter painter = Painter.instance;
		Color4f color = new Color4f(0.25f, 0.25f, 0.25f, 0.75f);
		Color4f textColor = new Color4f(234, 255, 0);
		
		//dessin du fond
		if (isChanging()) {
			float timeRatio = this.animTimer.getTime() / (float) APPEAR_TIME;
			float alpha = 0;
			
			if (this.name != null) {
				alpha += 0.75f * timeRatio;
				textColor.setAlphaf(timeRatio);
			}
			
			if (this.oldName != null) {
				alpha += 0.75f * (1 - timeRatio);
			}
			
			color.setAlphaf(alpha);
		}
		
		painter.setColor(color);
		GLUtil.drawQuad2D(bounds, painter);
		
		//dessin de l'ancien nom
		if (this.oldName != null && isChanging()) {
			int fontSize = this.fm.calculFontSize(boundsGLToDisp(bounds), this.oldName, 18);
			float timeRatio = this.animTimer.getTime() / (float) APPEAR_TIME;
			Color4f oldTextColor = textColor.clone();
			oldTextColor.setAlphaf(1 - timeRatio);
			this.theTextPainter.setColor(oldTextColor);
			this.theTextPainter.drawCenteredString2D(this.oldName, bounds.center().x, bounds.center().y, fontSize);
		}
		
		if (this.name != null) {
			int fontSize = this.fm.calculFontSize(boundsGLToDisp(bounds), this.name, 18);
			this.theTextPainter.setColor(textColor);
			this.theTextPainter.drawCenteredString2D(this.name, bounds.center().x, bounds.center().y, fontSize);
		}
	}
	
	public boolean isChanging() {
		return this.animTimer.isRunning() && this.animTimer.getTime() < APPEAR_TIME;
	}

	@Override
	public void start() {
		this.animTimer.resume();
		this.animation = true;
	}

	@Override
	public void stop() {
		this.animTimer.pause();
		this.animation = false;
	}

	@Override
	public void reset() {
		if (this.animTimer.isRunning()) this.animTimer.restart();
		else this.animTimer.stop();
	}

	@Override
	public TextureQuad get() {
		return null;
	}
}
