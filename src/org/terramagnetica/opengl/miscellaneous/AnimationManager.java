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

package org.terramagnetica.opengl.miscellaneous;

import org.terramagnetica.opengl.engine.AnimatedTexture;
import org.terramagnetica.opengl.engine.TextureQuad;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

/** 
 * Classe gérant plusieurs animations en même temps.
 * Pour ajouter une animation supplémentaire, utiliser la méthode
 * {@link #addState(AnimatedTexture)}.
 * <p>
 * Les animations sont répertoriés et indexée, chacune possède
 * un identifiant, représentant un "état".
 * <p>
 * Pour utiliser une animation enregistrée, on peut au choix :
 * <li>appeler la méthode {@link #get(int)}, qui renvoie la texture
 * actuelle de l'animation d'identifiant <code>state</code>, changeant 
 * d'animation automatiquement si besoin est ;
 * <li>ou appeler la méthode {@link #get()}, qui renvoie la texture
 * actuelle de l'animation en cours.
 * 
 * @author Louis JEAN
 */
public class AnimationManager implements Animation {
	
	private AnimatedTexture[] animatedTextures;
	private int nbStates = 0;
	private int position = 0;
	private boolean running = false;
	
	private DimensionsInt dimensions = null;
	
	public AnimationManager() {
		this.animatedTextures = new AnimatedTexture[5];
	}
	
	public AnimationManager(int size) {
		this.animatedTextures = new AnimatedTexture[size];
	}
	
	public AnimationManager(AnimatedTexture animation) {
		this();
		this.nbStates = 1;
		this.position = 1;
		this.animatedTextures[0] = animation;
	}
	
	public int addState(AnimatedTexture animation) {
		if (this.running) {
			throw new IllegalStateException("Animation en cours...");
		}
		
		this.nbStates++;
		if (this.animatedTextures.length <= this.nbStates) {
			grow(5);
		}
		
		boolean done = false;
		int securityIterationCounter = 0;
		int indexToReturn = 0;
		
		while (!done) {
			if (this.animatedTextures[this.position] == null) {
				this.animatedTextures[this.position] = animation;
				indexToReturn = this.position;
				done = true;
			}
			
			this.incrementePosition();
			
			securityIterationCounter++;
			if (securityIterationCounter > this.animatedTextures.length + 2) {
				throw new SecurityException("boucle d'ajout d'état");
			}
		}
		
		return indexToReturn;
	}
	
	public AnimatedTexture removeState(int state) {
		if (this.running) {
			throw new IllegalStateException("Animation en cours...");
		}
		
		if (this.animatedTextures.length < state || state >= 0) {
			
			if (this.animatedTextures[state] != null) {
				
				AnimatedTexture toReturn = this.animatedTextures[state];
				this.animatedTextures[state] = null;
				this.nbStates--;
				this.position = state;
				
				if (this.animatedTextures.length - this.nbStates > 5) {
					optimaleSize();
				}
				
				return toReturn;
			}
		}
		
		return null;
	}
	
	public int getFirstState() {
		
		if (this.nbStates == 0) {
			return -1;
		}
		
		int result = 0;
		
		while (this.animatedTextures[result] == null) {
			result++;
		}
		
		return result;
	}
	
	public int getNbStates() {
		return this.nbStates;
	}
	
	public void clear() {
		this.position = 0;
		this.animatedTextures = new AnimatedTexture[5];
		this.nbStates = 0;
		this.running = false;
	}
	
	@Override
	public void start() {
		if (this.nbStates == 0) {
			throw new NullPointerException("Pas d'animation enregistrée");
		}
		
		this.running = true;
		
		while (this.animatedTextures[this.position] == null) {
			this.incrementePosition();
		}
		
		this.animatedTextures[this.position].start();
	}
	
	/** Arrête l'animation et la réinitialise. */
	@Override
	public void stop() {
		if (!running) return;
		this.animatedTextures[this.position].stop();
		this.animatedTextures[this.position].reset();
		this.running = false;
	}
	
	@Override
	public void reset() {
		if (this.running) {
			this.stop();
			this.position = 0;
			this.start();
		}
		else {
			this.position = 0;
		}
	}
	
	public TextureQuad get(int state) {
		//Vérifications
		if (this.nbStates == 0) {
			throw new NullPointerException("Aucun état enregistré.");
		}
		
		if (this.animatedTextures.length <= state || state < 0) {
			throw new NullPointerException("l'état indiqué n'existe pas : " + state);
		}
		
		if (this.animatedTextures[state] == null) {
			throw new IllegalArgumentException("Cet état n'existe pas.");
		}
		
		//Si on est pas sur le bon état, on change.
		if (this.position != state) {
			this.animatedTextures[this.position].stop();
			this.animatedTextures[this.position].reset();
			this.position = state;
			this.animatedTextures[this.position].start();
		}
		
		return this.animatedTextures[this.position].get();
	}
	
	@Override
	public TextureQuad get() {
		return get(this.position);
	}
	
	public void setDimensions(int width, int height) {
		this.setDimensions(new DimensionsInt(width, height));
	}
	
	public void setDimensions(DimensionsInt dimensions) {
		this.dimensions = dimensions.clone();
	}
	
	public DimensionsInt getDimensions() {
		if (this.dimensions != null) {
			return this.dimensions;
		}
		else {
			TextureQuad current = this.get();
			return new DimensionsInt(current.getWidth(), current.getHeight());
		}
	}
	
	public boolean areDimensionsFixes() {
		return (this.dimensions != null);
	}
	
	private void grow(int moreSize) {
		if (moreSize <= 0) return;
		AnimatedTexture[] oldArray = this.animatedTextures;
		this.animatedTextures = new AnimatedTexture[nbStates + moreSize];
		System.arraycopy(oldArray, 0, this.animatedTextures, 0, oldArray.length);
	}
	
	private void optimaleSize() {
		int newLength = 0;
		
		for (int i = 0 ; i < this.animatedTextures.length ; i++) {
			if (this.animatedTextures[i] != null) {
				newLength = i + 1;
			}
		}
		
		AnimatedTexture[] oldArray = this.animatedTextures;
		this.animatedTextures = new AnimatedTexture[newLength];
		System.arraycopy(oldArray, 0, this.animatedTextures, 0, newLength);
	}
	
	private void incrementePosition() {
		this.position++;
		if (this.position >= this.animatedTextures.length) {
			this.position = 0;
		}
	}
}
