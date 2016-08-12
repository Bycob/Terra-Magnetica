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

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;

public class ThreadMusicStreaming extends Thread {
	
	private static int threadRunning = 0;
	
	private MusicStreaming music = null;
	
	private boolean running = false;
	
	public ThreadMusicStreaming() {
		
	}
	
	@Override
	public void run() {
		if (this.music == null) return;
		
		this.running = true;
		
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.err.println("\n<!> SONS NON INITIALISES");
			return;
		}
		
		try {
			this.music.initStream();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		
		@SuppressWarnings("unchecked")
		Map<? extends Object, ? extends Object> propertyMap = this.music.getAudioFileFormat().properties();
		if (!propertyMap.containsKey("ogg.frequency.hz")) {
			throw new UnsupportedOperationException("Impossible de lire le paramètre : sampleRate");
		}
		if (!propertyMap.containsKey("ogg.channels")) {
			throw new UnsupportedOperationException("Impossible de lire le paramètre : channels");
		}
		
		int sampleRate = (Integer) propertyMap.get("ogg.frequency.hz");
		int format = (Integer) propertyMap.get("ogg.channels") == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;
		
		
		//Tampon de 1 sec
		final int BUFFER_LENGTH_IN_SAMPLES = sampleRate;
		
		int[] buffers = new int[2];
		int source = 0;
		ByteBuffer readingBuffer = ByteBuffer.allocateDirect(this.music.getDataLength(BUFFER_LENGTH_IN_SAMPLES));
		
		buffers[0] = AL10.alGenBuffers();
		buffers[1] = AL10.alGenBuffers();
		source = AL10.alGenSources();
		
		try {
			readData(readingBuffer, buffers[0], BUFFER_LENGTH_IN_SAMPLES, sampleRate, format);
			readData(readingBuffer, buffers[1], BUFFER_LENGTH_IN_SAMPLES, sampleRate, format);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Impossible de lire la musique.");
			return;
		}
		
		AL10.alSourceQueueBuffers(source, buffers[0]);
		AL10.alSourceQueueBuffers(source, buffers[1]);
		
		AL10.alSourcePlay(source);
		
		
		int status;
		do {
			int processedCount = AL10.alGetSourcei(source, AL10.AL_BUFFERS_PROCESSED);
			
			for (int i = 0 ; i < processedCount ; i++) {
				int processedBuf = AL10.alSourceUnqueueBuffers(source);
				try {
					readData(readingBuffer, processedBuf, BUFFER_LENGTH_IN_SAMPLES, sampleRate, format);
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("Impossible de lire la musique.");
					return;
				}
				AL10.alSourceQueueBuffers(source, processedBuf);
			}
			
			status = AL10.alGetSourcei(source, AL10.AL_SOURCE_STATE);
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (status == AL10.AL_PLAYING);
		
		
		this.music.closeStream();
		
		AL.destroy();
		
		threadRunning --;
	}
	
	private void readData(ByteBuffer bufNative, int alBufID, int samplesRead,
			int sampleRate, int format) throws IOException {
		
		this.music.getData(bufNative, samplesRead);
		
		bufNative.flip();
		AL10.alBufferData(alBufID, format, bufNative, sampleRate);
	}
	
	private void closeALBuffer() {
		
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public void setMusic(MusicStreaming music) {
		if (this.running) throw new IllegalStateException("En cours de lecture...");
		
		this.music = music;
	}
	
	public void startMusic() {
		this.start();
	}
	
	@Override
	public void start() {
		this.setName("Thread music #" + threadRunning);
		threadRunning++;
		super.start();
	}
	
	public void pauseMusic() {
		
	}
	
	public void stopMusic() {
		
	}
}
