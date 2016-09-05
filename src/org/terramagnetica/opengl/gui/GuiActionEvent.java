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

public class GuiActionEvent {
	
	/** L'évènement null */
	public static final GuiActionEvent NULL_EVENT = new GuiActionEvent("null");
	public static final GuiActionEvent CLICK_EVENT = new GuiActionEvent("click");
	
	private Object message;
	
	public GuiActionEvent() {
		message = null;
	}
	
	public GuiActionEvent(Object message){
		this.message = message;
	}
}
