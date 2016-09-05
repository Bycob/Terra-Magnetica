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

package org.terramagnetica.opengl.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.terramagnetica.opengl.miscellaneous.Animation;
import org.terramagnetica.opengl.miscellaneous.Timer;

import net.bynaryscode.util.maths.geometric.Vec2d;

public class AnimatedTexture implements Animation, Texture, Cloneable {
	
	private static final int DEFAULT_FPS = 24;
	
	//ANIMATION
	/** Le chronomètre qui permet de controler le déroulement de l'animation. */
	private Timer chrono = new Timer();
	private boolean running = false;
	private boolean paused = false;
	/** La durée pendant laquelle une image reste à l'écran en ms. Aussi
	 * égal à l'inverse du fps. */
	private int speed;
	
	//TEXTURE OPENGL
	private List<TextureQuad> textures;
	private boolean isSameImage = true;
	private int texID = 0;

	public AnimatedTexture() {
		this(DEFAULT_FPS);
	}
	
	public AnimatedTexture(int fps) {
		this.setFPS(fps);
		this.textures = new ArrayList<TextureQuad>();
	}
	
	public AnimatedTexture(int fps, Collection<TextureQuad> textures) {
		this.textures = new ArrayList<TextureQuad>(textures);
		this.setFPS(fps);
	}
	
	public AnimatedTexture(int fps, TextureQuad[] textures) {
		this.textures = new ArrayList<TextureQuad>();
		
		for (TextureQuad tex : textures) {
			this.textures.add(tex);
		}
		this.setFPS(fps);
	}
	
	public AnimatedTexture(Collection<TextureQuad> textures) {
		this(DEFAULT_FPS, textures);
	}
	
	public AnimatedTexture(TextureQuad[] textures) {
		this(DEFAULT_FPS, textures);
	}
	
	public void add(TextureQuad texture) {
		if (texture == null) throw new NullPointerException();
		this.textures.add(texture);
		checkAddedTextureID(texture);
	}
	
	public void add(int index, TextureQuad texture) {
		this.textures.add(index, texture);
		checkAddedTextureID(texture);
	}
	
	public void remove(int index) {
		this.textures.remove(index);
		checkTextureID();
	}
	
	public void setFPS(int fps) {
		if (this.running) throw new IllegalStateException();
		if (fps < 1) throw new IllegalArgumentException();
		this.speed = 1000 / fps;
	}
	
	public AnimatedTexture withFPS(int fps) {
		this.setFPS(fps);
		return this;
	}
	
	public int getFPS() {
		return 1000 / this.speed;
	}
	
	/** Donne la durée d'un tour complet de l'animation en ms. */
	public int getDuration() {
		return this.speed * this.textures.size();
	}
	
	
	//ANIMATION
	
	@Override
	public void start() {
		if (this.textures.size() == 0) throw new NullPointerException("Pas de textures enregistrée");
		
		if (this.paused) {
			this.chrono.resume();
			this.paused = false;
		}
		else { 
			this.chrono.start();
		}
		this.running = true;
	}
	
	@Override
	public void stop() {
		this.chrono.pause();
		this.running = false;
		this.paused = true;
	}
	
	@Override
	public void reset() {
		if (this.running) {
			this.chrono.restart();
		}
		else {
			this.chrono.stop();
		}
		
		this.paused = false;
	}
	
	@Override
	public TextureQuad get() {
		if (!this.running) {
			if (this.textures.size() != 0) {
				return this.textures.get(0);
			}
			else {
				throw new NullPointerException("pas de texture enregistrée.");
			}
		}
		
		int index = (int) (this.chrono.getTime() / this.speed) % this.textures.size();
		
		return this.textures.get(index);
	}

	
	/** 
	 * Vérifie si on peut attribuer un ID de texture à l'animation, autrement dit,
	 * si toutes les textures de l'animation possèdent le même identifiant.
	 * Puis, met à jour les variables {@code texID} et {@code isSameImage}
	 */
	private void checkTextureID() {
		int texID = -1;
		
		for (TextureQuad tex : this.textures) {
			if (texID == -1) {
				texID = tex.getGLTextureID();
			}
			else if (texID != tex.getGLTextureID()) {
				texID = -1;
				break;
			}
		}
		
		this.texID = texID;
		this.isSameImage = (texID != -1);
	}

	/** vérifie si pour la texture entrante passée en paramètre, il faut
	 * changer l'identifiant de texture de l'animation ou non. */
	private void checkAddedTextureID(Texture tex) {
		if (this.textures.size() == 1) {
			this.isSameImage = true;
			this.texID = tex.getGLTextureID();
		}
		else {
			if (tex.getGLTextureID() != this.texID) {
				this.isSameImage = false;
				this.texID = 0;
			}
		}
	}
	
	
	
	//TEXTURAGE OPENGL
	
	/** @return {@code true} si l'animation est toute entière sur une seule
	 * texture openGL, {@code false} sinon. */
	public boolean hasUniqueGLID() {
		return this.isSameImage;
	}
	
	@Override
	public int getGLTextureID() {
		return get().getGLTextureID();
	}

	@Override
	public AnimatedTexture withTextureID(int texID) {
		this.setTextureID(texID);
		return this;
	}

	@Override
	public void setTextureID(int texID) {
		this.texID = texID;
		this.isSameImage = true;
		
		for (Texture tex : this.textures) {
			tex.setTextureID(texID);
		}
	}

	@Override
	public Vec2d[] getSTSommets() {
		return get().getSTSommets();
	}
	
	@Override
	public int getNbSommets() {
		return 4;
	}
	
	@Override
	public AnimatedTexture clone() {
		AnimatedTexture result = null;
		try {
			result = (AnimatedTexture) super.clone();
		} catch (CloneNotSupportedException e) {
			//jamais vu, jamais connu
		}
		
		result.textures = new ArrayList<TextureQuad>();
		for (TextureQuad tex : this.textures) {
			result.textures.add(tex.clone());
		}
		
		result.chrono = this.chrono.clone();
		return result;
	}
}
