package org.terramagnetica.game.lvldefault.lvl3;

import java.awt.Image;

import org.terramagnetica.game.lvldefault.Entity;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

public class FloatingRock extends Entity {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	protected void createRender() {
		
	}

	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt();
	}
}
