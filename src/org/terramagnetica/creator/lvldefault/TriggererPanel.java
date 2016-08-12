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

import javax.swing.JPanel;

import org.terramagnetica.game.lvldefault.GameEvent;
import org.terramagnetica.game.lvldefault.Room;

@SuppressWarnings("serial")
public abstract class TriggererPanel extends JPanel {
	
	protected Room theRoom = null;
	
	public TriggererPanel() {
		
	}
	
	public void setRoom(Room r) {
		this.theRoom = r;
	}
	
	public abstract GameEvent getEvent(int x, int y);
	
	public String getTriggererName() {
		return "(untitled)";
	}
}
