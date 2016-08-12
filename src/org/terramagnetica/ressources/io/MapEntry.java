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

import java.util.Map;

/**
 * L'implémentation {@link Codable} de {@link java.util.Map#Entry}.
 * Ne supporte pas les valeurs nulles si les types K et V ne sont pas spécifiés.
 * @author Louis JEAN
 *
 * @param <K> Le type de la clé.
 * @param <V> Le type de la valeur.
 */
public class MapEntry<K, V> implements Codable {
	
	private Class<?> kClass;
	private Class<?> vClass;
	
	K key;
	V value;
	
	public MapEntry() {
		
	}
	
	public MapEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}
	
	public MapEntry(Class<?> kClass, Class<?> vClass) {
		this.kClass = kClass;
		this.vClass = vClass;
	}
	
	public MapEntry(Map.Entry<K, V> e) {
		this(e.getKey(), e.getValue());
	}
	
	public void putInMap(Map<K, V> map) {
		map.put(key, value);
	}

	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		String kpn = PrimitiveNode.definePrimitiveName(kClass == null ? key.getClass() : kClass);
		String vpn = PrimitiveNode.definePrimitiveName(vClass == null ? value.getClass() : vClass);
		
		if (kpn.equals("")) {
			if (this.key instanceof Codable) {
				out.writeCodableField((Codable) this.key, 0);
			}
			else throw new IllegalArgumentException("clé invalide");
		}
		else {
			out.writePrimitiveField(this.key, kpn, 0);
		}
		
		if (vpn.equals("")) {
			if (this.value instanceof Codable) {
				out.writeCodableField((Codable) this.value, 1);
			}
			else throw new IllegalArgumentException("valeur invalide");
		}
		else {
			out.writePrimitiveField(this.value, vpn, 1);
		}
		
		out.writeStringField((kClass == null ? key.getClass() : kClass).getName(), 2);
		out.writeStringField((vClass == null ? value.getClass() : vClass).getName(), 3);
	}

	@SuppressWarnings("unchecked")
	@Override
	public MapEntry<K, V> decode(BufferedObjectInputStream in) throws GameIOException {
		try {
			kClass = Class.forName(in.readStringField(2));
			vClass = Class.forName(in.readStringField(3));
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		
		
		String kpn = PrimitiveNode.definePrimitiveName(kClass == null ? key.getClass() : kClass);
		String vpn = PrimitiveNode.definePrimitiveName(vClass == null ? value.getClass() : vClass);
		
		if (kpn.equals("")) {
			try {
				this.key = (K) in.readCodableInstanceField((Class<? extends Codable>) kClass, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			this.key = (K) in.readPrimitiveField(kpn, 0);
		}
		
		if (vpn.equals("")) {
			try {
				this.value = (V) in.readCodableInstanceField((Class<? extends Codable>) vClass, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {
			this.value = (V) in.readPrimitiveField(vpn, 1);
		}
		
		return this;
	}
}
