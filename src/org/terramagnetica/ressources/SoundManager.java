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

package org.terramagnetica.ressources;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;
import org.terramagnetica.game.GameRessources;
import org.terramagnetica.openal.MusicStreaming;
import org.terramagnetica.openal.MusicStreamingPlayer;
import org.terramagnetica.openal.Sound;
import org.terramagnetica.openal.Source;
import org.terramagnetica.ressources.SoundSet.SoundSetEntry;

public final class SoundManager implements Runnable {
	
	
	private static final SoundManager instance = new SoundManager();
	private static boolean running = false;
	
	private static final Object LOCK = new Object();
	
	
	public static final String MUSIC_PATH = "assets/music/";
	
	private static HashMap<String, Sound> soundsMap = new HashMap<String, Sound>();
	
	private static int soundSourcesIndex;
	private static final int SOUND_SOURCES_COUNT = 8;
	private static HashMap<Integer, Source> soundSources = new HashMap<Integer, Source>(SOUND_SOURCES_COUNT);
	
	private static final int LOOP_SOUND_SOURCES_COUNT = 8;
	private static HashMap<Integer, Source> loopSoundSources = new HashMap<Integer, Source>(LOOP_SOUND_SOURCES_COUNT);
	
	private static MusicStreaming currentMusic = null;
	
	
	//QUEUES
	private static ArrayList<Sound> playSoundQueue = new ArrayList<Sound>();
	private static ArrayList<LoopSound> loopSoundQueue = new ArrayList<LoopSound>();
	private static ArrayList<String> musicEventQueue = new ArrayList<String>();
	
	
	//PLAYSTATE
	public static final int PLAYSTATE_PLAY = 0,
			PLAYSTATE_PAUSE = 1,
			PLAYSTATE_STOP = 2;
	
	private static int musicPlayState = PLAYSTATE_STOP;
	
	
	private SoundManager() {}
	
	@Override
	public void run() {
		if (running) return;
		
		
		
		running = true;
		//INITIALISATION 
		try {
			AL.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.err.println("\n<!> SONS NON INITIALISES");
			return;
		}
		
		//sons simples
		for (int i = 0 ; i < SOUND_SOURCES_COUNT ; i++) {
			int srcID = AL10.alGenSources();
			soundSources.put(i, new Source(srcID));
		}
		
		//sons en boucle
		for (int i = 0 ; i < LOOP_SOUND_SOURCES_COUNT ; i++) {
			int srcID = AL10.alGenSources();
			Source src = new Source(srcID);
			src.enableSourceLoop(true);
			loopSoundSources.put(i, src);
		}
		
		
		
		while (running) {
			
			//SONS SIMPLES
			synchronized (LOCK) {
				for (Sound s : playSoundQueue) {
					Source src = getSoundSource();
					src.setSound(s);
					src.play();
				}
				
				playSoundQueue.clear();
			}
			
			
			
			//SONS EN BOUCLE
			synchronized (LOCK) {
				for (LoopSound ls : loopSoundQueue) {
					if (ls.play) {
						if (!ls.enableDupli && isLoopSoundPlaying(ls.sound)) continue;
						
						if (ls.sound != null) {
							Source src = getLoopSoundSource();
							src.setSound(ls.sound);
							src.play();
						}
					}
					else {
						Source toStop = null;
						for (int i = 0 ; i < LOOP_SOUND_SOURCES_COUNT ; i++) {
							toStop = loopSoundSources.get(i);
							if (toStop.isPlaying() && toStop.getSourceData() == ls.sound.getDataID()) {
								toStop.stop();
							}
						}
					}
				}
				
				loopSoundQueue.clear();
			}
			
			
			
			//MUSIQUE
			synchronized (LOCK) {
				for (String evt : musicEventQueue) {
					if (START_EVENT.equals(evt)) {
						if (thePlayer.getMusic() != null) thePlayer.playMusic();
					}
					else if (STOP_EVENT.equals(evt)) {
						if (thePlayer.isSourcePlaying()) thePlayer.stopMusic();
					}
				}
				
				//updates de la lecture en streaming de la musique.
				if (thePlayer.isSourcePlaying()) {
					thePlayer.updateStreaming();
				}
				
				musicEventQueue.clear();
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
		
		AL.destroy();
	}
	
	
	
	
	public static void initialize() {
		Thread t = new Thread(instance, "SoundThread");
		t.start();
	}
	
	public static void destroy() {
		running = false;
	}
	
	
	/*------------------------ CHARGEMENT DES SONS ---------------------*/
	
	
	
	
	
	public static void loadAudio() {
		if (!running) {
			System.err.println("contexte non initialisé");
			return;
		}
		
		loadSoundSet(GameRessources.tmSoundSet);
	}
	
	public static void loadSoundSet(SoundSet set) {
		for (SoundSetEntry e : set.getEntryList()) {
			Sound s = new Sound(loadSound(e.getPath()));
			soundsMap.put(e.getPath(), s);
		}
	}
	
	/** charge un son contenu dans le jar. Si le son n'est pas chargé,
	 * retourne 0. */
	private static int loadSound(String path) {
		try {
			return loadSound(RessourcesManager.getURL(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	private static int loadSound(URL path) {
		if (path == null) {
			System.err.println("1 son non chargé");
			return 0;
		}
		if (!running) {
			throw new IllegalStateException("Impossible de charger des sons si le contexte n'est pas initialisé");
		}
		
		int ID = 0;
		BufferedInputStream bis = null;
		WaveData dat = null;
		
		try {
			bis = new BufferedInputStream(path.openStream());
			dat = WaveData.create(bis);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (dat != null) {
			ID = AL10.alGenBuffers();
			AL10.alBufferData(ID, dat.format, dat.data, dat.samplerate);
		}
		
		return ID;
	}
	
	public static MusicStreaming loadMusic(String path) {
		MusicStreaming music = new MusicStreaming();
		music.setFile(new File(MUSIC_PATH + path));
		return music;
	}
	
	
	
	
	
	/*---------------------------- SONS SIMPLES ------------------------*/
	
	
	
	
	
	/** Joue le son portant l'identifiant indiqué. Le son sera joué une
	 * seule fois en entier, sauf indication contraire.
	 * <p>Attention si toutes les sources sont occupées, alors le son démarré
	 * en premier sera arrêté. */
	public static void playSound(String ID) {
		Sound played = soundsMap.get(ID);
		if (played == null) {
			System.err.println("son inconnu : " + ID);
			return;
		}
		
		synchronized (LOCK) {
			playSoundQueue.add(played);
		}
	}
	
	private static Source getSoundSource() {
		Source src = null;
		int index = soundSourcesIndex;
		
		do {
			if (!soundSources.get(index).isPlaying()) {
				src = soundSources.get(index);
			}
			
			index++;
			if (index >= SOUND_SOURCES_COUNT) index = 0;
			
		} while (src == null && soundSourcesIndex != index);
		
		if (src == null) {
			src = soundSources.get(index);
			src.stop();
		}
		
		soundSourcesIndex = index++;
		if (soundSourcesIndex >= SOUND_SOURCES_COUNT) soundSourcesIndex = 0;
		
		return src;
	}
	
	
	
	
	
	/*-------------------------- SONS EN BOUCLE ------------------------*/
	
	
	
	
	
	private static class LoopSound {
		public Sound sound;
		public boolean enableDupli;
		public boolean play;
		public LoopSound(Sound ls, boolean enableDupli, boolean start) {
			this.sound = ls;
			this.enableDupli = enableDupli;
			this.play = start;
		}
	}
	
	/**
	 * Joue un son en boucle. Le son sera joué du début à la fin,
	 * puis rejoué en repartant au début, indéfiniment jusqu'à
	 * indication d'arrêt.
	 * @param id - L'identifiant du son.
	 * @param enableDupli - Si le même son est déjà joué en boucle,
	 * ce paramètre peut être mis à {@code true} pour autoriser,
	 * ou à {@code false} pour interdire de créer une nouvelle
	 * boucle de ce même son par dessus celle existante.
	 */
	public static void playLoopSound(String id, boolean enableDupli) {
		Sound played = soundsMap.get(id);
		if (played == null) {
			System.err.println("son inconnu : " + id);
			return;
		}
		
		synchronized (LOCK) {
			loopSoundQueue.add(new LoopSound(played, enableDupli, true));
		}
	}
	
	public static void stopLoopSound(String id) {
		Sound played = soundsMap.get(id);
		if (played == null) {
			System.err.println("son inconnu : " + id);
			return;
		}
		
		synchronized (LOCK) {
			loopSoundQueue.add(new LoopSound(played, false, false));
		}
	}
	
	private static Source getLoopSoundSource() {
		Source src = null;
		
		for (int i = 0 ; i < LOOP_SOUND_SOURCES_COUNT ; i++) {
			src = loopSoundSources.get(i);
			if (!src.isPlaying()) break;
		}
		
		return src;
	}
	
	private static boolean isLoopSoundPlaying(Sound ls) {
		if (ls == null) return false;
		
		for (int i = 0 ; i < LOOP_SOUND_SOURCES_COUNT ; i++) {
			Source src = loopSoundSources.get(i);
			if (src.isPlaying() && ls != null && src.getSourceData() == ls.getDataID()) {
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	/*-------------------------- MUSIQUES ------------------------------*/
	
	public static final String START_EVENT = "START",
			PAUSE_EVENT = "PAUSE",
			STOP_EVENT = "STOP";
	
	private static final MusicStreamingPlayer thePlayer = new MusicStreamingPlayer();
	
	
	public static void setMusic(MusicStreaming music) {
		if (thePlayer.isSourcePlaying()) return;//TODO améliorer le synchronisme.
		synchronized (LOCK) {
			thePlayer.setMusic(music);
		}
	}
	
	public static void setMusicLooped(boolean looped) {
		synchronized (LOCK) {
			thePlayer.setLooped(looped);
		}
	}
	
	public static void playMusic() {
		synchronized (LOCK) {
			musicEventQueue.add(START_EVENT);
		}
	}
	
	public static void pauseMusic() {
		synchronized (LOCK) {
			musicEventQueue.add(PAUSE_EVENT);
		}
	}
	
	public static void stopMusic() {
		synchronized (LOCK) {
			musicEventQueue.add(STOP_EVENT);
		}
	}
}
