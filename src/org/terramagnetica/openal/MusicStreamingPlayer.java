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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

import org.lwjgl.openal.AL10;

public class MusicStreamingPlayer {
	
	private MusicStreaming music = null;
	
	private boolean playingMusic = false;
	private boolean paused = false;
	
	private int sampleRate, format;
	private int bufferLengthInSamples;
	private int[] buffers = new int[2];
	private int source;
	
	private ByteBuffer directByteBuffer;
	
	private boolean looped = false;
	
	public MusicStreamingPlayer() {
		
	}
	
	public MusicStreamingPlayer(MusicStreaming music) {
		setMusic(music);
	}
	
	public void setMusic(MusicStreaming music) {
		if (this.playingMusic) throw new IllegalStateException("A music is playing");
		this.music = music;
	}
	
	public MusicStreaming getMusic() {
		return this.music;
	}
	
	/** Commence à jouer la musique.
	 * @throws NullPointerException Si aucune musique n'a été définie pour le lecteur. */
	public void playMusic() {
		if (this.music == null) throw new NullPointerException("no music to play !");
		
		if (this.playingMusic) {
			if (this.paused) {
				AL10.alSourcePlay(this.source);
				this.paused = false;
			}
			return;
		}
		
		try {
			initAndStartPlaying();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Impossible de continuer la lecture, le flux va être fermé.");
			return;
		}
		
		this.playingMusic = true;
	}
	
	private void initAndStartPlaying() throws IOException {
		
		this.music.initStream();
		
		@SuppressWarnings("unchecked")
		Map<? extends Object, ? extends Object> propertyMap = this.music.getAudioFileFormat().properties();
		if (!propertyMap.containsKey("ogg.frequency.hz")) {
			throw new UnsupportedOperationException("Impossible de lire le paramètre : sampleRate");
		}
		if (!propertyMap.containsKey("ogg.channels")) {
			throw new UnsupportedOperationException("Impossible de lire le paramètre : channels");
		}
		
		this.sampleRate = (Integer) propertyMap.get("ogg.frequency.hz");
		this.format = (Integer) propertyMap.get("ogg.channels") == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
		
		
		//Tampons de 1 sec
		this.bufferLengthInSamples = this.sampleRate;
		this.directByteBuffer = ByteBuffer.allocateDirect(this.music.getDataLength(this.bufferLengthInSamples));

		this.source = AL10.alGenSources();
		
		for (int i = 0 ; i < 2 ; i++) {
			this.buffers[i] = AL10.alGenBuffers();

			try {
				readData(this.buffers[i], this.bufferLengthInSamples);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Impossible de lire la musique.");
				return;
			}

			AL10.alSourceQueueBuffers(this.source, this.buffers[i]);
		}
		
		AL10.alSourcePlay(source);
	}
	
	/** Cette méthode doit être appelée régulièrement lorsqu'une musique est
	 * jouée en streaming, afin de re-remplir le tampon avec les données de
	 * la musique. */
	public void updateStreaming() {
		if (!this.playingMusic) throw new IllegalStateException("Pas de musique jouée.");
		
		
		//On vérifie que la musique n'est pas terminée.
		int status = AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE);
		if (status == AL10.AL_STOPPED) {
			//Si on est à la fin de la musique on l'arrête, ou on la fait reprendre du début.
			if (this.music.isEnd()) {
				this.stopMusic();
				if (this.looped) {
					this.playMusic();
				}
			}
			//Lors d'une interruption de stream on relance juste la musique.
			else {
				AL10.alSourcePlay(source);
			}
			return;
		}
		
		//On re-remplit les tampons lus par la suite des données de la musique.
		int processedCount = AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED);
		
		for (int i = 0 ; i < processedCount ; i++) {
			int processedBuf = AL10.alSourceUnqueueBuffers(source);
			try {
				readData(processedBuf, this.bufferLengthInSamples);
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Impossible de lire la musique.");
				return;
			}
			AL10.alSourceQueueBuffers(this.source, processedBuf);
		}
	}
	
	private void readData(int alBufID, int samplesRead) throws IOException {
		
		this.music.getData(this.directByteBuffer, samplesRead);
		
		this.directByteBuffer.flip();
		AL10.alBufferData(alBufID, this.format, this.directByteBuffer, this.sampleRate);
	}
	
	public void pauseMusic() {
		if (this.playingMusic && !this.paused) {
			AL10.alSourcePause(this.source);
			this.paused = true;
		}
	}
	
	/** Arrête de jouer la musique. */
	public void stopMusic() {
		if (!this.playingMusic) return;
		
		this.playingMusic = false;
		
		AL10.alSourceStop(this.source);
		
		int bufCount = AL10.alGetSourcei(this.source, AL10.AL_BUFFERS_QUEUED);
		for (int i = 0 ; i < bufCount ; i++) {
			int unqueued = AL10.alSourceUnqueueBuffers(this.source);
			AL10.alDeleteBuffers(unqueued);
		}
		
		AL10.alDeleteSources(this.source);
		
		this.music.closeStream();
	}
	
	/** Retourne {@code true} si la musique est en train d'être jouée. */
	public boolean isSourcePlaying() {
		return this.playingMusic;
	}
	
	public void setLooped(boolean looped) {
		this.looped = looped;
	}
}
