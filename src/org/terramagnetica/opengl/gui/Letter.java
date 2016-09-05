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

package org.terramagnetica.opengl.gui;

import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.ressources.TexturesLoader;

public class Letter {
	
	private TextureQuad texture;
	private float ratioWidth;
	private int resolution;
	private int glTexID;
	
	private char value;
	
	public Letter(char value) {
		this.texture = new TextureQuad();
		this.ratioWidth = 0;
		this.resolution = 0;
		this.glTexID = 0;
		this.value = value;
	}
	
	public Letter(TextureQuad tex, char value) {
		setTexture(tex);
		
		this.value = value;
	}
	
	public void setTexture(TextureQuad tex) {
		if (tex == null) tex = TexturesLoader.TEXTURE_NULL;
		
		this.texture = tex;
		
		float x1 = tex.getCoinHautGauche().x;
		float x2 = tex.getCoinBasDroit().x;
		float y1 = tex.getCoinHautGauche().y;
		float y2 = tex.getCoinBasDroit().y;
		
		float height = y1 - y2;
		float width = x1 - x2;
		
		this.ratioWidth = width / height;
		
		this.resolution = (int) height;
		this.glTexID = tex.getGLTextureID();
	}
	
	public TextureQuad getTexture(){
		return texture;
	}
	
	public int getWidth(int height) {
		return (int) (ratioWidth * height);
	}
	
	public char getChar() {
		return value;
	}
	
	public void setChar(char value) {
		this.value = value;
	}

	public int getGLTextureID() {
		return glTexID;
	}

	public void setGlTextureID(int glTexID) {
		this.glTexID = glTexID;
	}
}
