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

package org.terramagnetica.creator;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;

@SuppressWarnings("serial")
public class JMenuItemWithIDs extends JMenuItem {
	
	private int id;
	
	public JMenuItemWithIDs() {
		
	}

	public JMenuItemWithIDs(Icon icon) {
		super(icon);
	}

	public JMenuItemWithIDs(String text) {
		super(text);
	}

	public JMenuItemWithIDs(Action a) {
		super(a);
	}

	public JMenuItemWithIDs(String text, Icon icon) {
		super(text, icon);
	}

	public JMenuItemWithIDs(String text, int mnemonic) {
		super(text, mnemonic);
	}
	
	public int getID() {
		return this.id;
	}
	
	public void setID(int id) {
		this.id = id;
	}
}
