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

import java.util.ArrayList;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.Entity;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.IGoal;
import org.terramagnetica.game.lvldefault.PlayerDefault;
import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Painter.Primitive;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.engine.Transform;
import org.terramagnetica.opengl.gui.GuiWindow;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.RectangleDouble;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderElementDirectionCircle extends RenderGameDefaultElement {
	
	/** Taille du cercle. */
	private double sizeX = 0.6, sizeY = 0.6;
	private double arrowSizeX = sizeX / 4, arrowSizeY = sizeY / 4;
	
	@Override
	public void render(GamePlayingDefault game, Painter painter) {
		ArrayList<IGoal> list = extractEntities(game);
		PlayerDefault player = game.getPlayer();
		
		painter.ensure2D();
		painter.setPrimitive(Primitive.QUADS);
		painter.setColor(new Color4f());//blanc
		
		RectangleDouble circleBounds = new RectangleDouble();
		RectangleDouble windowBounds = GuiWindow.getInstance().getOrtho().getBounds2D();
		final double offset = 0.2;
		circleBounds.xmin = windowBounds.xmin + offset;
		circleBounds.ymax = windowBounds.ymax + offset;
		circleBounds.xmax = circleBounds.xmin + this.sizeX;
		circleBounds.ymin = circleBounds.ymax + this.sizeY;
		
		TextureQuad circleTex = TexturesLoader.getQuad(GameRessources.PATH_DIRECTIONS_CIRCLE);
		painter.setTexture(circleTex);
		GLUtil.drawQuad2D(circleBounds, painter);
		
		painter.setTexture(TexturesLoader.get(GameRessources.PATH_DIRECTION_ARROW));
		
		for (IGoal obj : list) {
			//assert obj instanceof Entity;

			double dist = player.getDistancef((Entity) obj);
			double coef = (0.3 * dist + 15) / (dist + 10);//max : 1.5, min : 0.3
			
			RectangleDouble arrowBounds = RectangleDouble.createRectangleFromCenter(0, 0, this.arrowSizeX * coef, this.arrowSizeY * coef);
			
			Vec2d arrowLocation = circleBounds.center();
			double dir = player.getDirection((Entity) obj);
			arrowLocation.x += Math.cos(dir) * this.sizeX / 2.15;
			arrowLocation.y += Math.sin(dir) * this.sizeY / 2.15;
			
			painter.pushTransformState();
			painter.addTransform(Transform.newTranslation((float) arrowLocation.x, (float) arrowLocation.y, 0));
			painter.addTransform(Transform.newRotation((float) Math.toDegrees(dir - Math.PI / 2), new Vec3d(0, 0, 1)));
			
			painter.setColor(obj.getIndicationColor());
			GLUtil.drawQuad2D(arrowBounds, painter);
			
			painter.popTransformState();
		}
	}
	
	private ArrayList<IGoal> extractEntities(GamePlayingDefault game) {
		ArrayList<IGoal> list = new ArrayList<IGoal>();
		
		for (Entity e : game.getEntities()) {
			if (e instanceof IGoal) {
				list.add((IGoal) e);
			}
		}
		return list;
	}
}
