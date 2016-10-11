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

package org.terramagnetica.game.lvldefault;

import static org.terramagnetica.game.GameRessources.*;

import java.awt.Image;
import java.util.List;

import org.terramagnetica.game.GameRessources;
import org.terramagnetica.game.lvldefault.rendering.RenderEntityTexture;
import org.terramagnetica.opengl.engine.TextureQuad;
import org.terramagnetica.physics.Hitbox;
import org.terramagnetica.physics.HitboxCircle;
import org.terramagnetica.ressources.ImagesLoader;
import org.terramagnetica.ressources.TexturesLoader;

import net.bynaryscode.util.maths.geometric.DimensionsInt;

public class Aimant extends EntityMoving {
	
	private static final long serialVersionUID = 1L;
	
	private transient InfluenceMagnetiqueMajeure influence = null;
	
	public Aimant() {
		super();
	}
	
	public Aimant(int x, int y) {
		super(x,y);
	}
	
	
	@Override
	public Image getImage() {
		return ImagesLoader.get(PATH_COMPOSANTS + TEX_CRYSTAL);
	}
	
	@Override
	public TextureQuad getMinimapIcon() {
		return TexturesLoader.getQuad(GameRessources.ID_MAP_CRYSTAL);
	}
	
	@Override
	public void createRender() {
		this.renderManager.putRender("default", new RenderEntityTexture(PATH_COMPOSANTS + TEX_CRYSTAL, (float) Math.PI * (45f / 180f)).withPositionOffset(0, 0.001f, 0));
		this.renderManager.putRender("dangerous", new RenderEntityTexture(TexturesLoader.getAnimatedTexture(GameRessources.PATH_ANIM000_DANGEROUS_CRYSTAL)));
		updateRenderingDanger();
	}
	
	/**
	 * Active ou desactive l'animation du "cristal dangereux", selon la
	 * vitesse du cristal.
	 */
	private void updateRenderingDanger() {
		if (!this.createdRenderManager) {
			return;
		}
		
		if (this.lastHitbox.getSpeedLength() > PlayerDefault.CRYSTAL_KILL) {
			this.renderManager.setEffect("dangerous", true);
		}
		else {
			this.renderManager.setEffect("dangerous", false);
		}
	}

	
	@Override
	public DimensionsInt getDimensions() {
		return new DimensionsInt(128, 128);
	}
	
	@Override
	public Hitbox createHitbox() {
		return new HitboxCircle(0.25f);
	}
	
	@Override
	public DimensionsInt getImgDimensions(){
		return new DimensionsInt(128, 192);
	}
	
	@Override
	public void updateLogic(long dT, GamePlayingDefault game) {
		List<Entity> entityList = game.getEntities();
		
		/* Détermine l'influence magnetique la plus proche. Elle exerce
		 * ensuite son influence sur cet aimant. */
		this.influence = null;
		for (Entity entity : entityList) {
			
			if (getDistancef(entity) <= MAX_DISTANCE
					&& entity instanceof InfluenceMagnetiqueMajeure
					&& ((InfluenceMagnetiqueMajeure) entity).isAvailableFor(this)) {
				
				if (this.influence == null) {
					this.influence = (InfluenceMagnetiqueMajeure) entity;
				}
				else if (getDistance((Entity) this.influence) > getDistance(entity)) {
					this.influence = (InfluenceMagnetiqueMajeure) entity;
				}
			}
		}
		
		if (this.influence != null) {
			this.hitbox.setSolid(this.influence.hasPermissionForCollision(this));
			this.influence.controls(game, dT, this);
		}
		
		//mise à jour du rendu
		updateRenderingDanger();
		
		//appel de la méthode classique.
		super.updateLogic(dT, game);
	}
	
	@Override
	public void onEntityCollision(long dT, GamePlayingDefault game, Entity entity) {
		
		if (entity != this.influence && entity != this) {
			if (entity instanceof PlayerDefault) {
				if (this.lastHitbox.getSpeedLength() > PlayerDefault.CRYSTAL_KILL) game.getPlayer().attack(1, game);
			}
		}
	}
	
	@Override
	public void push(float force, float direction, GamePlayingDefault game, Entity pusher) {
		
		if (this.influence != null && !this.influence.hasPermissionForCollision(this)) {
			return;
		}
		
		super.push(force, direction, game, pusher);
	}
}
