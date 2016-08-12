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

package org.terramagnetica.game.gui;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.terramagnetica.game.GameInputBuffer.InputKey;
import org.terramagnetica.game.Options;
import org.terramagnetica.opengl.engine.GLUtil;
import org.terramagnetica.opengl.engine.Painter;
import org.terramagnetica.opengl.engine.Painter.Primitive;
import org.terramagnetica.opengl.gui.FontSizeManager;
import org.terramagnetica.opengl.gui.FontSizeRelativeToRectangle;
import org.terramagnetica.opengl.gui.GuiComponent;
import org.terramagnetica.opengl.gui.KeyboardEvent;
import org.terramagnetica.opengl.gui.KeyboardListener;
import org.terramagnetica.opengl.gui.MouseEvent;
import org.terramagnetica.opengl.gui.MouseListener;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class GuiControlsComponent extends GuiComponent implements
		KeyboardListener, MouseListener {
	
	private Options options;
	
	private List<Slot> slots = new ArrayList<Slot>();
	private int index_selected = 0;
	private int index_edit = -1;
	/** Nombre de colonnes. */
	private int column;
	/** nombre de slots dans une colonne. */
	private int column_height;
	
	public static final double SLOT_HEIGHT = 0.2;
	public static final double SLOT_WIDTH = 1.2;
	public static final double SLOT_VGAP = 0.05;
	public static final double SLOT_HGAP = 0.05;
	
	public final FontSizeManager fsm = new FontSizeRelativeToRectangle(this.theTextPainter);
	
	public GuiControlsComponent(Options opt) {
		this.options = opt;
		this.updateSlots();
		this.updateSlotsPosition();
	}
	
	private void updateSlots() {
		this.slots.clear();
		for (InputKey val : InputKey.values()) {
			int keyID = this.options.getInputID(val);
			if (keyID != -1) this.slots.add(new Slot(val, keyID));
		}
		if (this.index_selected != -1 && this.index_selected < this.slots.size()) {
			this.slots.get(this.index_selected).selected = true;
		}
		if (this.index_edit != -1 && this.index_edit < this.slots.size()) {
			this.slots.get(this.index_edit).edit = true;
		}
	}
	
	@Override
	public void eventMouse(MouseEvent event) {
		Vec2i c = new Vec2i(event.getX(), event.getY());
		
		if (event.getButton() == BUTTON_LEFT && event.getState()) {
			for (int i = 0 ; i < this.slots.size() ; i++) {
				if (boundsGLToDisp(getSlotBounds(i)).contains(c)) {
					
					if (this.index_selected != -1 && this.index_selected < this.slots.size()) {
						this.slots.get(this.index_selected).selected = false;
					}
					
					if (this.index_edit != -1 && this.index_edit < this.slots.size()) {
						this.slots.get(this.index_edit).edit = false;
					}
					
					if (this.index_selected == i) {
						this.slots.get(i).edit = true;
						this.index_edit = i;
					}
					else {
						this.index_selected = i;
						this.slots.get(i).selected = true;
						this.index_edit = -1;
					}
					
					break;
				}
			}
		}
	}

	@Override
	public void eventKey(KeyboardEvent e) {
		if (!e.getKeyState()) return;
		
		if (this.index_edit != -1 && this.index_edit < this.slots.size()) {
			Slot s = this.slots.get(this.index_edit);
			editKey(s.command, e.getKey());
		}
		else {
			switch (e.getKey()) {
			case Keyboard.KEY_UP :
				if (this.index_selected > 0) this.index_selected--;
				updateSlots();
				break;
			case Keyboard.KEY_DOWN :
				if (this.index_selected < this.slots.size() - 1) this.index_selected++;
				updateSlots();
				break;
			case Keyboard.KEY_RIGHT :
				this.index_selected += this.column_height;
				if (this.index_selected >= this.slots.size()) this.index_selected -= this.column_height;
				updateSlots();
				break;
			case Keyboard.KEY_LEFT :
				this.index_selected -= this.column_height;
				if (this.index_selected < 0) this.index_selected += this.column_height;
				updateSlots();
				break;
			case Keyboard.KEY_RETURN :
				if (this.index_selected != -1 && this.index_selected < this.slots.size()) {
					this.index_edit = this.index_selected;
					updateSlots();
				}
				break;
			}
		}
	}
	
	private void editKey(InputKey key, int keyID) {
		try {
			this.options.setInput(key, keyID);
		} catch (IllegalStateException e) {
			Toolkit.getDefaultToolkit().beep();
		}
		this.index_edit = -1;
		if (this.index_selected < this.slots.size() - 1) this.index_selected++;
		this.updateSlots();
	}
	
	private void updateSlotsPosition() {
		final double height = this.getHeightGL();
		final double slotSize = SLOT_HEIGHT + SLOT_VGAP;
		this.column = (int) Math.ceil(slotSize * this.slots.size() / height);
		this.column_height = Math.max(1, (int) Math.floor(height / slotSize));
	}
	
	private RectangleDouble getSlotBounds(int index) {
		final double slotWidth = SLOT_WIDTH + SLOT_HGAP;
		final double slotXMin = this.getBoundsGL().center().x - (this.column * slotWidth - SLOT_HGAP) / 2;
		final double slotYMin = this.getBoundsGL().ymin - SLOT_VGAP;
		Vec2i loc = new Vec2i(
				(int) Math.floor((double) index / this.column_height),
				index % this.column_height);
		
		RectangleDouble result = new RectangleDouble();
		result.xmin = slotXMin + slotWidth * loc.x;
		result.xmax = result.xmin + SLOT_WIDTH;
		result.ymin = slotYMin - (SLOT_HEIGHT + SLOT_VGAP) * loc.y;
		result.ymax = result.ymin - SLOT_HEIGHT;
		return result;
	}
	
	@Override
	public void drawComponent() {
		updateSlotsPosition();
		for (int i = 0 ; i < this.slots.size() ; i++) {
			this.slots.get(i).drawSlot(getSlotBounds(i));
		}
	}
	
	protected class Slot {
		private InputKey command;
		private int key_value;
		private String key;
		
		protected boolean selected = false;
		protected boolean edit = false;
		
		public Slot(InputKey command, int key_value) {
			this.command = command;
			this.key_value = key_value;
			this.key = Keyboard.getKeyName(this.key_value);
		}
		
		public void drawSlot(RectangleDouble bounds) {
			Painter painter = Painter.instance;
			painter.ensure2D();
			painter.setPrimitive(Primitive.QUADS);
			painter.setTexture(null);
			
			if (this.edit)
				painter.setColor(new Color4f(255, 100, 0, 64));
			else if (this.selected)
				painter.setColor(new Color4f(234, 255, 0, 32));
			else painter.setColor(new Color4f(0, 0, 0, 32));
			
			GLUtil.drawQuad2D(bounds, painter);
			
			String text = this.command.getKeyName() + " : " + key;
			
			GuiControlsComponent.this.theTextPainter.setColor(GuiConstants.TEXT_COLOR_DEFAULT);
			int fontSize = fsm.calculFontSize(boundsGLToDisp(bounds), text, 12);
			GuiControlsComponent.this.theTextPainter.drawString2DBeginAt(text, bounds.xmin + 0.03, bounds.center().y, fontSize);
		}
	}
}
