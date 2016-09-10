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
import org.terramagnetica.game.lvldefault.CaseEntity;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityTexture;
import org.terramagnetica.opengl.engine.Texture;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.TexturesLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.DimensionsInt;
import net.bynaryscode.util.maths.geometric.Vec2i;

public class Portal extends CaseEntity implements BarrierStateListener {

	private static final long serialVersionUID = 1L;
	
	/** {@code true} si le portail est activé. La barrière correspondante
	 * est donc désactivée dans ce cas. */
	private boolean state = false;
	
	private Color4f color;
	
	public static final String PORTAL_OFF = "off";
	public static final String PORTAL_OPENING = "opening";
	public static final String PORTAL_ON = "on";
	
	private Texture texture = TexturesLoader.getQuad(GameRessources.PATH_LVL2_TEXTURES + GameRessources.TEX_PORTAL_OFF);
	
	public Portal() {
		this(new Color4f());
	}
	
	public Portal(Color4f color) {
		this.color = color;
		if (isColorNeutral()) {
			this.setState(true);
		}
		
		this.hitbox.setSolid(false);
	}
	
	@Override
	public void setState(boolean state) {
		this.state = !state;
		if (this.createdRenderManager) this.renderManager.render(this.state ? PORTAL_ON : PORTAL_OFF);
	}
	
	@Override
	public Color4f getColor() {
		return this.color;
	}
	
	/**
	 * Indique si la couleur du portail est la couleur neutre (par
	 * convention, le blanc). Si oui alors celui-ci ne dépend d'aucun
	 * {@link BarrierHandle} et sera toujours activé.
	 * @return
	 */
	protected boolean isColorNeutral() {
		return new Color4f().equals(this.color);
	}
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(GameRessources.PATH_LVL2_TEXTURES + GameRessources.TEX_PORTAL_ON);
	}
	
	@Override
	protected void createRender() {
		this.renderManager.putRender(PORTAL_OFF, new RenderEntityTexture(GameRessources.PATH_LVL2_TEXTURES + GameRessources.TEX_PORTAL_OFF).setOnGround(true));
		this.renderManager.putRender(PORTAL_ON, new RenderEntityTexture(GameRessources.PATH_LVL2_TEXTURES + GameRessources.TEX_PORTAL_ON).setOnGround(true));
		this.renderManager.putRender(PORTAL_OPENING, new RenderEntityTexture(GameRessources.PATH_ANIM002_OPENING_PORTAL).setOnGround(true));
	}

	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(256, 256);
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		if (this.state && getDistancef(game.getPlayer()) < 0.25f) {
			Vec2i c = game.getAspect(ControlPaneSystemManager.class).getCenterOfRoom1();
			game.getPlayer().setCasePosition(c.x, c.y);
		}
	}
	
	@Override
	public void code(BufferedObjectOutputStream out) throws GameIOException {
		super.code(out);
		
		BarrierHandle.writeBarrierColor(out, this.color, 200);
	}
	
	@Override
	public Portal decode(BufferedObjectInputStream in) throws GameIOException {
		super.decode(in);
		
		this.color = BarrierHandle.readBarrierColor(in, 200);
		
		return this;
	}
}
