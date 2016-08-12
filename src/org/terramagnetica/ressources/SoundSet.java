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

import java.util.ArrayList;
import java.util.List;

public class SoundSet {
	
	public class SoundSetEntry {
		
		private String path;
		
		private SoundSetEntry(String path) {
			this.path = path;
		}
		
		public String getPath() {
			return this.path;
		}
	}
	
	private List<SoundSetEntry> entries = new ArrayList<SoundSetEntry>();
	
	public SoundSet() {
		
	}
	
	public SoundSetEntry addSound(String path) {
		if (path == null || "".equals(path)) throw new NullPointerException("path == null");
		
		SoundSetEntry sse = getSoundEntryByPath(path);
		if (sse != null) {
			return sse;
		}
		
		sse = new SoundSetEntry(path);
		this.entries.add(sse);
		
		return sse;
	}
	
	public SoundSetEntry getSoundEntryByPath(String path) {
		for (SoundSetEntry e : this.entries) {
			if (e.getPath().equals(path)) return e;
		}
		return null;
	}
	
	public SoundSetEntry[] getEntryList() {
		return this.entries.toArray(new SoundSetEntry[0]);
	}
}
