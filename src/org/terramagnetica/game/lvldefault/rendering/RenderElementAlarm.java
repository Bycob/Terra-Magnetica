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

import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.LampState;
import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Painter.Primitive;
import org.terramagnetica.opengl.gui.GuiWindow;
import org.terramagnetica.opengl.miscellaneous.Timer;

import net.bynaryscode.util.Color4f;

/**
 * <b>Rendu de l'alarme</b>
 * <p>Lorsque le joueur entre dans une zone d'activation permanente
 * des lampes, une alarme retentit, en plus d'un changement de visuel
 * des lampes.
 * <p>Cette classe permet de rendre l'alarme qui se traduit par une
 * lumière rouge clignotante.
 * @author Louis JEAN
 */
public class RenderElementAlarm extends RenderGameDefaultElement {
	
	private boolean activated = false ;
	private Timer chrono = new Timer();
	
	@Override
	public void render(GamePlayingDefault game, Painter painter) {
		updateActivation(game);
		if (!this.activated) return;
		
		long time = this.chrono.getTime();
	
		painter.ensure2D();
		painter.setTexture(null);
		painter.setPrimitive(Primitive.QUADS);
		
		painter.setColor(new Color4f(255, 0, 0, 
				(int) (Math.abs( Math.cos((double) time * Math.PI / 1000)) * 64 + 31)
				));
		
		GLUtil.drawQuad2D(GuiWindow.getInstance().getOrtho().getBounds2D(), painter);
	}
	
	private void updateActivation(GamePlayingDefault game) {
		LampState lampState = game.getAspect(LampState.class);
		boolean newActivated = lampState.isLampStatePermanent();
		
		if (newActivated && ! this.activated) {
			this.chrono.restart();
		}
		else if (!newActivated && this.activated) {
			this.chrono.stop();
		}
		
		this.activated = newActivated;
	}
}
