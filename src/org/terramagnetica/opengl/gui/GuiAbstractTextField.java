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

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.terramagnetica.game.gui.GameWindow;
import org.terramagnetica.opengl.engine.Painter;

import net.bynaryscode.util.Util;

public abstract class GuiAbstractTextField extends GuiComponent implements Writer, MouseListener {
	
	protected String text;
	protected GuiCursor cursor;
	private int maxCapacity = 25;
	private boolean focused = false;
	private long lastInput;
	
	protected WriterInput input = new WriterInput(this);
	
	protected GuiAbstractTextField() {
		text = "";
	}
	
	@Override
	public GuiActionEvent processLogic() {
		return GuiActionEvent.NULL_EVENT;
	}
	
	@Override
	public synchronized void write(char character) {
		if (!focused) return;
		
		String before = text.substring(0, cursor.getCursorPlace());
		String after = text.substring(cursor.getCursorPlace());
		char toInsert = character;
		boolean insert = true;
		
		if (text.length() >= maxCapacity) {
			insert = false;
		}
		
		if (insert) {
			text = before + toInsert + after;
			cursor.moveRight();
		}
		
		lastInput = GameWindow.getInstance().getTime();
	}
	
	@Override
	public synchronized void remove(int key) {
		if (!focused) return;
		String before = new String();
		String after = new String();
		boolean remove = false;
		int cursorPlace = cursor.getCursorPlace();
		
		switch (key) {
		case GLFW.GLFW_KEY_BACKSPACE :
			before = text.substring(0, (cursorPlace  > 0)? cursorPlace - 1 : cursorPlace);
			after = text.substring(cursorPlace);
			remove = (cursorPlace > 0);
			if (remove) cursor.moveLeft();
			break;
		case GLFW.GLFW_KEY_DELETE :
			before = text.substring(0, cursorPlace);
			after = text.substring((cursorPlace < text.length())? cursorPlace + 1 : cursorPlace);
			remove = (cursorPlace < text.length());
			break;
		}
		
		if (remove) {
			text = before + after;
		}
		
		lastInput = GameWindow.getInstance().getTime();
	}
	
	@Override
	public synchronized void move(int key) {
		if (!focused) return;
		
		if (key == GLFW.GLFW_KEY_RIGHT) {
			cursor.moveRight();
		} else if (key == GLFW.GLFW_KEY_LEFT) {
			cursor.moveLeft();
		}

		lastInput = GameWindow.getInstance().getTime();
	}
	
	public synchronized void setText(String text) {
		if (text.length() > maxCapacity) {
			text = text.substring(0, maxCapacity);
		}
		this.text = text;
		
		cursor.setCursorPlace(text.length());
	}
	
	public String getText() {
		return text;
	}
	
	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}
	
	public long getLastInput() {
		return lastInput;
	}
	
	@Override
	public KeyboardListener[] getKeyboardListeners() {
		
		ArrayList<KeyboardListener> listeners = Util.createList(super.getKeyboardListeners());
		listeners.add(this.input);
		
		return listeners.toArray(new KeyboardListener[0]);
	}
	
	protected abstract class GuiCursor {
		
		private int place;
		
		public GuiCursor() {
			place = 0;
		}
		
		public void setCursorPlace(int place) {
			if (place >= 0 && place <= text.length()) {
				this.place = place;
			}
		}
		
		public int getCursorPlace() {
			return place;
		}
		
		public void moveRight() {
			if (place < text.length()) place ++;
		}
		
		public void moveLeft() {
			if (place > 0) place --;
		}
		
		public abstract void drawCursor(Painter painter);
	}
}
