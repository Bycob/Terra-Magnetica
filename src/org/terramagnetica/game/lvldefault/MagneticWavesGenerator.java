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

import java.awt.Image;
import java.util.ArrayList;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.rendering.RenderCompound;
import org.terramagnetica.game.lvldefault.rendering.RenderEntity;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityDefault;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Boussole;
import net.bynaryscode.util.maths.geometric.DimensionsInt;

public class MagneticWavesGenerator extends CaseEntity implements IDirectionnalEntity {

	private static final long serialVersionUID = 1L;
	
	public static final float DEFAULT_FREQUENCY = 4;
	public static final float DEFAULT_SPEED = 6;
	public static final float DEFAULT_DISTANCE = 9;
	
	private PropertyDirectionnalEntity direction = new PropertyDirectionnalEntity((float) (Math.PI * 3/2));
	/** Nombre de vagues par seconde. */
	private float freq = 4;
	/** Distance sur laquelle se propagent les vagues en cases. */
	private float distance = 9;
	/** Vitesse des vagues en cases par seconde. */
	private float speed = 6;
	
	private long lastWaveDate = -1;
	private ArrayList<MagneticWave> waves = new ArrayList<MagneticWave>();
	
	/**
	 * Constructeur par défaut du générateur d'ondes. La valeur
	 * de la direction par défaut est 270° soit plein Sud.
	 */
	public MagneticWavesGenerator() {
		super();
	}
	
	
	@Override
	public PropertyDirectionnalEntity getDirectionObject() {
		return this.direction;
	}
	
	public float getFreq() {
		return freq;
	}
	
	public void setFreq(float freq) {
		if (freq <= 0) throw new IllegalArgumentException("\"freq\" doit être strictement positif !");
		this.freq = freq;
	}
	
	public float getDistance() {
		return distance;
	}
	
	public void setDistance(float distance) {
		if (distance < 0) throw new IllegalArgumentException("\"distance\" doit être positif !");
		this.distance = distance;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		if (speed <= 0) throw new IllegalArgumentException("\"speed\" doit être strictement positif !");
		this.speed = speed;
	}
	
	
	/**
	 * Donne l'identifiant de la texture de cet objet, en fonction
	 * de la direction d'émission des ondes.
	 * @return
	 */
	private String getTextureID() {
		Boussole b = this.direction.getAverageDirection();
		String id;
		
		switch (b) {
		case NORD :
			id = GameRessources.ID_WAVE_GENERATOR_NORD;
			break;
		case SUD :
			id = GameRessources.ID_WAVE_GENERATOR_SOUTH;
			break;
		case EST :
			id = GameRessources.ID_WAVE_GENERATOR_EAST;
			break;
		case OUEST :
			id = GameRessources.ID_WAVE_GENERATOR_WEST;
			break;
		default :
			id = GameRessources.ID_WAVE_GENERATOR_SOUTH;
		}
		
		return id;
	}
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(getTextureID());
	}
	
	private RenderCompound waveRender = new RenderCompound();
	
	@Override
	protected RenderEntity createRender() {
		RenderCompound render = new RenderCompound();
		
		render.addRender(new RenderEntityDefault(getTextureID()));
		render.addRender(this.waveRender);
		
		return render;
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(Entity.CASE, Entity.CASE);
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		super.updateLogic(dT, game);
		
		long date = game.getTime();
		long period = (long) (1000 / this.freq);
		long timeElapsed = this.lastWaveDate == -1 ? period : date - this.lastWaveDate; 
		
		//Création de vagues
		if (timeElapsed >= period) {
			long aliveTime = timeElapsed - period;//Indique la durée depuis laquelle l'onde existe déjà.
			MagneticWave createdWave = new MagneticWave(this.speed, this.distance, this.direction.getDirection());
			createdWave.setCoordonnéesf(this.getCoordonnéesf());
			
			createdWave.updateLogic(aliveTime + (int) (DEMI_CASE_F / this.speed), game);
			
			this.waves.add(createdWave);
			this.waveRender.addEntityToRender(createdWave);
			
			this.lastWaveDate = date - aliveTime;
		}
		
		//Suppression de vagues
		ArrayList<MagneticWave> deleted = new ArrayList<MagneticWave>();
		for (MagneticWave wave : this.waves) {
			wave.updateLogic(dT, game);
			
			if (!wave.isAlive()) {
				deleted.add(wave);
			}
		}
		for (MagneticWave toDelete : deleted) {
			this.waves.remove(toDelete);
			this.waveRender.removeEntityToRender(toDelete);
		}
	}
	
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		
		out.writeFloatField(this.direction.getDirection(), 200);
		out.writeFloatField(this.freq, 201);
		out.writeFloatField(this.distance, 202);
		out.writeFloatField(this.speed, 203);
		
		out.writeArrayField(this.waves.toArray(new MagneticWave[0]), 204);
	}
	
	@Override
	public MagneticWavesGenerator decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		
		this.direction = new PropertyDirectionnalEntity(in.readFloatField(200));
		this.freq = in.readFloatField(201);
		this.distance = in.readFloatField(202);
		this.speed = in.readFloatField(203);
		
		try {
			in.readListField(this.waves, 204);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;
	}
	
	@Override
	public MagneticWavesGenerator clone() {
		MagneticWavesGenerator clone = (MagneticWavesGenerator) super.clone();
		
		clone.direction = new PropertyDirectionnalEntity(this.direction.getDirection());
		
		clone.waves = new ArrayList<MagneticWave>();
		for (MagneticWave wave : this.waves) {
			clone.waves.add((MagneticWave) wave.clone());
		}
		clone.waveRender = new RenderCompound();
		for (MagneticWave wave : clone.waves) {
			clone.waveRender.addEntityToRender(wave);
		}
		
		return clone;
	}
}
