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

package org.terramagnetica.game.lvldefault.lvl2;

import java.awt.Image;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.EntityMoving;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.IA.AIMovable;
import org.terramagnetica.game.lvldefault.lvl2.Level2.CreatureAI;
import org.terramagnetica.game.lvldefault.rendering.RenderEntity;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityTexture;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.RectangleInt;

public class TheCreature extends EntityMoving {
	
	private static final long serialVersionUID = 1L;
	
	private CreatureAI myAI;
	
	private RectangleInt bounds;
	
	public TheCreature() {
		FreeCreatureAI creatureAI = new FreeCreatureAI(this);
		this.myAI = creatureAI;
	}
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(GameRessources.PATH_CREATURE);
	}
	
	@Override
	public DimensionsInt getImgDimensions() {
		return new DimensionsInt(678 * 2 / 3, 240 * 2 / 3);
	}
	
	@Override
	protected RenderEntity createRender() {
		return new RenderEntityTexture(GameRessources.PATH_CREATURE).withScale(0.4, 0.4);
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(DEMI_CASE, DEMI_CASE);
	}
	
	@Override
	public boolean canPassVirtualWall() {
		return true;
	}
	
	public AIMovable<TheCreature> getCreatureAI() {
		return this.myAI;
	}
	
	/** L'AI de la créature devient une {@link CapturedCreatureAI} qui va mener
	 * le joueur à la fin du niveau. */
	public void changeAI() {
		this.myAI = new CapturedCreatureAI(this);

		this.hitbox.setSolid(false);
	}
	
	public void setZoneBounds(RectangleInt bounds) {
		this.bounds = bounds;
		if (this.myAI instanceof FreeCreatureAI) {
			((FreeCreatureAI) this.myAI).setBounds(this.bounds);
		}
	}
	
	public RectangleInt getZoneBounds() {
		return this.bounds;
	}
	
	@Override
	public void updateLogic(long tick, GamePlayingDefault game) {
		//Pour tester directement l'animation de fin de niveau.
		//if (this.myAI instanceof FreeCreatureAI) changeAI();
		this.myAI.update(game);
		
		super.updateLogic(tick, game);
	}
	
	@Override
	public TheCreature clone() {
		TheCreature clone = (TheCreature) super.clone();
		
		clone.myAI = this.myAI.clone();
		clone.myAI.setEntity(clone);
		return clone;
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		
		out.writeIntField(this.myAI instanceof FreeCreatureAI ? 1 : 2, 200);
		out.writeCodableField(this.myAI, 201);
		
		//Rectangle limite
		if (this.bounds != null) {
			out.writeIntField(this.bounds.xmin, 210);
			out.writeIntField(this.bounds.ymin, 211);
			out.writeIntField(this.bounds.xmax, 212);
			out.writeIntField(this.bounds.ymax, 213);
		}
	}
	
	@Override
	public TheCreature decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		
		int id = in.readIntFieldWithDefaultValue(200, 1);
		
		if (id == 1) {
			this.myAI = new FreeCreatureAI(this);
		}
		else {
			this.myAI = new CapturedCreatureAI(this);
		}
		
		in.readCodableField(this.myAI, 201);
		
		try {
			setZoneBounds(new RectangleInt(
					in.readIntField(210),
					in.readIntField(211),
					in.readIntField(212),
					in.readIntField(213)));
		} catch (GameIOException e) {
			this.bounds = null;
		}
		
		return this;
	}
}
