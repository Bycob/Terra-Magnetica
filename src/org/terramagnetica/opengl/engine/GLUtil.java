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

package org.terramagnetica.opengl.engine;

import org.lwjgl.opengl.GL11;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec2d;
import net.bynaryscode.util.maths.geometric.Rectangle;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class GLUtil {
	
	@Deprecated
	public static void drawNowQuad2D(double x1, double y1, double x2, double y2) {
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2d(x1, y1);
			GL11.glVertex2d(x2, y1);
			GL11.glVertex2d(x2, y2);
			GL11.glVertex2d(x1, y2);
		GL11.glEnd();
	}
	
	public static void drawQuad2D(Rectangle quad, Painter painter) {
		RectangleDouble rect = quad.asDouble();
		drawQuad2D(rect.xmin, rect.ymin, rect.xmax, rect.ymax, painter);
	}
	
	public static void drawQuad2D(double x1, double y1, double x2, double y2, Painter painter) {
		painter.setPrimitive(Painter.Primitive.QUADS);
		painter.addVertex(x1, y1);
		painter.addVertex(x2, y1);
		painter.addVertex(x2, y2);
		painter.addVertex(x1, y2);
	}
	
	@Deprecated
	public static void drawNowLine2D(double x1, double y1, double x2, double y2) {
		GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2d(x1, y1);
			GL11.glVertex2d(x2, y2);
		GL11.glEnd();
	}
	
	public static void drawLine2D(double x1, double y1, double x2, double y2, Painter painter) {
		painter.setPrimitive(Painter.Primitive.LINES);
		painter.addVertex(x1, y1);
		painter.addVertex(x2, y2);
	}
	
	public static void drawGradientPaintedRectangle(double xmin, double ymin, double xmax, double ymax,
			Color4f beginingColor, Color4f endColor, boolean vertical, Painter painter) {
		
		painter.setPrimitive(Painter.Primitive.QUADS);
		painter.setColor(beginingColor);
		painter.addVertex(xmin, ymin);
		if (!vertical) painter.setColor(endColor);
		painter.addVertex(xmax, ymin);
		if (vertical) painter.setColor(endColor);
		painter.addVertex(xmax, ymax);
		if (!vertical) painter.setColor(beginingColor);
		painter.addVertex(xmin, ymax);
	}
	
	public static void drawGradientPaintedRectangle(Rectangle bounds,
			Color4f beginingColor, Color4f endColor, boolean vertical, Painter painter) {
		
		RectangleDouble bounds2 = bounds.asDouble();
		GLUtil.drawGradientPaintedRectangle(
				bounds2.xmin, bounds2.ymin, bounds2.xmax, bounds2.ymax, beginingColor, endColor, vertical, painter);
	}
	
	public static void drawDoubleGradientPaintedRectangle(double xmin, double ymin, double xmax,
			double ymax, Color4f external, Color4f internal, boolean vertical, Painter painter) {
		
		if (vertical) {
			double ymil = (ymin + ymax) / 2;
			GLUtil.drawGradientPaintedRectangle(xmin, ymin, xmax, ymil, external, internal, vertical, painter);
			GLUtil.drawGradientPaintedRectangle(xmin, ymil, xmax, ymax, internal, external, vertical, painter);
		}
		else {
			double xmil = (xmin + xmax) / 2;
			GLUtil.drawGradientPaintedRectangle(xmin, ymin, xmil, ymax, external, internal, vertical, painter);
			GLUtil.drawGradientPaintedRectangle(xmil, ymin, xmax, ymax, internal, external, vertical, painter);
		}
	}
	
	/** Dessine un rectangle à largeur adaptative : la texture au centre est étirée en
	 * largeur mais les textures de gauche et de droite gardent leur proportions, ainsi
	 * le rectangle possède une forme différente de celle sur l'image de sa texture, mais
	 * sa texture n'est pas déformée pour autant. */
	public static void drawHorizontalTexturedRectangle(Rectangle bounds,
			TextureQuad left, TextureQuad center, TextureQuad right, Painter painter) {
		
		RectangleDouble b = bounds.asDouble();
		Vec2d c = bounds.center();
		double texHeight = Math.max(left.getHeight(), Math.max(center.getHeight(), right.getHeight()));
		double prop = b.getHeight() / texHeight;
		double xLeft = b.xmin + left.getWidth() * prop;//l'abscisse du bord droit de la texture à gauche
		double xRight = b.xmax - right.getWidth() * prop;//l'abscisse du bord gauche de la texture à droite
		
		painter.setTexture(left);
		drawQuad2D(b.xmin, c.y + left.getHeight() * prop / 2, xLeft, c.y - left.getHeight() * prop / 2, painter);
		
		painter.setTexture(center);
		drawQuad2D(xLeft, c.y + center.getHeight() * prop / 2, xRight, c.y - center.getHeight() * prop / 2, painter);

		painter.setTexture(right);
		drawQuad2D(xRight, c.y + right.getHeight() * prop / 2, b.xmax, c.y - right.getHeight() * prop / 2, painter);
	}
}
