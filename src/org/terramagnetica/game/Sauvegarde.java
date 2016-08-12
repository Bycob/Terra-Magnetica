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

package org.terramagnetica.game;

import java.io.Serializable;
import java.util.Arrays;

import org.terramagnetica.ressources.ExternalFilesManager;
import org.terramagnetica.ressources.RessourcesManager;
import org.terramagnetica.ressources.SavesInfo;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.GameIOException;
import org.terramagnetica.utile.GameException;

public class Sauvegarde implements Serializable, Codable {
	
	private static final long serialVersionUID = 1L;
	
	public Sauvegarde_Histoire story;
	
	protected String name;
	protected boolean[] niveauxTerminés;
	protected int ID;
	
	/** La date de dernière modification. */
	protected long lastModified;
	
	/** Les carnets : quelles pages ont été consultées par
	 * le joueur... */
	protected Carnets carnets = new Carnets();
	
	public Sauvegarde() throws GameException {
		this("");
	}
	
	public Sauvegarde(String name) throws GameException {
		SavesInfo infos = ExternalFilesManager.infos;
		
		if (infos.getIDForName(name) != -1) {
			throw new GameException("Invalid name !");
		}
		
		this.story = new Sauvegarde_Histoire(this);
		this.name = name;
		this.niveauxTerminés = new boolean[RessourcesManager.NB_LEVEL];
		Arrays.fill(this.niveauxTerminés, false);
		
		updateLastModified();
	}
	
	public Sauvegarde_Histoire getStory() {
		return story;
	}

	public void setStory(Sauvegarde_Histoire story) {
		this.story = story;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getID() {
		return ID;
	}

	public void setID(int id) {
		ID = id;
	}
	
	public int nbFreeHistoryLevel() {
		int c = 0;
		
		for (boolean b : this.niveauxTerminés) {
			if (b) c++;
		}
		
		return c;
	}
	
	public boolean isAvailable(int lvl) {
		return this.niveauxTerminés[lvl];
	}
	
	public void setAvailable(int lvl) {
		this.niveauxTerminés[lvl] = true;
	}
	
	public void updateLastModified() {
		this.lastModified = System.currentTimeMillis();
	}
	
	public long getLastModified() {
		return this.lastModified;
	}
	
	/**
	 * Si le jeu a changé de version et que de nouveaux niveaux sont
	 * apparus, permet de mettre la sauvegarde à jour.
	 */
	protected void update() {
		boolean[] boolArray1 = new boolean[RessourcesManager.NB_LEVEL];
		System.arraycopy(
				this.niveauxTerminés, 0,
				boolArray1, 0,
				Math.min(this.niveauxTerminés.length, boolArray1.length));
		this.niveauxTerminés = boolArray1;
		
		this.story.update();
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		out.writeStringField(name, 0);
		out.writeIntField(ID, 1);
		out.writeCodableField(this.story, 2);
		out.writeBoolArrayField(this.niveauxTerminés, 3);
		out.writeLongField(this.lastModified, 4);
		out.writeCodableField(this.carnets, 5);
	}
	
	@Override
	public Sauvegarde decode(BufferedObjectInputStream in) throws GameIOException {
		this.name = in.readStringField(0);
		this.ID = in.readIntField(1);
		
		this.story = new Sauvegarde_Histoire(this);
		in.readCodableField(this.story, 2);
		
		this.niveauxTerminés = new boolean[in.readArrayFieldLength(Boolean.class, 3)];
		Arrays.fill(this.niveauxTerminés, false);
		try {
			in.readBoolArrayField(this.niveauxTerminés, 3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (RessourcesManager.NB_LEVEL != this.niveauxTerminés.length) {
			update();
		}
		
		this.lastModified = in.readLongFieldWithDefaultValue(4, this.lastModified);
		
		this.carnets = new Carnets();
		in.readCodableField(this.carnets, 5);
		
		return this;
	}
}
