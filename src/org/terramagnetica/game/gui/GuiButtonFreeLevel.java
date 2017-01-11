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

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.Level;
import org.terramagnetica.game.lvldefault.DecorType;
import org.terramagnetica.game.lvldefault.LevelDefault;
import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.gui.FontSizeManager;
import org.terramagnetica.opengl.gui.FontSizeRelativeToRectangle;
import org.terramagnetica.opengl.gui.GuiAbstractButton;
import org.terramagnetica.ressources.ExternalFilesManager;
import org.terramagnetica.ressources.RessourcesManager;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.Util;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class GuiButtonFreeLevel extends GuiAbstractButton {
	
	private TextureQuad levelImg;
	private String levelImgID = "";
	
	private int levelID;
	private String levelName;
	
	private FontSizeManager fm = new FontSizeRelativeToRectangle(this.theTextPainter);
	
	public GuiButtonFreeLevel(RectangleDouble bounds, int lvlID) {
		this.setBoundsGL(bounds);
		this.setLevel(lvlID);
	}
	
	public GuiButtonFreeLevel(RectangleDouble bounds, int lvlID, String name) {
		this.setBoundsGL(bounds);
		this.setLevel(name, lvlID);
	}
	
	public void setLevel(int lvlID) {
		this.levelID = lvlID;
		this.levelName = "Niveau " + (lvlID + 1);
		Level lvl = RessourcesManager.getLevel(this.levelID);

		if (lvl != null) {
			this.setLevel(lvl);
		}
	}
	
	public void setLevel(String name, int lvlID) {
		this.levelID = lvlID;
		this.levelName = name;
		
		Level lvl = ExternalFilesManager.getPlayerLevel(this.levelID);
		
		if (lvl != null) {
			this.setLevel(lvl);
		}
	}
	
	private void setLevel(Level lvl) {
		if (lvl instanceof LevelDefault) {
			LevelDefault lvlDef = (LevelDefault) lvl;
			DecorType type = lvlDef.getMainRoom().getDecorType();
			this.levelImgID = Util.formatDecimal(GameRessources.SPEC_IMG_TERRAIN, type.getIndex() + 1) + GameRessources.TEX_INACCESSIBLE;
			this.levelImg = TexturesLoader.getQuad(this.levelImgID);
		}
	}
	
	public void reloadTextures() {
		if (this.levelImg != null) {
			this.levelImg = TexturesLoader.getQuad(this.levelImgID);
		}
	}
	
	/** Donne l'identifiant du niveau auquel correspond ce bouton. */
	public int getLevelID() {
		return this.levelID;
	}
	
	@Override
	public void drawComponent(Painter p) {
		p.set2DConfig();
		p.setPrimitive(Painter.Primitive.QUADS);
		
		RectangleDouble bounds = this.getBoundsGL();
		final double marge = 0.03;
		
		//Fond
		p.setTexture(null);
		p.setColor(this.getState() != PRESSED ? new Color4f(0, 0, 0, 31) : GuiConstants.TEXT_COLOR_DEFAULT.clone().withAlphaf(31f / 255f));
		GLUtil.drawQuad2D(bounds, p);
		
		Color4f left, right;
		
		if (this.levelImg != null) {
			p.setTexture(this.levelImg);
			left = new Color4f(1f, 1f, 1f, 1f); right = new Color4f(255, 255, 255, 128);
			RectangleDouble texQuad = bounds.clone();
			texQuad.xmax = Math.min(bounds.xmax, texQuad.xmin + texQuad.getHeight());
			GLUtil.drawGradientPaintedRectangle(texQuad, left, right, false, p);
		}
		
		if (this.levelName != null && !this.levelName.equals("")) {
			p.setTexture(null);
			left = new Color4f(255, 100, 0, 196); right = new Color4f(255, 100, 0, 0);
			RectangleDouble nameBounds = bounds.clone();
			nameBounds.xmin = Math.min(nameBounds.xmin + marge, nameBounds.xmax);
			nameBounds.ymin -= marge; nameBounds.ymax = nameBounds.ymin - bounds.getHeight() * 1 / 3;
			GLUtil.drawGradientPaintedRectangle(nameBounds, left, right, false, p);
			
			this.theTextPainter.setColor(new Color4f(255, 234, 0));
			int fontSize = this.fm.calculFontSize(boundsGLToDisp(nameBounds), this.levelName, 14);
			this.theTextPainter.drawString2DBeginAt(this.levelName, nameBounds.xmin, nameBounds.center().y, fontSize);
		}
	}
}
