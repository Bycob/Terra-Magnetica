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

package org.terramagnetica.ressources.io;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ObjectNode extends Node {
	
	private Map<Short, Node> fields = new HashMap<Short, Node>();
	private String className;
	
	public ObjectNode(String className) {
		this.className = className;
	}
	
	public void addField(Node field, short id) {
		field.setFieldID(id);
		field.parent = this;
		this.fields.put(id, field);
	}
	
	public Set<Entry<Short, Node>> getFieldSet() {
		return this.fields.entrySet();
	}
	
	@Override
	public String getClassName() {
		return this.className;
	}
	
	public boolean hasField(short fid) {
		return this.fields.containsKey(fid);
	}
	
	private PrimitiveNode<?> getPrimitiveField(short fid) throws GameIOException {
		Node f = this.fields.get(fid);
		if (f == null || !(f instanceof PrimitiveNode)) {
			throw new GameIOException("champ inconnu");
		}
		return (PrimitiveNode<?>) f;
	}
	
	public boolean getBoolField(short fid) throws GameIOException {
		PrimitiveNode<?> fp = getPrimitiveField(fid);
		String cn = fp.getClassName();
		if (!cn.equals("boolean")) {
			throw new GameIOException("champ inconnu");
		}
		return (Boolean) fp.getValue();
	}
	
	public byte getByteField(short fid) throws GameIOException {
		PrimitiveNode<?> fp = getPrimitiveField(fid);
		String cn = fp.getClassName();
		if (!cn.equals("byte")) {
			throw new GameIOException("champ inconnu");
		}
		return (Byte) fp.getValue();
	}
	
	public short getShortField(short fid) throws GameIOException {
		PrimitiveNode<?> fp = getPrimitiveField(fid);
		String cn = fp.getClassName();
		if (!cn.equals("short")) {
			throw new GameIOException("champ inconnu");
		}
		return (Short) fp.getValue();
	}
	
	public int getIntField(short fid) throws GameIOException {
		PrimitiveNode<?> fp = getPrimitiveField(fid);
		String cn = fp.getClassName();
		if (!cn.equals("int")) {
			throw new GameIOException("champ inconnu");
		}
		return (Integer) fp.getValue();
	}
	
	public float getFloatField(short fid) throws GameIOException {
		PrimitiveNode<?> fp = getPrimitiveField(fid);
		String cn = fp.getClassName();
		if (!cn.equals("float")) {
			throw new GameIOException("champ inconnu");
		}
		return (Float) fp.getValue();
	}
	
	public long getLongField(short fid) throws GameIOException {
		PrimitiveNode<?> fp = getPrimitiveField(fid);
		String cn = fp.getClassName();
		if (!cn.equals("long")) {
			throw new GameIOException("champ inconnu");
		}
		return (Long) fp.getValue();
	}
	
	public double getDoubleField(short fid) throws GameIOException {
		PrimitiveNode<?> fp = getPrimitiveField(fid);
		String cn = fp.getClassName();
		if (!cn.equals("double")) {
			throw new GameIOException("champ inconnu");
		}
		return (Double) fp.getValue();
	}
	
	public String getStringField(short fid) throws GameIOException {
		PrimitiveNode<?> fp = getPrimitiveField(fid);
		String cn = fp.getClassName();
		if (!cn.equals("String")) {
			throw new GameIOException("champ inconnu");
		}
		return (String) fp.getValue();
	}
	
	public ObjectNode getObjectField(short fid, String className) throws GameIOException {
		Node f = this.fields.get(fid);
		if (!(f instanceof ObjectNode)) {
			throw new GameIOException("champ inconnu");
		}
		ObjectNode of = (ObjectNode) f;
		if (of.getClassName().equals("null")) {
			return of;
		}
		if (!compatibleClass(of.getClassName(), className)) {
			throw new GameIOException("champ inconnu");
		}
		return of;
	}
	
	public ArrayNode getArrayField(short fid, String className) throws GameIOException {
		Node f = this.fields.get(fid);
		if (!(f instanceof ArrayNode)) {
			throw new GameIOException("champ inconnu");
		}
		ArrayNode af = (ArrayNode) f;
		return af;
	}
	
	public static boolean compatibleClass(String realClassName, String possibleClassName) {
		if (realClassName.equals(possibleClassName)) {
			return true;
		}
		
		try {
			Class<?> c1 = Class.forName(realClassName);
			Class<?> c2 = Class.forName(possibleClassName);
			return c2.isAssignableFrom(c1);
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
