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

import java.util.HashMap;
import java.util.Map;

import org.terramagnetica.opengl.engine.DisplayList;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec3d;

public class RenderLandscape {
	
	private static Map<String, DisplayList> displayListMap = new HashMap<String, DisplayList>();
	/** Détruit toutes les displaylist assignées à un paysage. A appeler
	 * impérativement lors d'une destruction du contexte openGL. */
	public static void destroyAllLists() {
		displayListMap = new HashMap<String, DisplayList>();
	}
	
	private TextureQuad tex;
	private String texID;
	
	private float height = 0f;
	private Color4f color = new Color4f();
	
	public RenderLandscape() {
		this("");
	}
	
	public RenderLandscape(String texture) {
		this.texID = texture;
		
		if ((this.tex = TexturesLoader.getQuad(this.texID)) == TexturesLoader.TEXTURE_NULL) {
			this.texID = "decor/terrain1.png.inaccessible";
			this.tex = TexturesLoader.getQuad(this.texID);
		}
	}
	
	public RenderLandscape(String texture, float height) {
		this(texture);
		this.height = height;
	}
	
	public void setHeight(float height) {
		this.height = height;
	}
	
	public void setColor(Color4f color) {
		this.color = color.clone();
	}
	
	public Color4f getColor() {
		return this.color;
	}
	
	public void renderLandscape3D(int x, int y, Painter painter) {
		
		if (displayListMap.get(this.texID) == null) {
			displayListMap.put(this.texID, new DisplayList());
		}
		DisplayList dp = displayListMap.get(this.texID);
		
		if (!dp.isCompiled()) {
			painter.startRecordList(dp);
			
			Vec3d[] vertices = new Vec3d[4];
			
			vertices[0] = new Vec3d(0, 0, 0);
			vertices[1] = new Vec3d(1, 0, 0);
			vertices[2] = new Vec3d(1, -1, 0);
			vertices[3] = new Vec3d(0, -1, 0);
			
			painter.setPrimitive(Painter.Primitive.QUADS);
			painter.setTexture(this.tex);
			painter.setColor(this.color);
			
			for (Vec3d vertex : vertices) {
				painter.addVertex(vertex);
			}
			
			painter.endRecordList();
		}
		
		painter.getLightModel().getLight0().setPosition(0, 0, 1);
		painter.drawListAt(dp, new Vec3d(x, -y, this.height));
	}
}
