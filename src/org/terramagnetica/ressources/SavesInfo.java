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

import java.io.Serializable;

import org.terramagnetica.game.Sauvegarde;
import org.terramagnetica.utile.RuntimeGameException;

public class SavesInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private SaveData[] saves;
	private int nbSaves;
	
	public static final int MAX_ENREGISTRABLE = 50;
	
	public SavesInfo() {
		this.saves = new SaveData[MAX_ENREGISTRABLE];
		this.nbSaves = 0;
	}
	
	public int registerSave(Sauvegarde save) {
		int id  = newID();
		this.nbSaves++;
		
		if (this.nbSaves >= MAX_ENREGISTRABLE || id == -1) {
			this.nbSaves --;
			throw new RuntimeGameException("Le nombre de sauvegarde a atteint sa limite !");
		}
		
		this.saves[id] = createSaveDataForSave(save, id);
		save.setID(id);
		
		return id;
	}
	
	public int getIDForName(String name) {
		for (SaveData data : this.saves) {
			if (data != null && data.getName().equals(name)) {
				return data.getID();
			}
		}
		
		return -1;
	}
	
	public SaveData getDataByID(int id) {
		if (id >= MAX_ENREGISTRABLE || id < 0) {
			throw new IndexOutOfBoundsException();
		}
		
		return this.saves[id];
	}
	
	/**
	 * Met les informations sur la sauvegarde à jour.
	 * @param save
	 * @throws NullPointerException Si la sauvegarde n'existe pas
	 * et donc ne peut être mise à jour.
	 */
	public void update(Sauvegarde save) {
		int id = save.getID();
		SaveData data = this.saves[id];
		
		if (data == null) {
			throw new NullPointerException("Unknown save");
		}
		
		this.saves[id] = createSaveDataForSave(save, id);
	}
	
	public boolean removeByID(int id) {
		if (this.saves[id] == null) {
			return false;
		}
		
		this.saves[id] = null;
		this.nbSaves--;
		return true;
	}
	
	public SaveData[] getSavesData() {
		SaveData[] result = new SaveData[this.nbSaves];
		int j = 0;
		
		for (SaveData data : this.saves) {
			if (data != null) {
				result[j] = data;
				j++;
			}
		}
		
		return result;
	}
	
	public int getNbSaves() {
		return this.nbSaves;
	}
	
	public SaveData createSaveDataForSave(Sauvegarde save, int id) {
		return new SaveData(save.getName(), id, save.getStory().getCurrentLevelID())
			.withLastModified(save.getLastModified());
	}
	
	int newID() {
		for (int i = 0 ; i < MAX_ENREGISTRABLE ; i++) {
			if (this.saves[i] == null) {
				return i;
			}
		}
		return -1;
	}
}
