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

package org.terramagnetica.physics;

import java.util.HashMap;

/** Cette classe définit une famille de hitbox et son comportement
 * vis-à-vis des autres familles de hitbox.*/
public class HitboxFamily {
	
	private String name;
	private HashMap<String, Boolean> collideMap = new HashMap<String, Boolean>();
	
	/** @param name Le nom de la famille, utilisé comme identifiant
	 * pour la reconnaitre. Si vaut <tt>null</tt>, alors le nom de la
	 * famille sera une chaine de caractère vide. Dans ce cas, cette
	 * famille représentera la famille par défaut */
	public HitboxFamily(String name) {
		if (name == null) name = "";
		this.name = name;
		
		setCollisionPermissionDefault(true);
	}
	
	/** @return le nom de cette famille.  */
	public String getName() {
		return this.name;
	}
	
	public void setCollisionPermission(String familyName, boolean permission) {
		this.collideMap.put(familyName, permission);
	}
	
	public void setCollisionPermissionDefault(boolean permission) {
		setCollisionPermission("", permission);
	}
	
	public void setCollisionPermission(HitboxFamily family, boolean permission) {
		if (family == null) setCollisionPermission("", permission);
		setCollisionPermission(family.name, permission);
	}
	
	public boolean canCollide(HitboxFamily other) {
		if (other == null) return this.collideMap.get("");
		Boolean thisEntry = this.collideMap.get(other.name);
		Boolean otherEntry = other.collideMap.get(this.name);
		
		return (thisEntry != null ? thisEntry : true) && (otherEntry != null ? otherEntry : true);
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof HitboxFamily)) return false;
		return this.name.equals(((HitboxFamily) other).name);
	}
}
