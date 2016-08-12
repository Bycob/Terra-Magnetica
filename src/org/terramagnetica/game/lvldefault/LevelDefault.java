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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.terramagnetica.game.Level;
import org.terramagnetica.game.lvldefault.lvl2.ControlPaneSystemManager;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.Codable;
import org.terramagnetica.ressources.io.GameIOException;

public class LevelDefault extends Level implements Serializable, Cloneable, Codable{
	
	private static final long serialVersionUID = 1L;
	
	protected Map<Integer, Room> rooms = new HashMap<Integer, Room>();
	protected int indexMainRoom = 0;
	
	@Override
	public GamePlayingDefault createGameEngine() {
		GamePlayingDefault result = new GamePlayingDefault(this);
		
		result.addAspect(new LampState(result));
		result.addAspect(new PortalNameFinder(result));
		result.addAspect(new GameEventDispatcher(result));
		result.addAspect(new MapUpdater(result));
		
		switch (this.levelID) {
		
		case 1 : break;
		
		case 2 :
			result.addAspect(new ControlPaneSystemManager());
			break;
		}
		
		return result;
	}
	
	public LevelDefault(){
		this(new Room());
	}
	
	public LevelDefault(Room main){
		main.setID(0);
		this.rooms.put(0, main);
	}
	
	public void putRoom(int index, Room room){
		if (room != null) {
			this.rooms.put(index, room);
			room.setID(index);
		}
	}
	
	public int addRoom(Room room) {
		int foundIndex = 0;
		while (this.rooms.get(foundIndex) != null) {
			foundIndex++;
		}
		this.putRoom(foundIndex, room);
		return foundIndex;
	}
	
	/**
	 * Retire une salle du niveau.
	 * @param index - l'ID de la salle à retirer.
	 * @return La salle supprimée.
	 * @throws IllegalArgumentException Si la salle portant l'ID
	 * {@code index} est la salle principale.
	 */
	public Room delRoom(int index) {
		if (index == this.indexMainRoom) {
			throw new IllegalArgumentException("Impossible de supprimer la salle principale !");
		}
		Room removed = this.rooms.remove(index);
		if (removed != null) {
			removed.setID(-1);
		}
		return removed;
	}
	
	/**
	 * @param index
	 * @return La salle portant l'ID {@code index}, ou {@code null}
	 * si elle n'existe pas.
	 */
	public Room getRoom(int index){
		return this.rooms.get(index);
	}
	
	public ArrayList<Room> getRoomList() {
		ArrayList<Room> result = new ArrayList<Room>();
		
		for (Entry<Integer, Room> e : this.rooms.entrySet()) {
			result.add(e.getValue());
		}
		
		return result;
	}
	
	public void setMainRoom(Room main){
		main.setID(this.indexMainRoom);
		this.putRoom(this.indexMainRoom, main);
	}
	
	public void setMainRoom(int index){
		if (this.rooms.get(index) != null) {
			this.indexMainRoom = index;
		}
	}
	
	public Room getMainRoom(){
		return this.rooms.get(this.indexMainRoom);
	}
	
	@Override
	public boolean isRunnable() {
		for (Room r : getRoomList()) {
			if (r.getPlayer() == null) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + indexMainRoom;
		result = prime * result + ((rooms == null) ? 0 : rooms.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LevelDefault)) {
			return false;
		}
		LevelDefault other = (LevelDefault) obj;
		if (indexMainRoom != other.indexMainRoom) {
			return false;
		}
		if (rooms == null) {
			if (other.rooms != null) {
				return false;
			}
		} else if (!rooms.equals(other.rooms)) {
			return false;
		}
		return true;
	}

	@Override
	public LevelDefault clone(){
		LevelDefault result = null;
		result = (LevelDefault) super.clone();
		
		result.rooms = new HashMap<Integer, Room>();
		
		Iterator<Entry<Integer, Room>> iter = this.rooms.entrySet().iterator();
		
		Entry<Integer, Room> element = null;
		while (iter.hasNext()) {
			element = iter.next();
			result.rooms.put(element.getKey(), element.getValue().clone());
		}
		
		return result;
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException{
		out.writeIntField(this.indexMainRoom, 100);
		out.writeMapField(this.rooms, 101);
	}
	
	@Override
	public LevelDefault decode(BufferedObjectInputStream in) throws GameIOException{
		this.indexMainRoom = in.readIntField(100);
		try {
			this.rooms = in.readMapField(Integer.class, Room.class, 101);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this;
	}
}
