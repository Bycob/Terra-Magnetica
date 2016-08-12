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

public class SaveData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String name;
	private int ID;
	private int niveauActuel;
	
	private long lastModified;
	
	public SaveData(String name, int ID, int level){
		this.name = name;
		this.ID = ID;
		this.niveauActuel = level;
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

	public void setID(int iD) {
		ID = iD;
	}

	public int getLevel() {
		return niveauActuel;
	}

	public void setLevel(int niveauActuel) {
		this.niveauActuel = niveauActuel;
	}
	
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}
	
	public SaveData withLastModified(long lastModified) {
		this.setLastModified(lastModified);
		return this;
	}
	
	public long getLastModified() {
		return this.lastModified;
	}
}
