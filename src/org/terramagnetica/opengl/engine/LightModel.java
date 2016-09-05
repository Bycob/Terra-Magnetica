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

package org.terramagnetica.opengl.engine;

import java.util.HashMap;
import java.util.Map.Entry;

public class LightModel {
	
	private HashMap<Integer, Light> lights = new HashMap<Integer, Light>();
	
	public LightModel() {
		
	}
	
	public Light addLight() {
		int freeID = 0;
		while (this.lights.containsKey(freeID)) {
			freeID ++;
		}
		
		Light newLight = new Light(freeID, this);
		
		notifyBeforeChanges();
		this.lights.put(freeID, newLight);
		
		return newLight;
	}
	
	/** Retourne la lumière 1. Utilisé dans les systèmes avec une seule source de lumière. */
	public Light getLight0() {
		Light l = this.lights.get(0);
		if (l == null) return addLight();
		
		return l;
	}
	
	public boolean removeLight(Light light) {
		if (light.getModel() == this) {
			notifyBeforeChanges();
			boolean result = this.lights.remove(light.getID()) != null;
			
			light.id = -1;
			light.model = null;
			
			return result;
		}
		
		return false;
	}
	
	public void removeAllLights() {
		if (lightCount() != 0) notifyBeforeChanges();
		
		for (Entry<Integer, Light> e : this.lights.entrySet()) {
			Light light = e.getValue();
			
			light.id = -1;
			light.model = null;
		}
		
		this.lights.clear();
	}
	
	public int lightCount() {
		return this.lights.size();
	}
	
	void activate() {
		for (Entry<Integer, Light> e : this.lights.entrySet()) {
			e.getValue().activate();
		}
	}
	
	void desactivate() {
		
	}

	
	Painter painter;
	
	private void notifyBeforeChanges() {
		if (painter != null) {
			painter.notifyExternalChanges();
		}
	}
	
}
