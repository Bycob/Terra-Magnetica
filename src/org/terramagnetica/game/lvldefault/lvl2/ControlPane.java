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

import org.terramagnetica.game.GameInputBuffer.InputKey;
import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.CaseEntity;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.IGoal;
import org.terramagnetica.opengl.engine.RenderableModel3D;
import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxPolygon;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.ModelLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.RectangleDouble;

public class ControlPane extends CaseEntity implements BarrierStateListener, IGoal {
	
	private static final long serialVersionUID = 1L;
	
	/** en degrés */
	private float orientation;
	private Color4f color;
	private boolean activated = true;
	
	public ControlPane() {
		this(90, ControlPaneSystemManager.GREEN);
	}
	
	public ControlPane(float orientation, Color4f color) {
		this.orientation = orientation;
		this.color = color;
	}
	
	public void setOrientation(float orientation) {
		this.orientation = orientation;
		recreateHitbox();
		this.destroyRender();
	}
	
	@Override
	public void setState(boolean state) {
		this.activated = state;
	}
	
	@Override
	public Color4f getColor() {
		return this.color;
	}
	
	@Override
	public Color4f getIndicationColor() {
		if (!this.activated) return new Color4f(0, 0, 0, 0);
		return getColor();
	}
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(ImagesLoader.marqueur);
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(CASE, CASE);
	}
	
	@Override
	public Hitbox createHitbox() {
		RectangleDouble bounds = new RectangleDouble(-0.5, 0, 0.5, -0.5);
		bounds.rotateFromPoint(0, 0, Math.toRadians(this.orientation));
		
		return new HitboxPolygon(bounds);
	}
	
	@Override
	public DimensionsInt getImgDimensions() {
		return new DimensionsInt(128, 128);
	}
	
	@Override
	public void createRender() {
		this.renderManager.putRender("default", new RenderableModel3D(ModelLoader.get(GameRessources.PATH_MODEL_LVL2_CONTROLPANE))
				.withRotationOffset(0, 0, 360 - this.orientation));
	}
	
	/** Vaut true si le joueur a pressé la touche "parler" et ne l'a pas
	 * relaché. */
	private boolean talking = false;
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		
		if (getDistancef(game.getPlayer()) <= 1.1
				&& game.getInput().isKeyPressed(InputKey.KEY_TALK)
				&& !this.talking) {
			
			this.talking = true;
			if (this.activated) {
				game.getAspect(ControlPaneSystemManager.class).desactivate(this.color);
			}
		}
		else if (this.talking && !game.getInput().isKeyPressed(InputKey.KEY_TALK)) {
			this.talking = false;
		}
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		
		out.writeFloatField(this.orientation, 200);
		BarrierHandle.writeBarrierColor(out, this.color, 201);
	}
	
	@Override
	public ControlPane decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		
		setOrientation(in.readFloatField(200));
		this.color = BarrierHandle.readBarrierColor(in, 201);
		
		return this;
	}
}
