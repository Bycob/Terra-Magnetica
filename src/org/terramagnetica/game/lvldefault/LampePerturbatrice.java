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

import java.awt.Image;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.rendering.RenderCompound;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityDefault;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxCircle;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.Color4f;
import net.bynaryscode.util.maths.geometric.DimensionsInt;

/**
 * Une lampe perturbatrice est un type particulier de lampe.
 * <ul><li>Lorsqu'elle est jaune, l'aimant tourne autour d'elle, plus
 * ou moins vite, mais ne change pas de sens.
 * <li>Lorsqu'elle est rouge, l'aimant tourne toujours, mais effectue
 * des oscillations violentes.
 * </ul>
 * <p>Dans le jeu elle est appelée : "lampe magnétique à champs".
 * @author Louis JEAN
 *
 */
public class LampePerturbatrice extends AbstractLamp implements InfluenceMagnetiqueMajeure {
	
	private static final long serialVersionUID = 1L;
	
	private static final float FORCE = 40;
	private static final float DEFAULT_PERIOD = 1000;
	
	//Variables d'influence
	/** La période en ms */
	private float period = DEFAULT_PERIOD;
	private float normalMoveOrigin = 0;
	private float normalMoveOffset = 0;
	
	//RENDUS
	private RenderEntityDefault renderMainIndicator;
	private float indicatorDirection = 0;

	public LampePerturbatrice() {
		super();
	}
	
	public LampePerturbatrice(int x, int y) {
		super(x, y);
	}
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(GameRessources.ID_LAMP_PERTURBATRICE_OFF);
	}
	
	@Override
	public TextureQuad getMinimapIcon() {
		return TexturesLoader.getQuad(GameRessources.ID_MAP_LAMP_RANDOM);
	}
	
	@Override
	protected RenderCompound createRender() {
		RenderCompound render = new RenderCompound();
		
		//image principale
		String id = this.state ? GameRessources.ID_LAMP_PERTURBATRICE_ON : GameRessources.ID_LAMP_PERTURBATRICE_OFF;
		render.addRender(new RenderEntityDefault(id));
		
		//indicateurs de polarité
		Color4f color = this.state ? new Color4f(1f, 0f, 0f) : new Color4f(1f, 1f, 0f);
		
		render.addRender(this.renderMainIndicator = new RenderEntityDefault(GameRessources.ID_MAGNETIC_FIELD_INDICATOR)
				.withElevation(0.25f)
				.withColor(color));
		this.updateIndicatorTranslation();
		
		return render;
	}
	
	/** Met à jour la position du rendu de l'indicateur de direction
	 * de la lampe  */
	private void updateIndicatorTranslation() {
		float dir = indicatorDirection;
		float tX = (float) Math.cos(dir) * 0.7f;
		float tY = (float) Math.sin(dir) * 0.5f;
		
		if (this.renderMainIndicator != null) this.renderMainIndicator.setTranslation(tX, tY);
	}
	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(192, 192);
	}
	
	@Override
	public DimensionsInt getImgDimensions() {
		return new DimensionsInt(256, 256);
	}
	
	@Override
	public Hitbox createHitbox() {
		return new HitboxCircle(this.getDimensionsf().getHeight() / 2);
	}
	
	@Override
	public void controls(GamePlayingDefault game, long delta, EntityMoving controlled) {
		updateLogic(delta, game);
		
		
		
		if (this.state) {
			
		}
	}
	
	@Override
	public boolean hasPermissionForCollision(EntityMoving e) {
		return true;
	}
	
	@Override
	public boolean isAvailableFor(EntityMoving e) {
		return true;
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		if (this.updated) return;
		this.updated = true;
		
		super.updateLogic(dT, game);
		
		//mise à jour de l'état
		if (this.didStateChanged) {
			if (this.state) {
				this.normalMoveOrigin = game.getTime() / 1000f - this.normalMoveOffset;
			}
			else {
				this.normalMoveOffset = (game.getTime() / 1000f - this.normalMoveOrigin) % this.period;
			}
		}
		
		//mise à jour du rendu
		if (this.didStateChanged) {
			recreateRender();
		}
		updateIndicatorTranslation();
	}
}
