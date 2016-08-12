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

package org.terramagnetica.game.lvldefault.lvl2;

import java.awt.Image;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.CaseEntity;
import org.terramagnetica.game.lvldefault.GamePlayingDefault;
import org.terramagnetica.game.lvldefault.rendering.RenderEntity;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityDefault;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityDefaultAnimation;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityNothing;
import org.terramagnetica.opengl.engine.AnimatedTexture;
import org.terramagnetica.opengl.engine.Texture;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.opengl.miscellaneous.AnimationManager;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.TexturesLoader;
import org.terramagnetica.ressources.io.BufferedObjectInputStream;
import org.terramagnetica.ressources.io.BufferedObjectOutputStream;
import org.terramagnetica.ressources.io.GameIOException;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.Vec2i;
import net.bynaryscode.util.maths.geometric.DimensionsInt;

public class Portal extends CaseEntity implements BarrierStateListener {

	private static final long serialVersionUID = 1L;
	
	/** {@code true} si le portail est activé. La barrière correspondante
	 * est donc désactivée dans ce cas. */
	private boolean state = false;
	
	private Color4f color;
	
	public Portal() {
		this(new Color4f());
	}
	
	public Portal(Color4f color) {
		this.color = color;
		if (isColorNeutral()) {
			this.state = true;
			this.updateRender = true;
		}
	}
	
	@Override
	public void setState(boolean state) {
		if (this.isColorNeutral()) return;
		this.state = !state;
		this.updateRender = true;
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
	
	/**
	 * {@code true} si une mise à jour du rendu est nécessaire.
	 */
	private boolean updateRender = false;
	private Texture texture = TexturesLoader.getQuad(GameRessources.PATH_LVL2_TEXTURES + GameRessources.TEX_PORTAL_OFF);
	
	/**
	 * Définit la texture de cet objet et raffraichi le rendu.
	 * @param tex - La nouvelle texture de cet objet.
	 */
	public void setRender(Texture tex) {
		if (tex instanceof AnimatedTexture) {
			System.err.println("Portal.setAnimatedRender(...) est conseillé avec les animations.");
			setAnimatedRender((AnimatedTexture) tex);
			return;
		}
		this.texture = tex;
		recreateRender();
	}
	
	private AnimationManager animater = null;
	
	/**
	 * Définit une animation comme texture du portail.
	 * @param tex
	 * @return L'objet {@link AnimationManager} qui executera
	 * l'animation.
	 * <p>Attention, l'animation n'est pas démarrée automatiquement.
	 */
	public AnimationManager setAnimatedRender(AnimatedTexture tex) {
		this.texture = tex;
		this.animater = new AnimationManager(tex);
		recreateRender();
		return this.animater;
	}
	
	@Override
	protected RenderEntity createRender() {
		if (this.texture instanceof TextureQuad) {
			return new RenderEntityDefault((TextureQuad) this.texture).setOnGround(true);
		}
		else if (this.texture instanceof AnimatedTexture) {
			RenderEntityDefaultAnimation render = new RenderEntityDefaultAnimation(this.animater);
			render.setOnGround(true);
			return render;
		}
		
		return new RenderEntityNothing();
	}

	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(256, 256);
	}
	
	@Override
	public boolean isSolid() {
		return false;
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		updateRender();
		
		if (this.state && getDistancef(game.getPlayer()) < 0.25f) {
			Vec2i c = game.getAspect(ControlPaneSystemManager.class).getCenterOfRoom1();
			game.getPlayer().setCoordonnéesCase(c.x, c.y);
		}
	}
	
	private void updateRender() {
		if (!this.updateRender) return;
		
		String id = this.state ? GameRessources.TEX_PORTAL_ON : GameRessources.TEX_PORTAL_OFF;
		this.texture = TexturesLoader.getQuad(GameRessources.PATH_LVL2_TEXTURES + id);
		this.recreateRender();
		
		this.updateRender = false;
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
