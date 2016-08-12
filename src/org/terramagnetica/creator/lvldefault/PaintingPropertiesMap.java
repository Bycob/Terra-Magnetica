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

package org.terramagnetica.creator.lvldefault;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.terramagnetica.creator.PaintingListener;

public class PaintingPropertiesMap {
	
	public static class Property implements Comparable<Property> {
		private int id;
		private Class<?> key;
		
		private PaintingListener paintingListener;
		private PanelEntityProperties<?> propertyPanel;
		private String name = "";
		private PinceauFilterLevelDefault filter;
		private boolean showDialogOnCreate = false;

		private Property(Class<?> key) {
			this.key = key;
		}
		
		public Class<?> getKeyClass() {
			return this.key;
		}
		
		public Property setPaintingListener(PaintingListener listener) {
			this.paintingListener = listener;
			return this;
		}
		
		public PaintingListener getPaintingListener() {
			return this.paintingListener;
		}
		
		public PanelEntityProperties<?> getPropertyPanel() {
			return propertyPanel;
		}
		
		public Property setPropertyPanel(PanelEntityProperties<?> propertyPanel) {
			this.propertyPanel = propertyPanel;
			return this;
		}
		
		public String getName() {
			return name;
		}
		
		public Property setName(String name) {
			if (name == null) throw new NullPointerException("name == null !");
			this.name = name;
			return this;
		}
		
		public PinceauFilterLevelDefault getFilter() {
			return this.filter;
		}
		
		public Property setFilter(PinceauFilterLevelDefault filter) {
			this.filter = filter;
			return this;
		}
		
		public boolean shouldShowDialogOnCreate() {
			return this.showDialogOnCreate;
		}
		
		public Property setShowDialogOnCreate(boolean show) {
			this.showDialogOnCreate = show;
			return this;
		}

		@Override
		public int compareTo(Property o) {
			return this.id - o.id;
		}
	}
	
	private Map<Class<?>, Property> properties = new HashMap<Class<?>, Property>();
	
	public PaintingPropertiesMap() {
		
	}
	
	public PaintingPropertiesMap putPaintingListener(Class<?> key, PaintingListener value) {
		getProperty(key).paintingListener = value;
		return this;
	}
	
	public PaintingListener getPaintingListener(Class<?> key) {
		return getProperty(key).paintingListener;
	}
	
	public PaintingPropertiesMap putName(Class<?> key, String name) {
		getProperty(key).setName(name);
		return this;
	}
	
	public String getName(Class<?> key) {
		return getProperty(key).name;
	}
	
	public PaintingPropertiesMap putPropertyPanel(Class<?> key, PanelEntityProperties<?> panel) {
		getProperty(key).setPropertyPanel(panel);
		return this;
	}
	
	public PanelEntityProperties<?> getPropertyPanel(Class<?> key) {
		return getProperty(key).propertyPanel;
	}
	
	public Property getProperty(Class<?> key) {
		if (this.properties.containsKey(key)) {
			return this.properties.get(key);
		}
		else {
			Property p = new Property(key);
			p.id = this.properties.size();
			this.properties.put(key, p);
			return p;
		}
	}

	public Property[] getAllProperties() {
		List<Property> result = new ArrayList<Property>(this.properties.values());
		Collections.sort(result);
		return result.toArray(new Property[0]);
	}
}
