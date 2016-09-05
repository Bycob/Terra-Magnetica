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

package org.terramagnetica.ressources.io;

public abstract class Node {
	
	private boolean isField;
	private short fieldID;
	ObjectNode parent = null;
	
	public Node() {
		this.isField = false;
		this.fieldID = 0;
	}
	
	public Node(short fieldID) {
		this.isField = true;
		this.fieldID = fieldID;
	}
	
	public short getFieldID() {
		return this.fieldID;
	}
	
	public boolean isField() {
		return this.isField;
	}
	
	public void setFieldID(short id) {
		this.isField = true;
		this.fieldID = id;
	}
	
	public void setNonField() {
		this.isField = false;
	}
	
	public abstract String getClassName();
}
