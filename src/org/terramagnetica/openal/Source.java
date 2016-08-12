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

package org.terramagnetica.openal;

import org.lwjgl.openal.AL10;
import org.terramagnetica.ressources.SoundManager;

/**
 * Cet objet manipule des sources openAL. La plupart des méthodes
 * utilisent directement les fonctions openAL, cet objet ne doit
 * donc être manipulé que par le thread dédié au son.
 * @see SoundManager
 */
public class Source {
	
	private int srcID;
	private int srcData;
	
	private int srcX = 0, srcY = 0, srcZ = 0;
	private boolean srcLoop = false;
	
	public Source() {
		this(0);
	}
	
	public Source(int id) {
		this.srcID = id;
	}
	
	public void setSourceID(int id) {
		AL10.alDeleteSources(this.srcID);
		this.srcID = id;
		
		setSourceData(srcData);
	}
	
	public int getSourceID() {
		return this.srcID;
	}
	
	public void setSound(Sound s) {
		setSourceData(s.getDataID());
	}
	
	public void setSourceData(int srcData) {
		this.stop();
		
		int oldData = this.srcData;
		this.srcData = srcData;
		
		if (oldData != srcData) {
			AL10.alSourcei(this.srcID, AL10.AL_BUFFER, srcData);
		}
	}
	
	public int getSourceData() {
		return this.srcData;
	}
	
	public void enableSourceLoop(boolean flag) {
		this.srcLoop = flag;
		this.stop();
		
		AL10.alSourcei(this.srcID, AL10.AL_LOOPING, flag ? AL10.AL_TRUE : AL10.AL_FALSE);
	}
	
	public boolean isSourceLoopEnabled() {
		return this.srcLoop;
	}
	
	/** Joue la source. Si la source est déjà jouée, alors la méthode la
	 * réinitialisera avant de la rejouer depuis le début. */
	public void play() {
		this.stop();
		AL10.alSourcePlay(this.srcID);
	}
	
	public void stop() {
		if (this.isPlaying()) AL10.alSourceStop(this.srcID);
	}
	
	public boolean isPlaying() {
		int srcState = AL10.alGetSourcei(this.srcID, AL10.AL_SOURCE_STATE);
		return (srcState == AL10.AL_PLAYING || srcState == AL10.AL_PAUSED);
	}
}
