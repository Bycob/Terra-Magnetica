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

import org.terramagnetica.utile.RuntimeGameException;

@SuppressWarnings("serial")
public class UnusedRoomException extends RuntimeGameException {

	public UnusedRoomException() {
		super();
	}

	public UnusedRoomException(String arg0, Throwable arg1, boolean arg2,
			boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public UnusedRoomException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UnusedRoomException(String arg0) {
		super(arg0);
	}

	public UnusedRoomException(Throwable arg0) {
		super(arg0);
	}
	
}
