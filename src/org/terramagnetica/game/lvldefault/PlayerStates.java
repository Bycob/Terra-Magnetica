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

public interface PlayerStates {
	
	int REPOS = 0;
	int RUN_RIGHT = 1;
	int RUN_LEFT = 2;
	int RUN_TOP = 3;
	int RUN_BOTTOM = 4;
	
	int[] statesArray = new int[]{REPOS, RUN_RIGHT, RUN_LEFT, RUN_TOP, RUN_BOTTOM};
	
	public static final int NB_STATES = 5;
	public static final int NB_FRAMES = 1;
	public static final int SPRITE_WIDTH = 192;
	public static final int SPRITE_HEIGHT = 288;
}
