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

package org.terramagnetica.game.lvldefault;

import java.util.ArrayList;

public class MiniMap {
	
	private ArrayList<MapEntity> mapEntities = new ArrayList<MapEntity>();
	private ArrayList<MapLandscape> mapLands = new ArrayList<MapLandscape>();
	
	public MiniMap() {
		
	}
	
	public void reset() {
		this.mapEntities.clear();
		this.mapLands.clear();
	}
	
	public void addMapLandscape(MapLandscape mapLand) {
		this.mapLands.add(mapLand);
	}
	
	public void addMapEntity(MapEntity mapEntity) {
		this.mapEntities.add(mapEntity);
	}
	
	public MapEntity[] getAllEntitiesMapped() {
		return this.mapEntities.toArray(new MapEntity[0]);
	}
	
	public MapLandscape[] getAllLandscapeMapped() {
		return this.mapLands.toArray(new MapLandscape[0]);
	}
}
