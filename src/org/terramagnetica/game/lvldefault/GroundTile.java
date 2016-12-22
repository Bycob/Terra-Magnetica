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

package org.terramagnetica.game.lvldefault;

import static org.terramagnetica.game.GameRessources.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import org.terramagnetica.game.lvldefault.rendering.RenderEntityTexture;
import org.terramagnetica.opengl.engine.Renderable;
import org.terramagnetica.ressources.ImagesLoader;

public class GroundTile extends LandscapeTile {
	
	private static final long serialVersionUID = 1L;
	
	public GroundTile(){
		super();
	}
	
	public GroundTile(int x, int y, boolean isCased){
		super(x, y);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof GroundTile)) {
			return false;
		}
		return true;
	}
	
	@Override
	public Image getImage(DecorType type){
		Image img = ImagesLoader.get(pathTerrainArray[type.ordinal()] + TEX_SOL);
		
		if (this.needInfosObject()) {//L'image semblera verte
			BufferedImage bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = bimg.createGraphics();
			g2d.drawImage(img, 0, 0, null);
			
			g2d.setColor(new Color(0, 255, 0, 64));
			g2d.fillRect(0, 0, bimg.getWidth(), bimg.getHeight());
			
			g2d.dispose();
			img = bimg;
		}
		
		return img;
	}
	
	@Override
	public Renderable createRender(DecorType type, RenderRegistry renders) {
		String id = "".equals(this.skin) ? pathTerrainArray[type.ordinal()] + TEX_SOL : this.skin;
		
		Renderable render = renders.getRender(id);
		if (render == null) {
			render = new RenderEntityTexture(id).setOnGround(true);
			renders.registerRender(id, render);
		}
		
		return render;
	}
	
	@Override
	public boolean isEnabled(){
		return true;
	}
}
