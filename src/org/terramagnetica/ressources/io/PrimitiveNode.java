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

public class PrimitiveNode<T> extends Node {
	
	private static final Map<String, Byte> typesIDs = new HashMap<String, Byte>();
	
	static {
		typesIDs.put("boolean", (byte) 0);
		typesIDs.put("byte", (byte) 1);
		typesIDs.put("short", (byte) 2);
		typesIDs.put("int", (byte) 3);
		typesIDs.put("float", (byte) 4);
		typesIDs.put("long", (byte) 5);
		typesIDs.put("double", (byte) 6);
		
		typesIDs.put("arrays", (byte) 10);
		typesIDs.put("String", (byte) 7);
		typesIDs.put("Object", (byte) 8);
		typesIDs.put("end", (byte) 9);
	}
	
	public static final byte getID(String type) {
		Byte ID = typesIDs.get(type);
		byte id = ID == null ? -1 : ID;
		return id;
	}
	
	public static final String getType(byte id) {
		for (Entry<String, Byte> e : typesIDs.entrySet()) {
			if (e.getValue() == id) {
				return e.getKey();
			}
		}
		return "";
	}
	
	public static final boolean isPrimitive(String primitiveClass) {
		byte id = getID(primitiveClass);
		return id >= 0 && id <=7;
	}
	
	public static final boolean isPrimitive(byte id) {
		return id >= 0 && id <=7;
	}
	
	private String className;
	private T value;
	
	/**
	 * Associe à chaque classe primitive, un nom en chaine de caractère.
	 * @param type - La classe du type primitif dont on cherche le nom
	 * @return Le nom du type primitif correspondant à la classe, ou
	 * {@code new String("")} si la classe n'est pas un type primitif.
	 */
	public static String definePrimitiveName(Class<?> type) {
		String name = type.getName();
		if (name.equals(Boolean.class.getName())) {
			return "boolean";
		}
		else if (name.equals(Byte.class.getName())) {
			return "byte";
		}
		else if (name.equals(Short.class.getName())) {
			return "short";
		}
		else if (name.equals(Integer.class.getName())) {
			return "int";
		}
		else if (name.equals(Float.class.getName())) {
			return "float";
		}
		else if (name.equals(Long.class.getName())) {
			return "long";
		}
		else if (name.equals(Double.class.getName())) {
			return "double";
		}
		else if (name.equals(String.class.getName())) {
			return "String";
		}
		else {
			return "";
		}
	}
	
	/**
	 * Donne la classe qui enveloppe le type primitif du nom passé en paramètre.
	 * Par exemple si le paramètre est "int", le résultat sera {@code Integer.class}.
	 * @param primitiveName
	 * @return
	 */
	public static Class<?> defineWrapper(String primitiveName) {
		if (primitiveName.equals("boolean")) {
			return Boolean.class;
		}
		else if (primitiveName.equals("byte")) {
			return Byte.class;
		}
		else if (primitiveName.equals("short")) {
			return Short.class;
		}
		else if (primitiveName.equals("int")) {
			return Integer.class;
		}
		else if (primitiveName.equals("float")) {
			return Float.class;
		}
		else if (primitiveName.equals("long")) {
			return Long.class;
		}
		else if (primitiveName.equals("double")) {
			return Double.class;
		}
		else if (primitiveName.equals("String")) {
			return String.class;
		}
		else {
			return Object.class;
		}
	}
	
	public PrimitiveNode(T value) {
		Class<?> classe = value.getClass();
		this.className = definePrimitiveName(classe);
		if (this.className.equals("")) {
			throw new IllegalArgumentException("type non primitif !");
		}
		this.value = value;
	}
	
	@Override
	public String getClassName() {
		return this.className;
	}
	
	public T getValue() {
		return this.value;
	}
}
