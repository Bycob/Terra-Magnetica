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
import org.terramagnetica.opengl.engine.Painter;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class RenderClipping extends RenderGameDefaultElement {
	
	@Override
	public void render(GamePlayingDefault game, Painter painter) {
		
		//<!> NE FONCTIONNE PLUS
		RectangleDouble clip = new RectangleDouble(); //game.getDrawClipping();
		
		float onGround = 0.0001f;
		painter.setTexture(null);
		painter.setPrimitive(Painter.Primitive.QUADS);
		
		RectangleDouble inClip = clip.clone();
		RectangleDouble outClip = clip.clone();
		inClip.xmin += 1; inClip.ymin += 1; inClip.xmax -= 1; inClip.ymax -= 1;
		outClip.xmin -= 1; outClip.ymin -= 1; outClip.xmax += 1; outClip.ymax += 1;
		
		Vec2d[] inSommets = inClip.getSommets();
		Vec2d[] sommets = clip.getSommets();
		Vec2d[] outSommets = outClip.getSommets();
		
		for (int i = 0 ; i < 4 ; i++) {
			int next = i == 3 ? 0 : i + 1;
			painter.setColor(new Color4f(0, 0, 0, 0));
			painter.addVertex(inSommets[i].x, - inSommets[i].y, onGround);
			painter.addVertex(inSommets[next].x, - inSommets[next].y, onGround);
			
			painter.setColor(new Color4f(0, 0, 0, 255));
			painter.addVertex(sommets[next].x, - sommets[next].y, onGround);
			painter.addVertex(sommets[i].x, - sommets[i].y, onGround);
			
			painter.addVertex(sommets[next].x, - sommets[next].y, onGround);
			painter.addVertex(sommets[i].x, - sommets[i].y, onGround);
			painter.addVertex(outSommets[i].x, - outSommets[i].y, onGround);
			painter.addVertex(outSommets[next].x, - outSommets[next].y, onGround);
		}
		
		painter.flush();
	}
}
