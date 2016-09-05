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

package org.terramagnetica.openal;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioInputStream;

import javazoom.spi.vorbis.sampled.convert.DecodedVorbisAudioInputStream;
import javazoom.spi.vorbis.sampled.file.VorbisAudioFileFormat;
import javazoom.spi.vorbis.sampled.file.VorbisAudioFileReader;

public class MusicStreaming {
	
	private File file;
	
	private DecodedVorbisAudioInputStream stream = null;
	private VorbisAudioFileFormat format = null;
	
	/** Cette variable vaut <tt>true</tt> si on a atteint la fin du fichier. */
	private boolean end = false;
	
	public MusicStreaming() {
		
	}
	
	public MusicStreaming(File f) {
		this.setFile(f);
	}
	
	public void initStream() throws IOException {
		VorbisAudioFileReader reader = new VorbisAudioFileReader();
		try {
			this.format = (VorbisAudioFileFormat) reader.getAudioFileFormat(this.file);
			
			AudioInputStream input = reader.getAudioInputStream(this.file);
			this.stream = new DecodedVorbisAudioInputStream(
					this.format.getFormat(), input);
		} catch (Exception e) {
			throw new IOException("Impossible d'initialiser le flux", e);
		}
	}
	
	/** Place le nombre de samples indiqués dans le {@link ByteBuffer} passé en
	 * paramètres. Les samples sont prélevés à la position actuelle, qui est
	 * ensuite incrémentée.*/
	public void getData(ByteBuffer buf, int sampleCount) throws IOException {
		if (this.stream == null) throw new IOException("Lecture impossible, flux non initialisé.");
		
		int byteRead = 0;
		int byteLength = getDataLength(sampleCount);
		
		if (buf.capacity() < byteLength) {
			throw new UnsupportedOperationException("Le tampon est trop petit pour accueillir les données !");
		}
		
		while(byteRead < byteLength) {
			
			byte[] bufferArray = new byte[8];
			try {
				int count = this.stream.read(bufferArray);
				
				if (count == -1) { //fin du fichier
					this.end = true;
					break;
				}
				for (int i = 0 ; i < count ; i++) {
					buf.put(bufferArray[i]);
					byteRead++;
				}
			} catch (IOException e) {
				throw new IOException("Impossible de continuer la lecture", e);
			}
		}
	}
	
	public int getDataLength(int samplesCount) {
		return samplesCount * 2;//TODO Remplacer "2" par la longueur d'un sample.
	}

	public void setFile(File f) {
		closeStream();
		this.file = f;
	}
	
	public boolean isEnd() {
		return this.end;
	}
	
	public void closeStream() {
		this.end = false;
		if (this.stream == null) return;
		
		try {
			this.stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public VorbisAudioFileFormat getAudioFileFormat() {
		return this.format;
	}
}
