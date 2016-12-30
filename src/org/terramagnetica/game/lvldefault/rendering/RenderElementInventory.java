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

package org.terramagnetica.game.lvldefault.rendering;

import java.util.ArrayList;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.Bonus;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.lvl2.BonusTrap;
import org.terramagnetica.opengl.engine.GLOrtho;
import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Painter.Primitive;
import org.terramagnetica.opengl.gui.GuiWindow;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class RenderElementInventory extends RenderGameDefaultElement {

	@Override
	public void render(GamePlayingDefault game, Painter painter) {

		painter.set2DConfig();
		painter.setPrimitive(Primitive.QUADS);
		
		GLOrtho dispBounds = GuiWindow.getInstance().getOrtho();
		
		double inventoryYmin = dispBounds.bottom + 1;
		double inventoryXmin = dispBounds.getBounds2D().center().x - 1.5;
		
		//TODO dessiner l'inventaire et pas juste les traps. En clair faire ça "propre" (un render pour les traps, un render pour toto, pour titi, etc)
		
		ArrayList<Bonus> playerBonus = game.getPlayer().getBonusList();
		BonusTrap trapBonus = null;
		
		for (Bonus bonus : playerBonus) {
			
			if (bonus instanceof BonusTrap) {
				trapBonus = (BonusTrap) bonus;
				break;
			}
		}
		if (trapBonus == null) return;
		
		int trapCount = trapBonus.getTrapCount();
		Color4f white = new Color4f();
		Color4f dark = new Color4f(0, 0, 0, 32);
		
		for (int i = 0 ; i < trapCount ; i++) {
			RectangleDouble caseBounds = new RectangleDouble(inventoryXmin + i * 0.5, inventoryYmin, inventoryXmin + (i + 1) * 0.5, inventoryYmin - 0.5);
			
			painter.setColor(dark);
			painter.setTexture(TexturesLoader.TEXTURE_NULL);
			GLUtil.drawQuad2D(caseBounds, painter);
			
			painter.setColor(white);
			painter.setTexture(TexturesLoader.getQuad(GameRessources.PATH_ANIM004_TRAP_OFF + GameRessources.TEX_TRAP_OFF_IMAGE));
			GLUtil.drawQuad2D(caseBounds, painter);
		}
	}
}
