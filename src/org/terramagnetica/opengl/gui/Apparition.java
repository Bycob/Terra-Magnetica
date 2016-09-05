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

package org.terramagnetica.opengl.gui;

import org.terramagnetica.opengl.miscellaneous.Timer;

public class Apparition implements Behavior {
	
	private GuiComponent c;
	private Timer timer = new Timer();
	private long time = 0;
	
	@Override
	public void update() {
		if (this.c != null && this.timer.getTime() >= this.time) {
			this.c.setVisible(false);
			this.c = null;
			this.timer.stop();
			this.time = 0;
		}
	}
	
	public void appearDuring(GuiComponent c, long time) {
		if (time < 0) {
			throw new IllegalArgumentException("Le temps doit être positif !");
		}
		this.c = c;
		this.time = time;
		this.timer.start();
		this.c.setVisible(true);
	}
}
