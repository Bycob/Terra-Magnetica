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

import java.util.ArrayList;
import java.util.List;

public class ArrayNode extends Node {
	
	private List<Node> contents = new ArrayList<Node>();
	private Class<?> arrayClass;
	private boolean isObject;
	
	public ArrayNode(Class<?> arrayClass) {
		this.arrayClass = arrayClass;
		Class<?> contentType = this.arrayClass.getComponentType();
		if (contentType == null) contentType = this.arrayClass;
		String pname = PrimitiveNode.definePrimitiveName(contentType);
		this.isObject = pname.equals("");
	}
	
	public void addNode(Node toAdd) throws GameIOException {
		if (toAdd == null) {
			throw new GameIOException("noeud null !");
		}
		this.contents.add(toAdd);
	}
	
	public Node[] getContent() {
		return this.contents.toArray(new Node[this.contents.size()]);
	}
	
	@Override
	public String getClassName() {
		return this.arrayClass.getName();
	}

	public int length() {
		return this.contents.size();
	}

	public String getContentClassName() {
		if (this.isObject) {
			return this.arrayClass.getComponentType().getName();
		}
		else {
			return PrimitiveNode.definePrimitiveName(this.arrayClass.getComponentType());
		}
	}
}
