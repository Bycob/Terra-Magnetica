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

public enum DecorType {
	MONTS(0, "Montagnes"),
	GROTTE(1, "Grotte"),
	
	ENFERS(100, "Enfers");
	
	private int index;
	private String name;
	
	DecorType(int index, String name) {
		this.index = index;
		this.name = name;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public String getName() {
		return this.name;
	}
	
	public static DecorType getForName(String name) {
		for (DecorType value : values()) {
			if (value.getName().equals(name)) {
				return value;
			}
		}
		
		return null;
	}
}
